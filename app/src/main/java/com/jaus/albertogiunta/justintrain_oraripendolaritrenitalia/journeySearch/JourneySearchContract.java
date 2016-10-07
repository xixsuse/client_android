package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;

import java.util.List;

interface JourneySearchContract {

    interface Presenter extends BasePresenter {

        // SEARCH
        // TODO put the HOUR constant here?

        /**
         * It triggers an action when the user clicks on the "search" button.
         * It will check for correspondance of the inserted stations, and if everything's alright
         * it will fire the search. Otherwise it will notify the user with an error message.
         * @param departureStationName name for the departure station
         * @param arrivalStationName name for the arrival station
         */
        void onSearchButtonClick(String departureStationName, String arrivalStationName);
        // TODO check if names are the same what to do

        /**
         * Acts as a listener for time changes. It's triggere by the user clicks on the time buttons
         * and it will set the time in the view depending on the button that has been pressed.
         * @param delta the number of hours to increment or decrement from the previous value
         *              (The starting value is the current hour)
         */
        void onTimeChanged(int delta);

        void onDateChanged(int delta);

        /**
         * Called every time there's a change in the Autocomplete Text Views.
         * @param stationName name (can be also partial) to be searched (case-insensitive)
         * @return a list of matching station names
         */
        List<String> searchStationName(String stationName);


        /**
         * Swaps the Preferred Station objects inside of the presenter. Called on the swap button
         * placed in the Search Panel in INACTIVE mode (only the header is visible).
         * It will swap the objects and the actual names (in both panel modes)
         * @param departure string from the textview
         * @param arrival string from the textview
         */
        void onSwapButtonClick(String departure, String arrival);

        Bundle getBundle();
    }

    interface View extends BaseView {
        // SEARCH
        void setStationNames(String departureStationName, String arrivalStationName);

        /**
         * Callend whenever there's the need to set the time (on start up of the activity or after
         * a change done by the user)
         *
         * @param time the already formatted string (hh:mm) to be set
         */
        void setTime(String time);

        void setDate(String date);

        /**
         * Called when everything goes right after the click on the search button.
         */
        void onValidSearchParameters();

        /**
         * Called when something bad happens after the click on the search button.
         * Errors can be:
         * - No results found
         * - Server error
         * - Client error
         * - Generic error
         */
        void onInvalidSearchParameters();
    }
}
