package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import com.google.gson.Gson;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.TrainHeader;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsContract.View.JourneySearchStrategy.OnJourneySearchFinishedListener;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.APINetworkingFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.DatabaseHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.NetworkingHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.NotificationDataHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.PreferredStationsHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.ServerConfigsHelper;

import org.joda.time.DateTime;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ERROR_NOT_FOUND_JOURNEY_AFTER;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ERROR_NOT_FOUND_JOURNEY_BEFORE;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ERROR_NOT_FOUND_SOLUTION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.SCREEN_JOURNEY_RESULTS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST.I_TIME;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.DatabaseHelper.isThisJourneyPreferred;

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
    public PreferredJourney getPreferredJourney() {
        return new PreferredJourney(departureStation, arrivalStation);
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
            view.showSnackbar("Tratta rimossa dai Preferiti", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
        } else {
//            if (journeySolutions.size() > 0) {
                PreferredStationsHelper.setPreferredJourney(view.getViewContext(),
                        new PreferredJourney(departureStation, arrivalStation));
                setFavouriteButtonStatus();
            view.showSnackbar("Tratta aggiunta ai Preferiti", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
//            } else {
//                view.showSnackbar("Impossibile aggiungere ai preferiti una tratta senza soluzioni", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
//            }
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
        NotificationDataHelper.setNotificationData(view.getViewContext(), new PreferredJourney(departureStation, arrivalStation), journeySolutions.get(elementIndex));
        NotificationService.startActionStartNotification(view.getViewContext(),
                departureStation,
                arrivalStation,
                journeySolutions.get(elementIndex),
                journeySolutions.get(elementIndex).hasChanges() ? 0 : null);
    }

    @Override
    public void onJourneyRefreshRequested(int elementIndex) {
        RealmResults<Station4Database> stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        Map<Pair<String, String>, String> m = new HashMap<>();
        if (journeySolutions.get(elementIndex).hasChanges()) {
            for (int changeIndex = 0; changeIndex < journeySolutions.get(elementIndex).getChangesList().size(); changeIndex++) {
                try {
                    String departureStationName = journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getDepartureStationName();
                    String arrivalStationName = journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getArrivalStationName();
                    Station4Database tempDepartureStation = DatabaseHelper.getStation4DatabaseObject(departureStationName, stationList);
                    Station4Database tempArrivalStation = DatabaseHelper.getStation4DatabaseObject(arrivalStationName, stationList);

                    m.put(new Pair<>(tempDepartureStation.getStationShortId(), tempArrivalStation.getStationShortId()), journeySolutions.get(elementIndex).getChangesList().get(changeIndex).getTrainId());
                } catch (Exception e) {
                    Analytics.getInstance(view.getViewContext()).logScreenEvent(SCREEN_JOURNEY_RESULTS, ERROR_NOT_FOUND_SOLUTION);
                    view.showSnackbar("Non riesco ad aggiornare questa soluzione", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                }
            }
        } else {
            m.put(new Pair<>(departureStation.getStationShortId(), arrivalStation.getStationShortId()), journeySolutions.get(elementIndex).getTrainId());
        }
        Observable.concatDelayError(refreshChange(m)).subscribe(new Subscriber<TrainHeader>() {
            @Override
            public void onCompleted() {
                journeySolutions.get(elementIndex).refreshData();
                view.updateSolution(elementIndex);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("HTTP 404 ")) {
                    Analytics.getInstance(view.getViewContext()).logScreenEvent(SCREEN_JOURNEY_RESULTS, ERROR_NOT_FOUND_SOLUTION);
                    view.showSnackbar("Il treno potrebbe avere cambiato codice", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                }
            }

            @Override
            public void onNext(TrainHeader trainHeader) {
                Integer changeIndex = null;
                Journey.Solution sol = journeySolutions.get(elementIndex);

                if (sol.hasChanges()) {
                    List<Journey.Solution.Change> changes = sol.getChangesList();
                    for (Journey.Solution.Change c : changes) {
                        if (trainHeader.getTrainId().equalsIgnoreCase(c.getTrainId()))
                            changeIndex = changes.indexOf(c);
                    }
                    if (changeIndex != null) {
                        changes.get(changeIndex).setDeparturePlatform(trainHeader.getDeparturePlatform());
                        if (trainHeader.isDeparted()) {
                            changes.get(changeIndex).setTimeDifference(trainHeader.getTimeDifference());
                            changes.get(changeIndex).setProgress(trainHeader.getProgress());
                            changes.get(changeIndex).postProcess();
                        } else if (sol.getTimeDifference() == null) {
                            view.showSnackbar("Il treno "
                                    + trainHeader.getTrainCategory()
                                    + " "
                                    + trainHeader.getTrainId()
                                    + " non è ancora partito", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                        }
                    }
                } else {
                    sol.setDeparturePlatform(trainHeader.getDeparturePlatform());
                    if (trainHeader.isDeparted()) {
                        sol.setTimeDifference(trainHeader.getTimeDifference());
                        sol.setProgress(trainHeader.getProgress());
                    } else {
                        view.showSnackbar("Il treno non è ancora partito", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                    }
                }

            }
        });
    }

    @Override
    public List<Journey.Solution> getSolutionList() {
        return journeySolutions;
    }

    @Override
    public void onJourneyNotFound() {
        view.showErrorMessage("La tratta inserita non ha viaggi disponibili", "Cambia stazioni", INTENT_CONST.ERROR_BTN.NO_SOLUTIONS);
    }

    @Override
    public void onJourneyBeforeNotFound() {
        Analytics.getInstance(view.getViewContext()).logScreenEvent(SCREEN_JOURNEY_RESULTS, ERROR_NOT_FOUND_JOURNEY_BEFORE);
        view.showSnackbar("Sembra non ci siano altre soluzioni in giornata", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
    }

    @Override
    public void onJourneyAfterNotFound() {
        Analytics.getInstance(view.getViewContext()).logScreenEvent(SCREEN_JOURNEY_RESULTS, ERROR_NOT_FOUND_JOURNEY_AFTER);
        view.showSnackbar("Sembra non ci siano altre soluzioni in giornata", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
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
                        view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_CONST.ERROR_BTN.SEND_REPORT);
                    }
                } else if (exception instanceof ConnectException) {
                    if (NetworkingHelper.isNetworkAvailable(view.getViewContext())) {
                        view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_CONST.ERROR_BTN.SEND_REPORT);
                    } else {
                        view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_CONST.ERROR_BTN.CONN_SETTINGS);
                    }
                } else if (exception instanceof SocketException) {
                    view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_CONST.ERROR_BTN.CONN_SETTINGS);
                }
            }
        }
    }

    @Override
    public void onSuccess() {
        view.hideProgress();
        view.updateSolutionsList();
    }

    @Override
    public Context getViewContext() {
        return view.getViewContext();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private List<Observable<TrainHeader>> refreshChange(Map<Pair<String, String>, String> info) {
        List<Observable<TrainHeader>> o = new LinkedList<>();
        for (Pair<String, String> p : info.keySet()) {
            Log.d("refreshChange: trainId", info.get(p));
            try {
                Integer.parseInt(p.first);
                Integer.parseInt(p.second);
                Integer.parseInt(info.get(p));
            } catch (NumberFormatException e) {
                view.showSnackbar("Problema di aggiornamento", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                Log.e("refreshChange: ", "There are problems with these parameters:", p.first, p.second, info.get(p));
                continue;
            }

            o.add(APINetworkingFactory.createRetrofitService(JourneyService.class, ServerConfigsHelper.getAPIEndpoint(view.getViewContext()))
                    .getDelay(p.first,
                            p.second,
                            info.get(p))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
        }
        return o;
    }


    private boolean isInstant(DateTime selectedHour) {
        return selectedHour.getHourOfDay() == DateTime.now().getHourOfDay() &&
                selectedHour.getMinuteOfHour() == DateTime.now().getMinuteOfHour();
    }

    private static class SearchInstantlyStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(boolean isNewSearch, String departureStationId, String arrivalStationId, DateTime timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            Log.d(departureStationId, arrivalStationId, timestamp, isPreemptive, withDelays);
            APINetworkingFactory.createRetrofitService(JourneyService.class, ServerConfigsHelper.getAPIEndpoint(listener.getViewContext())).getJourneyInstant(departureStationId, arrivalStationId, isPreemptive)
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
            APINetworkingFactory.createRetrofitService(JourneyService.class, ServerConfigsHelper.getAPIEndpoint(listener.getViewContext()))
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
            APINetworkingFactory.createRetrofitService(JourneyService.class, ServerConfigsHelper.getAPIEndpoint(listener.getViewContext())).getJourneyBeforeTime(departureStationId, arrivalStationId, timestamp.toString("yyyy-MM-dd'T'HH:mmZ"), withDelays, isPreemptive)
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
