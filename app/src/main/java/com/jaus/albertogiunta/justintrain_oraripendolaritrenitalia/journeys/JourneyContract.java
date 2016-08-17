package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.List;

public interface JourneyContract {

    interface Presenter extends BasePresenter {

        // SEARCH
        // TODO put the HOUR constant here?

        /**
         * It triggers an action when the user clicks on the "star" icon button.
         * The action will most likely be to toggle the icon and save/remove the current
         * journey from the favourite journeys.
         */
        void onFavouriteButtonClick();

        /**
         * It is called in order to automatically set the status of the favourite button.
         * When called it will check for the current journey if it's already favourite, and
         * it will set the button status depending on that.
         */
        void setFavouriteButtonStatus();

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
         */
        void onSwapButtonClick();

        // RESULTS

        /**
         * Called when the Load More (Before) button is clicked. It will search for another set of
         * items (without delay, non preemptively) before the given ones (departing until 1 second
         * before the first know solution in order to avoid duplicates)
         */
        void onLoadMoreItemsBefore();

        /**
         * Called when the user swipes on a preferred station in the main activity and launches an
         * intent with the swiped data.
         * It's an INSTANT kind of search.
         */
        void searchFromIntent();

        /**
         * Called when the search input data is VALID, after the user clicked on the search button.
         * It will either search with the INSTANT search or the SearchAFTER kind of search.
         */
        void searchFromSearch();

        /**
         * Called when the Load More (After) button is clicked. It will search for another set of
         * items (without delay, non preemptively) after the given ones (departing 61 second after
         * the last known solution in order to avoid duplicates)
         */
        void onLoadMoreItemsAfter();

        void onNotificationRequested(int elementIndex);

        /**
         * Getter for the solution list (it's a static field)
         * @return the current solution list
         */
        List<SolutionList.Solution> getSolutionList();
    }

    interface View extends BaseView {

        enum SEARCH_PANEL_STATUS {
            @SerializedName("ACTIVE")
            ACTIVE,
            @SerializedName("INACTIVE")
            INACTIVE
        }

        /**
         * Getter for the View Context (needed by the presenter in order to do stuff with
         * Shared Preferences)
         * @return the current view's context (getApplicationContext())
         */
        Context getViewContext();

        // SEARCH

        /**
         * When called it will set the panel as Active (insert mode) or Inactive (Result mode)
         * @param status the status to be set
         */
        void setJourneySearchFragmentViewStatus(SEARCH_PANEL_STATUS status);

        /**
         * Used by the presenter in order to know the state of the activity (search panel related)
         * @return the current activity's state
         */
        SEARCH_PANEL_STATUS getJourneySearchFragmentViewStatus();


        void setStationNames(String departureStationName, String arrivalStationName);

        /**
         * Callend whenever there's the need to set the time (on start up of the activity or after
         * a change done by the user)
         * @param time the already formatted string (hh:mm) to be set
         */
        void setTime(String time);

        /**
         * Shows an error under the Autocomplete Edit Text if something wrong happens during the
         * search in the database
         * @param error message for the user
         */
        void showDepartureStationNameError(String error);

        /**
         * Shows an error under the Autocomplete Edit Text if something wrong happens during the
         * search in the database
         * @param error message for the user
         */
        void showArrivalStationNameError(String error);

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

        /**
         * Sets the status of the favourite button depending on if the current journey is favourite
         * or not
         * @param isPreferred the status to be set
         */
        void setFavouriteButtonStatus(boolean isPreferred);

        // RESULTS

        /**
         * Hides everything and shows a loader
         */
        void showProgress();

        /**
         * Hides the loader and shows everything else
         */
        void hideProgress();

        /**
         * Called when a new set of solutions is ready. It will then notify the adapter
         * @param solutionList the solutionlist to be set in the adapter
         */
        void updateSolutionsList(List<SolutionList.Solution> solutionList);

        /**
         * Shows an error
         * @param message to be send to the user
         */
        void showError(String message);

        interface JourneySearchStrategy {

            interface OnJourneySearchFinishedListener {

                /**
                 * Problems happened with the stations (probably ids).
                 */
                void onStationNotFound();

                /**
                 * No solution found for the requested journey
                 */
                void onJourneyNotFound();

                /**
                 * The server is unreachable or network not available?
                 */
                void onServerError();

                /**
                 * The request has gone through. Everything's ok (but still it can be that there's
                 * no solution. Check for it)
                 */
                void onSuccess();

            }

            /**
             * Strategy used to search in the various kind of classes available (Search Instant,
             * Before or After).
             * @param departureStationId short departure Id
             * @param arrivalStationId short arrival Id
             * @param timestamp departure time in unix time (seconds)
             * @param isPreemptive if you want ONE journey before the next available one
             * @param withDelays if you want N delays in the returned set of solutions
             * @param listener the listener that will call onSuccess or the other methods
             */
            void searchJourney(String departureStationId,
                               String arrivalStationId,
                               long timestamp,
                               boolean isPreemptive,
                               boolean withDelays,
                               OnJourneySearchFinishedListener listener);
        }
    }

}
