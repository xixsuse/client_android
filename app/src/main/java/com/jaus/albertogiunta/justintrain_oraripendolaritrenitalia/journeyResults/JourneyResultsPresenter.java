package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import com.google.gson.Gson;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.TrainHeader;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsContract.View.JourneySearchStrategy.OnJourneySearchFinishedListener;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils;

import org.joda.time.DateTime;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_TIME;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils.isThisJourneyPreferred;

class JourneyResultsPresenter implements JourneyResultsContract.Presenter, OnJourneySearchFinishedListener {

    private JourneyResultsActivity view;

    private static List<Journey.Solution> journeySolutions;

    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;

    JourneyResultsPresenter(JourneyResultsActivity view) {
        this.view = view;
        journeySolutions = new LinkedList<>();
        this.dateTime = DateTime.now().plusHours(2);
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void setState(Bundle bundle) {
        if (bundle != null) {
            // Restore value of members from saved state
            if (bundle.getString(I_STATIONS) != null) {
                PreferredJourney journey = new Gson().fromJson(bundle.getString(I_STATIONS), PreferredJourney.class);
                this.departureStation = journey.getStation1();
                this.arrivalStation = journey.getStation2();
                Log.d("Current bundled stations are: ", this.departureStation.toString(), this.arrivalStation.toString());
                view.setStationNames(departureStation.getNameLong(), arrivalStation.getNameLong());
                setFavouriteButtonStatus();
            }
            this.dateTime = new DateTime(bundle.getLong(I_TIME, DateTime.now().getMillis()));
            Log.d("Current bundled DateTime is: ", dateTime);
        } else {
            Log.d("no bundle found");
        }
    }

    @Override
    public Bundle getState(Bundle bundle) {
        if (bundle == null) bundle = new Bundle();
        bundle.putString(I_STATIONS, new Gson().toJson(new PreferredJourney(departureStation, arrivalStation)));
        bundle.putLong(I_TIME, dateTime.getMillis());
        return bundle;
    }

    @Override
    public void setFavouriteButtonStatus() {
        if (isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
            view.setFavouriteButtonStatus(true);
        } else {
            view.setFavouriteButtonStatus(false);
        }
    }

    @Override
    public void onLoadMoreItemsBefore() {
        new SearchBeforeTimeStrategy().searchJourney(false, departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(0).getDepartureTime().minusSeconds(1), false, false, this);
    }

//    @Override
//    public void searchInstantaneously() {
//        Log.d("searchInstantaneously: ");
//        view.showProgress();
//        new SearchInstantlyStrategy().searchJourney(true, departureStation.getStationShortId(),
//                arrivalStation.getStationShortId(),
//                null, true, true, this);
//    }

    @Override
    public void searchFromSearch(boolean isNewSearch) {
        view.showProgress();
        // TODO controlla connessione
        if (isInstant(dateTime)) {
            Log.d("Instant Search");
            new SearchInstantlyStrategy().searchJourney(isNewSearch, departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    null, true, true, this);
        } else {
            Log.d("NON Instant Search", dateTime);
            new SearchAfterTimeStrategy().searchJourney(isNewSearch, departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    dateTime, false, true, this);
        }
    }

    @Override
    public void onFavouriteButtonClick() {
        if (isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
            PreferredStationsHelper.removePreferredJourney(view.getViewContext(),
                    departureStation,
                    arrivalStation);
            setFavouriteButtonStatus();
            view.showSnackbar("Tratta rimossa dai Preferiti", INTENT_C.SNACKBAR_ACTIONS.NONE);
        } else {
            if (journeySolutions.size() > 0) {
                PreferredStationsHelper.setPreferredJourney(view.getViewContext(),
                        new PreferredJourney(departureStation, arrivalStation));
                setFavouriteButtonStatus();
                view.showSnackbar("Tratta aggiunta ai Preferiti", INTENT_C.SNACKBAR_ACTIONS.NONE);
            } else {
                view.showSnackbar("Impossibile aggiungere ai preferiti una tratta senza soluzioni", INTENT_C.SNACKBAR_ACTIONS.NONE);
            }
        }
    }

    @Override
    public void onSwapButtonClick() {
        PreferredStation temp = departureStation;
        departureStation = arrivalStation;
        arrivalStation = temp;
        searchFromSearch(true);
        view.setStationNames(departureStation.getNameLong(), arrivalStation.getNameLong());
    }

    @Override
    public void onLoadMoreItemsAfter() {
        new SearchAfterTimeStrategy().searchJourney(false, departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(journeySolutions.size() - 1).getDepartureTime().plusMinutes(1), false, false, this);
    }

    @Override
    public void onNotificationRequested(int elementIndex) {
        NotificationService.startActionStartNotification(view.getViewContext(),
                departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(elementIndex));
    }

    @Override
    public void onJourneyRefreshRequested(int elementIndex) {
        RealmResults<Station4Database> stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        if (journeySolutions.get(elementIndex).isHasChanges()) {
            for (int changeIndex = 0; changeIndex < journeySolutions.get(elementIndex).getChangesList().size(); changeIndex++) {
                String departureStationName = journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getDepartureStationName();
                String arrivalStationName = journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getArrivalStationName();
                try {
                    Station4Database tempDepartureStation = StationRealmUtils.getStation4DatabaseObject(departureStationName, stationList);
                    Station4Database tempArrivalStation = StationRealmUtils.getStation4DatabaseObject(arrivalStationName, stationList);
                    refreshChange(elementIndex, changeIndex,
                            tempDepartureStation.getStationShortId(),
                            tempArrivalStation.getStationShortId(),
                            journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getTrainId());
                } catch (Exception e) {
                    view.showSnackbar("Non riesco ad aggiornare questa soluzione", INTENT_C.SNACKBAR_ACTIONS.NONE);
                    Log.e("onJourneyRefreshRequested: ", "Non è stata trovata nessuna corrispondenza per le stazioni: ", departureStationName, arrivalStationName);
                }
            }
        } else {
            refreshChange(elementIndex, -1,
                    departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    journeySolutions.get(elementIndex).getTrainId());
        }
    }

    @Override
    public List<Journey.Solution> getSolutionList() {
        return journeySolutions;
    }

    @Override
    public void onJourneyNotFound() {
        view.showErrorMessage("La tratta inserita non ha viaggi disponibili", "Cambia stazioni", INTENT_C.ERROR_BTN.NO_SOLUTIONS);
    }

    @Override
    public void onJourneyBeforeNotFound() {
        view.showSnackbar("Sembra non ci siano altre soluzioni in giornata", INTENT_C.SNACKBAR_ACTIONS.NONE);
    }

    @Override
    public void onJourneyAfterNotFound() {
        view.showSnackbar("Sembra non ci siano altre soluzioni in giornata", INTENT_C.SNACKBAR_ACTIONS.NONE);
    }

    @Override
    public void onServerError(Throwable exception) {
        Log.d(exception.getMessage());
        if (view != null) {
            if (exception.getMessage().equals("HTTP 404 ")) {
                onJourneyNotFound();
            } else {
                Log.e("onServerError: ", exception.toString());
                if (exception instanceof HttpException) {
                    Log.d(((HttpException) exception).response().errorBody(), ((HttpException) exception).response().code());
                    if (((HttpException) exception).response().code() == 500) {
                        view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_C.ERROR_BTN.SEND_REPORT);
                    }
                } else if (exception instanceof ConnectException) {
                    if (isNetworkAvailable()) {
                        view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_C.ERROR_BTN.SEND_REPORT);
                    } else {
                        view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_C.ERROR_BTN.CONN_SETTINGS);
                    }
                } else if (exception instanceof SocketException) {
                    view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_C.ERROR_BTN.CONN_SETTINGS);
                }
            }
        }
    }

    @Override
    public void onSuccess() {
        view.hideProgress();
        view.updateSolutionsList(journeySolutions);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void refreshChange(int solutionIndex, int changeIndex, String departureStationId, String arrivalStationId, String trainId) {
        Log.d("refreshChange:", solutionIndex, changeIndex, trainId);
        try {
            Integer.parseInt(departureStationId);
            Integer.parseInt(arrivalStationId);
            Integer.parseInt(trainId);
        } catch (NumberFormatException e) {
            view.showSnackbar("Premere di nuovo il pulsante aggiorna", INTENT_C.SNACKBAR_ACTIONS.NONE);
            Log.e("refreshChange: ", "There are problems with these parameters:", departureStationId, arrivalStationId, trainId);
            view.updateSolution(solutionIndex);
            view.updateSolution(solutionIndex);
            return;
        }

        ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT)
                .getDelay(departureStationId,
                        arrivalStationId,
                        trainId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrainHeader>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.getMessage());
                    }

                    @Override
                    public void onNext(TrainHeader trainHeader) {
                        Log.d("onNext:", trainHeader.toString());

                        if (journeySolutions.get(solutionIndex).isHasChanges()) {
                            journeySolutions.get(solutionIndex).getChangesList().get(changeIndex).setDeparturePlatform(trainHeader.getDeparturePlatform());
                            if (trainHeader.isDeparted()) {
                                journeySolutions.get(solutionIndex).getChangesList().get(changeIndex).setTimeDifference(trainHeader.getTimeDifference());
                                journeySolutions.get(solutionIndex).getChangesList().get(changeIndex).setProgress(trainHeader.getProgress());
                                journeySolutions.get(solutionIndex).getChangesList().get(changeIndex).postProcess();
                            } else if (journeySolutions.get(solutionIndex).getTimeDifference() == null) {
                                view.showSnackbar("Il treno "
                                        + trainHeader.getTrainCategory()
                                        + " "
                                        + trainHeader.getTrainId()
                                        + " non è ancora partito", INTENT_C.SNACKBAR_ACTIONS.NONE);
                            }
                        } else {
                            journeySolutions.get(solutionIndex).setDeparturePlatform(trainHeader.getDeparturePlatform());
                            if (trainHeader.isDeparted()) {
                                journeySolutions.get(solutionIndex).setTimeDifference(trainHeader.getTimeDifference());
                                journeySolutions.get(solutionIndex).setProgress(trainHeader.getProgress());
                            } else {
                                view.showSnackbar("Il treno non è ancora partito", INTENT_C.SNACKBAR_ACTIONS.NONE);
                            }
                        }
                        journeySolutions.get(solutionIndex).refreshData();

                        if (!journeySolutions.get(solutionIndex).isHasChanges() || (journeySolutions.get(solutionIndex).isHasChanges() && changeIndex == journeySolutions.get(solutionIndex).getChangesList().size() - 1)) {
                            view.updateSolution(solutionIndex);
                        }
                    }
                });
    }

    private boolean isInstant(DateTime selectedHour) {
        return selectedHour.getHourOfDay() == DateTime.now().getHourOfDay() &&
                selectedHour.getMinuteOfHour() == DateTime.now().getMinuteOfHour();
    }

//    private String sanitizeStationName(String dirtyName) {
//        return dirtyName
//                .replaceAll("Bologna Centrale", "Bologna C.LE")
//                .replaceAll("TO Lingotto", "Torino Lingotto")
//                .replaceAll("Torino P. Susa", "Torino Porta Susa")
//                .replaceAll("Verona P.Nuova", "Verona Porta Nuova");
//    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) view.getViewContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static class SearchInstantlyStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(boolean isNewSearch, String departureStationId, String arrivalStationId, DateTime timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            Log.d(departureStationId, arrivalStationId, timestamp, isPreemptive, withDelays);
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyInstant(departureStationId, arrivalStationId, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Journey>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onServerError(e);
                        }

                        @Override
                        public void onNext(Journey solutionList) {
                            journeySolutions.clear();
                            journeySolutions.addAll(solutionList.getSolutions());
                            if (journeySolutions.size() > 0) {
                                listener.onSuccess();
                            } else {
                                listener.onJourneyNotFound();
                            }
                        }
                    });
        }
    }

    private static class SearchAfterTimeStrategy implements JourneyResultsContract.View.JourneySearchStrategy {
        @Override
        public void searchJourney(boolean isNewSearch, String departureStationId, String arrivalStationId, DateTime timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT)
                    .getJourneyAfterTime(departureStationId, arrivalStationId, timestamp.toString("yyyy-MM-dd'T'HH:mmZ"), withDelays, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Journey>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onServerError(e);
                        }

                        @Override
                        public void onNext(Journey solutionList) {
                            Log.d(solutionList.getSolutions().size(), "new solutions found");
                            if (isNewSearch) {
                                journeySolutions.clear();
                            }
                            journeySolutions.addAll(solutionList.getSolutions());
                            if (journeySolutions.size() > 0) {
                                listener.onSuccess();
                            } else {
                                listener.onJourneyAfterNotFound();
                            }
                        }
                    });
        }


    }

    private static class SearchBeforeTimeStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(boolean isNewSearch, String departureStationId, String arrivalStationId, DateTime timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            Log.d(timestamp);
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyBeforeTime(departureStationId, arrivalStationId, timestamp.toString("yyyy-MM-dd'T'HH:mmZ"), withDelays, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Journey>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onServerError(e);
                            Log.d(e.getMessage());
                        }

                        @Override
                        public void onNext(Journey solutionList) {
                            Log.d(solutionList.getSolutions().size(), "new solutions found");
                            if (solutionList.getSolutions().size() > 0) {
                                journeySolutions.addAll(0, solutionList.getSolutions());
                                listener.onSuccess();
                            } else {
                                listener.onJourneyBeforeNotFound();
                            }
                        }
                    });
        }
    }
}
