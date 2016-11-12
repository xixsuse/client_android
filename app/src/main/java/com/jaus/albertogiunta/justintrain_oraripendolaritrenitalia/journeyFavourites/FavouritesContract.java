package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.List;

interface FavouritesContract {

    interface View extends BaseView {

        /**
         * It will notify the adapter after the favourite journeys list object has been updated
         * @param preferredJourneys actually not needed. Take it out?
         */
        void updateFavouritesList(List<PreferredJourney> preferredJourneys);
        // TODO parameters actually not needed, take it out?

        /**
         * Gets the context of the view (this)
         * @return this view context
         */
        Context getViewContext();
        // TODO put it in BaseView

        void updateDashboard(Message message);

        void displayFavouriteJourneys();

        void displayEntryButton();
    }

    interface Presenter extends BasePresenter {

        void updateAllMessages();

        /**
         * Getter for the preferred journeys list object
         * @return the preferred journeys list object
         */
        List<PreferredJourney> getPreferredJourneys();

        void updateRequested();

//        void stopTimer();
    }

}
