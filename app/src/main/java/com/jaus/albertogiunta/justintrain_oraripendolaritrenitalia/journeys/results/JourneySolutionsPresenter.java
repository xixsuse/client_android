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
class
JourneySolutionsPresenter implements JourneyContract.Results.Presenter, JourneyContract.Results.Presenter.Interactor.OnJourneySearchFinishedListener {

    private JourneyContract.Results.View mJourneyResultsView;
    private JourneyContract.Results.Presenter.Interactor mInteractor;
    private String mDepartureStationId;
    private String mArrivalStationId;
    private int mHourOfDay;
    private List<SolutionList.Solution> mSolutionList;

    public JourneySolutionsPresenter(JourneyContract.Results.View journeyResultsView, String departureStationId, String arrivalStationId, int hourOfDay) {
        this.mJourneyResultsView = journeyResultsView;
        this.mInteractor = new SearchJourneyInteractor();
        this.mSolutionList = new LinkedList<>();
        this.mDepartureStationId = departureStationId;
        this.mArrivalStationId = arrivalStationId;
        this.mHourOfDay = hourOfDay;
    }

    @Override
    public String getDepartureStationId() {
        return mDepartureStationId;
    }

    @Override
    public String getArrivalStationId() {
        return mArrivalStationId;
    }

    public int getHourOfDay() {
        return mHourOfDay;
    }

    @Override
    public List<SolutionList.Solution>getSolutionList() {
        return mSolutionList;
    }

    @Override
    public void searchJourney() {
        mJourneyResultsView.showProgress();
        mInteractor.searchJourney(mDepartureStationId, mArrivalStationId, mHourOfDay, this);
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
        Log.d("Size of results: ", mSolutionList.size());
        mJourneyResultsView.hideProgress();
        mJourneyResultsView.setJourneySolutions(mSolutionList);
    }


    public class SearchJourneyInteractor implements JourneyContract.Results.Presenter.Interactor {

        @Override
        public void searchJourney(String departureStationId, String arrivalStationId, int hourOfDay, OnJourneySearchFinishedListener listener) {

            JourneyService journeyService = ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT);
            Log.d("Searching with following info: ", departureStationId, arrivalStationId, hourOfDay, DateTime.now().withHourOfDay(hourOfDay).toInstant().getMillis() / 1000);
            journeyService.getJourneySolutions(departureStationId, arrivalStationId, DateTime.now().withHourOfDay(hourOfDay).toInstant().getMillis() / 1000, true)
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
                            Log.d("Call finished, calling onSuccess");
                            listener.onSuccess();
                        }
                    });
        }
    }
}
