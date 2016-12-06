package com.jaus.albertogiunta.justintrain_oraritreni.journeyFavourites;

import com.jaus.albertogiunta.justintrain_oraritreni.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraritreni.BaseView;
import com.jaus.albertogiunta.justintrain_oraritreni.data.Message;
import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils;

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
         * @param titleColor
         */
        void updateDashboard(Message message, ViewsUtils.COLORS titleColor);

        /**
         * Should be called after the preferred journey list has been updated
         */
        void displayFavouriteJourneys();

        /**
         * Should be called when no preferred journey is present, it displays an additional button
         */
        void displayEntryButton();

        void hideDashboard();
    }

    interface Presenter extends BasePresenter {

        /**
         * Getter for the preferred journeys list object
         * @return the preferred journeys list object
         */
        List<PreferredJourney> getPreferredJourneys();
    }
}
