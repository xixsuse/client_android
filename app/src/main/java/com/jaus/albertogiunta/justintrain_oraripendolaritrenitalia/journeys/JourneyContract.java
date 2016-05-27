package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;

import java.util.List;

/**
 * Created by albertogiunta on 24/05/16.
 */
public interface JourneyContract {

    interface Search {

        interface View extends BaseView<Presenter> {

            void showArrivalStationNameError(String error);

            void showDepartureStationNameError(String error);

        }

        interface Presenter extends BasePresenter {

            List<String> getLastSearchedStations();

            List<String>  searchDbForMatchingStation(String constraint);

            void search(String departureStationName, String arrivalStationName, int hourOfDay);

        }

        interface Interactor {
            interface OnJourneySearchFinishedListener {

                void onStationNotFound();

                void onJourneyNotFound();

                void onServerError();

                void onSuccess();

            }

            void searchJourney(String departureStationId, String arrivalStationId, OnJourneySearchFinishedListener listener);
        }

    }

    interface Results {

        interface View extends BaseView<Presenter> {

        }

        interface Presenter extends BasePresenter {

        }
    }
}
