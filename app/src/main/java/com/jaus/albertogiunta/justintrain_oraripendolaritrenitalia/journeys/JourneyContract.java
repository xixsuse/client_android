package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import java.util.List;

/**
 * Created by albertogiunta on 24/05/16.
 */
public interface JourneyContract {

    interface Search {

        interface View extends BaseView<Presenter> {

            void showArrivalStationNameError(String error);

            void showDepartureStationNameError(String error);

            void setTime(String time);

        }

        interface Presenter extends BasePresenter {

            List<String> getLastSearchedStations();

            List<String> searchDbForMatchingStation(String constraint);

            boolean search(String departureStationName, String arrivalStationName, int hourOfDay);

            List<Station4Database> getSearchedStations();

            int getHourOfDay();

            void changeTime(int delta);

        }
    }

    interface Results {

        interface View extends BaseView<Presenter> {

            void showProgress();

            void hideProgress();

            void setJourneySolutions(List<SolutionList.Solution> solutionList);

            void showError(String message);

        }

        interface Presenter extends BasePresenter {

            String getDepartureStationId();

            String getArrivalStationId();

            int getHourOfDay();

            List<SolutionList.Solution> getSolutionList();

            void searchJourney();

            interface Interactor {
                interface OnJourneySearchFinishedListener {

                    void onStationNotFound();

                    void onJourneyNotFound();

                    void onServerError();

                    void onSuccess();

                }

                void searchJourney(String departureStationId, String arrivalStationId, int time, OnJourneySearchFinishedListener listener);
            }
        }
    }
}
