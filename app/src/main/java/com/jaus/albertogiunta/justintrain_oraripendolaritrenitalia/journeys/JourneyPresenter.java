package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.os.Bundle;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.JourneyWithDelay;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.LinkedList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity.JOURNEY_PARAM;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity.SEARCH_PANEL_STATUS_PARAM;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract.View.SEARCH_PANEL_STATUS.ACTIVE;

public class JourneyPresenter implements JourneyContract.Presenter, JourneyContract.View.JourneySearchStrategy.OnJourneySearchFinishedListener {

    private static final int HOUR = LocalDateTime.now().plusHours(2).getHourOfDay();

    private JourneyContract.View view;
    private RealmResults<Station4Database> stationList;

    private static List<SolutionList.Solution> journeySolutions;

    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private int hour;

    public JourneyPresenter(JourneyContract.View view) {
        this.view = view;
        this.stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        journeySolutions = new LinkedList<>();
        this.hour = HOUR;
    }

    @Override
    public void subscribe(BaseView baseView) {
        view = (JourneyContract.View) baseView;
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void onLeaving(Bundle bundle) {
        // Save value of member in saved state
        bundle.putString(JOURNEY_PARAM, new Gson().toJson(new PreferredJourney(departureStation, arrivalStation)));
        bundle.putString(SEARCH_PANEL_STATUS_PARAM, new Gson().toJson(view.getJourneySearchFragmentViewStatus()));
    }

    @Override
    public void onResuming(Bundle bundle) {
        if (bundle != null) {
            // Restore value of members from saved state
            PreferredJourney journey = new Gson().fromJson(bundle.getString(JOURNEY_PARAM), PreferredJourney.class);
            this.departureStation = journey.getStation1();
            this.arrivalStation = journey.getStation2();

            view.setJourneySearchFragmentViewStatus(new Gson()
                    .fromJson(bundle.getString(SEARCH_PANEL_STATUS_PARAM, ACTIVE.toString()), JourneyContract.View.SEARCH_PANEL_STATUS.class));

            searchFromIntent();
            view.setStationNames(departureStation.getName(), arrivalStation.getName());
        } else {
            // Probably initialize members with default values for a new instance
        }
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
    public void onTimeChanged(int delta) {
        hour += delta;
        if (delta != 0) {
            if (hour < 0) {
                hour = 23;
            } else if (hour > 24) {
                hour = 1;
            }
        }
        view.setTime((hour < 10 ? "0" + hour : Integer.toString(hour)).concat(":00"));
    }

    @Override
    public List<String> searchStationName(String stationName) {
        return Stream
                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
                .map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public void onSwapButtonClick() {
        if (departureStation != null && arrivalStation != null) {
            PreferredStation temp = departureStation;
            departureStation = arrivalStation;
            arrivalStation = temp;
            view.setStationNames(departureStation.getName(), arrivalStation.getName());
        }
    }

    @Override
    public void onSearchButtonClick(String departureStationName, String arrivalStationName) {
        boolean departureFound = false;
        boolean arrivalFound = false;

        if (PresenterUtilities.isStationNameValid(departureStationName, stationList)) {
            departureStation = new PreferredStation(PresenterUtilities.getStationObject(departureStationName, stationList));
            departureFound = true;
        } else {
            view.showDepartureStationNameError(departureStationName + " not found!");
            Log.d(arrivalStationName + " not found!");
        }

        if (PresenterUtilities.isStationNameValid(arrivalStationName, stationList)) {
            arrivalStation = new PreferredStation(PresenterUtilities.getStationObject(arrivalStationName, stationList));
            arrivalFound = true;
        } else {
            view.showArrivalStationNameError(arrivalStationName + " not found!");
            Log.d(departureStationName + " not found!");
        }

        if (departureFound && arrivalFound) {
            view.onValidSearchParameters();
            journeySolutions.clear();
            Log.d("Searching for: " + departureStation.toString(), arrivalStation.toString());
        }
    }

    @Override
    public void onLoadMoreItemsBefore() {
        new SearchBeforeTimeStrategy().searchJourney(departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(0).solution.departureTime-1, false, false, this);
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
        if (PresenterUtilities.isInstant(hour, HOUR)) {
            Log.d("Instant Search");
            new SearchInstantlyStrategy().searchJourney(departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    0, true, true, this);
        } else {
            Log.d("NON Instant Search");
            new SearchAfterTimeStrategy().searchJourney(departureStation.getStationShortId(),
                    arrivalStation.getStationShortId(),
                    DateTime.now().withHourOfDay(hour).toInstant().getMillis() / 1000, true, true, this);
        }
        view.setStationNames(departureStation.getName(), arrivalStation.getName());
    }

    @Override
    public void onLoadMoreItemsAfter() {
        new SearchAfterTimeStrategy().searchJourney(departureStation.getStationShortId(),
                arrivalStation.getStationShortId(),
                journeySolutions.get(journeySolutions.size()-1).solution.departureTime+61, false, false, this);
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
                        journeySolutions.get(elementIndex).solution.progress= journeyWithDelay.progress;
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


    public static class SearchInstantlyStrategy implements JourneyContract.View.JourneySearchStrategy {

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
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            journeySolutions.clear();
                            journeySolutions.addAll(solutionList);
                            listener.onSuccess();
                            // TODO perform checks
                        }
                    });
        }
    }

    public static class SearchAfterTimeStrategy implements JourneyContract.View.JourneySearchStrategy {

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

    public static class SearchBeforeTimeStrategy implements JourneyContract.View.JourneySearchStrategy {

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
