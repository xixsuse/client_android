package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.List;

interface FavouritesContract {

    interface View extends BaseView {

        /**
         * It will notify the adapter after the favourite journeys list object has been updated
         */
        void updateFavouritesList();

        /**
         * Update the message board with the selected message (downloaded from the internet)
         * @param message to display
         */
        void updateDashboard(Message message);

        /**
         * Should be called after the preferred journey list has been updated
         */
        void displayFavouriteJourneys();

        /**
         * Should be called when no preferred journey is present, it displays an additional button
         */
        void displayEntryButton();
    }

    interface Presenter extends BasePresenter {

        /**
         * Getter for the preferred journeys list object
         * @return the preferred journeys list object
         */
        List<PreferredJourney> getPreferredJourneys();
    }
}
