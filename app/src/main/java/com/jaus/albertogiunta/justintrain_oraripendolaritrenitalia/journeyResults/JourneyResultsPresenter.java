package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.JourneyWithDelay;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsContract.View.JourneySearchStrategy.OnJourneySearchFinishedListener;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch.PresenterUtilities;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_TIME;


public class JourneyResultsPresenter implements JourneyResultsContract.Presenter, OnJourneySearchFinishedListener {

    private JourneyResultsContract.View view;

    private static List<SolutionList.Solution> journeySolutions;

    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;


    public JourneyResultsPresenter(JourneyResultsContract.View view) {
        this.view = view;
        journeySolutions = new LinkedList<>();
        this.dateTime = DateTime.now().plusHours(2);
    }

    @Override
    public void subscribe(BaseView baseView) {
        view = (JourneyResultsContract.View) baseView;
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void onResuming(Bundle bundle) {
        if (bundle != null) {
            // Restore value of members from saved state
            if (bundle.getString(I_STATIONS) != null) {
                PreferredJourney journey = new Gson().fromJson(bundle.getString(I_STATIONS), PreferredJourney.class);
                this.departureStation = journey.getStation1();
                this.arrivalStation = journey.getStation2();
                this.dateTime = new DateTime(bundle.getLong(I_TIME, DateTime.now().getMillis()));
                Log.d(dateTime);
                view.setStationNames(departureStation.getName(), arrivalStation.getName());
                setFavouriteButtonStatus();
                searchFromSearch();
            }
        } else {
            // Probably initialize members with default values for a new instance
        }
    }

    @Override
    public void onLeaving(Bundle bundle) {
        bundle.putString(I_STATIONS, new Gson().toJson(new PreferredJourney(departureStation, arrivalStation)));
    }

    @Override
    public void setFavouriteButtonStatus() {
        if (PresenterUtilities.isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
            view.setFavouriteButtonStatus(true);
        } else {
            view.setFavouriteButtonStatus(false);
        }
    }

    @Override
    public void onLoadMoreItemsBefore() {
        new SearchBeforeTimeStrategy().searchJourney(departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(0).solution.departureTime - 1, false, false, this);
    }

    @Override
    public void searchFromIntent() {
        Log.d("instant");
        view.showProgress();
        new SearchInstantlyStrategy().searchJourney(departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                0, true, true, this);
    }

    @Override
    public void searchFromSearch() {
        view.showProgress();
        // TODO controlla connessione
        if (PresenterUtilities.isInstant(dateTime)) {
            Log.d("Instant Search");
            new SearchInstantlyStrategy().searchJourney(departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    0, true, true, this);
        } else {
            Log.d("NON Instant Search", dateTime);
            // aggiungo due ore ma non so bene perchè
            new SearchAfterTimeStrategy().searchJourney(departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    dateTime.plusHours(2).getMillis() / 1000, true, true, this);
        }
//        view.setStationNames(departureStation.getName(), arrivalStation.getName());
    }

    @Override
    public void onFavouriteButtonClick() {
        if (PresenterUtilities.isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
            PreferredStationsHelper.removePreferredJourney(view.getViewContext(),
                    departureStation,
                    arrivalStation);
            setFavouriteButtonStatus();
        } else {
            PreferredStationsHelper.setPreferredJourney(view.getViewContext(),
                    new PreferredJourney(departureStation, arrivalStation));
            setFavouriteButtonStatus();
        }
    }

    @Override
    public void onLoadMoreItemsAfter() {
        new SearchAfterTimeStrategy().searchJourney(departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(journeySolutions.size() - 1).solution.departureTime + 61, false, false, this);
    }

    @Override
    public void onNotificationRequested(int elementIndex) {
        NotificationService.startActionStartNotification(view.getViewContext(),
                journeySolutions.get(elementIndex).solution.departureStationName,
                journeySolutions.get(elementIndex).solution.departureTime,
                journeySolutions.get(elementIndex).solution.departureTimeReadable,
                journeySolutions.get(elementIndex).solution.arrivalStationName,
                journeySolutions.get(elementIndex).solution.arrivalTime,
                journeySolutions.get(elementIndex).solution.arrivalTimeReadable,
                journeySolutions.get(elementIndex).solution.trainId);
    }

    @Override
    public void onJourneyRefreshRequested(int elementIndex) {
        ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT)
                .getDelay(journeySolutions.get(elementIndex).solution.trainId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JourneyWithDelay>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.getMessage());
                    }

                    @Override
                    public void onNext(JourneyWithDelay journeyWithDelay) {
                        journeySolutions.get(elementIndex).solution.departurePlatform = journeyWithDelay.departurePlatform;
                        journeySolutions.get(elementIndex).solution.timeDifference = journeyWithDelay.timeDifference;
                        journeySolutions.get(elementIndex).solution.progress = journeyWithDelay.progress;
                        view.updateSolution(elementIndex);
                    }
                });
    }

    @Override
    public List<SolutionList.Solution> getSolutionList() {
        return journeySolutions;
    }

    @Override
    public void onStationNotFound() {
        view.showSnackbar("Station not found");
    }

    @Override
    public void onJourneyNotFound() {
        view.showSnackbar("Journey not found");
    }

    @Override
    public void onServerError() {
        view.showSnackbar("Server error");
    }

    @Override
    public void onSuccess() {
        view.hideProgress();
        view.updateSolutionsList(journeySolutions);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) view.getViewContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class SearchInstantlyStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            Log.d(departureStationId, arrivalStationId, timestamp, isPreemptive, withDelays);
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyInstant(departureStationId, arrivalStationId, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SolutionList.Solution>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(e.getMessage());
                            // TODO controlla 500
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            if (solutionList.size() == 0) {
                                listener.onJourneyNotFound();
                            } else {
                                journeySolutions.clear();
                                journeySolutions.addAll(solutionList);
                                listener.onSuccess();
                                // TODO perform checks
                            }
                        }
                    });
        }
    }

    public static class SearchAfterTimeStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyAfterTime(departureStationId, arrivalStationId, timestamp, withDelays, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SolutionList.Solution>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO controlla 500
                            Log.d(e.getMessage());
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            Log.d(solutionList.size(), "new solutions found");
                            journeySolutions.addAll(solutionList);
                            listener.onSuccess();
                            // TODO perform checks
                        }
                    });
        }
    }

    public static class SearchBeforeTimeStrategy implements JourneyResultsContract.View.JourneySearchStrategy {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            Log.d(timestamp);
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyBeforeTime(departureStationId, arrivalStationId, timestamp, withDelays, isPreemptive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SolutionList.Solution>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO controlla 500
                            Log.d(e.getMessage());
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            Log.d(solutionList.size(), "new solutions found");
                            solutionList.addAll(journeySolutions);
                            journeySolutions.clear();
                            journeySolutions.addAll(solutionList);
                            listener.onSuccess();
                        }
                    });
        }
    }
}
