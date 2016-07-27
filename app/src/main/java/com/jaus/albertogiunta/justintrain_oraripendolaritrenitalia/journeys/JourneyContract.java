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

            List<String> getRecentStations();

            List<String> searchDbForMatchingStation(String constraint);

            boolean search(String departureStationName, String arrivalStationName, int hourOfDay);

            List<Station4Database> getSearchedStations();

            int getHourOfDay();

            boolean userHasModifiedTime();

            void changeTime(int delta);

        }
    }

    interface Results {

        interface View extends BaseView<Presenter> {

            void showProgress();

            void hideProgress();

            void setRvJourneySolutions(List<SolutionList.Solution> solutionList);

            void showError(String message);

        }

        interface Presenter extends BasePresenter {

            List<SolutionList.Solution> getSolutionList();

            void searchJourney(JourneySearchStrategy strategy,
                               String departureStationId,
                               String arrivalStationId,
                               long timestamp,
                               boolean isPreemptive,
                               boolean withDelays);

            void searchJourney(JourneySearchStrategy strategy,
                               String departureStationId,
                               String arrivalStationId,
                               int hourOfDay,
                               boolean isPreemptive,
                               boolean withDelays);

            String getDepartureStationId();

            String getArrivalStationId();

            int getHourOfDay();

            interface JourneySearchStrategy {

                interface OnJourneySearchFinishedListener {

                    void onStationNotFound();

                    void onJourneyNotFound();

                    void onServerError();

                    void onSuccess();

                }

                void searchJourney(String departureStationId,
                                   String arrivalStationId,
                                   long timestamp,
                                   boolean isPreemptive,
                                   boolean withDelays,
                                   OnJourneySearchFinishedListener listener);
            }
        }
    }
}
