package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

/**
 * Created by albertogiunta on 17/06/16.
 */
public class JourneySolutionsPresenter implements JourneyContract.Results.Presenter,
        JourneyContract.Results.Presenter.JourneySearchStrategy.OnJourneySearchFinishedListener {

    private JourneyContract.Results.View mJourneyResultsView;
    private JourneyContract.Results.Presenter.JourneySearchStrategy strategy;
    private String mDepartureStationId;
    private String mArrivalStationId;
    private int mHourOfDay;
    private static List<SolutionList.Solution> mSolutionList;

    public JourneySolutionsPresenter(JourneyContract.Results.View journeyResultsView) {
        mJourneyResultsView = journeyResultsView;
        mSolutionList = new LinkedList<>();
    }

    @Override
    public List<SolutionList.Solution> getSolutionList() {
        return mSolutionList;
    }

    @Override
    public void searchJourney(JourneySearchStrategy strategy, String departureStationId, String arrivalStationId, int hourOfDay, boolean isPreemptive, boolean withDelays) {
        long timestamp = DateTime.now().withHourOfDay(hourOfDay).toInstant().getMillis() / 1000;
        this.searchJourney(strategy, departureStationId, arrivalStationId, timestamp, isPreemptive, withDelays);
    }

    @Override
    public void searchJourney(
            JourneySearchStrategy strategy, String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays) {
        mJourneyResultsView.showProgress();
        this.setStrategy(strategy);
        this.strategy.searchJourney(departureStationId, arrivalStationId, timestamp, isPreemptive, withDelays, this);
    }

    @Override
    public void onStationNotFound() {
        mJourneyResultsView.hideProgress();
        mJourneyResultsView.showError("station not found");
    }

    @Override
    public void onJourneyNotFound() {
        mJourneyResultsView.hideProgress();
        mJourneyResultsView.showError("journey not found");
    }

    @Override
    public void onServerError() {
        mJourneyResultsView.hideProgress();
        mJourneyResultsView.showError("server error");
    }

    @Override
    public void onSuccess() {
        // TODO what if size == 0 -> call onJourneyNotFound
        Log.d("Size of results: ", mSolutionList.size());
        mJourneyResultsView.hideProgress();
        mJourneyResultsView.setRvJourneySolutions(mSolutionList);
    }

    @Override
    public String getDepartureStationId() {
        return mDepartureStationId;
    }

    @Override
    public String getArrivalStationId() {
        return mArrivalStationId;
    }

    @Override
    public int getHourOfDay() {
        return mHourOfDay;
    }

    private void setStrategy(JourneySearchStrategy strategy) {
        this.strategy = strategy;
    }


    /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// /// ///


    public static class SearchInstantlyStrategy implements JourneySearchStrategy {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
            ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT).getJourneyInstant(departureStationId, arrivalStationId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SolutionList.Solution>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(e.getMessage());
                            listener.onJourneyNotFound();
                            listener.onServerError();
                            listener.onStationNotFound();
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            mSolutionList.clear();
                            mSolutionList.addAll(solutionList);
                            listener.onSuccess();
                        }
                    });
        }
    }

    public static class SearchAfterTimeStrategy implements JourneySearchStrategy {

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
                            listener.onJourneyNotFound();
                            listener.onServerError();
                            listener.onStationNotFound();
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            mSolutionList.addAll(solutionList);
                            listener.onSuccess();
                        }
                    });
        }
    }

    public static class SearchBeforeTimeStrategy implements JourneySearchStrategy {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, long timestamp, boolean isPreemptive, boolean withDelays, OnJourneySearchFinishedListener listener) {
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
                            listener.onJourneyNotFound();
                            listener.onServerError();
                            listener.onStationNotFound();
                        }

                        @Override
                        public void onNext(List<SolutionList.Solution> solutionList) {
                            solutionList.addAll(mSolutionList);
                            mSolutionList.clear();
                            mSolutionList.addAll(solutionList);
                            listener.onSuccess();
                        }
                    });
        }
    }
}