package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.List;

/**
 * Created by albertogiunta on 01/10/16.
 */

public interface JourneyResultsContract {

    interface Presenter extends BasePresenter {

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

        void onJourneyRefreshRequested(int elementIndex);

        /**
         * Getter for the solution list (it's a static field)
         * @return the current solution list
         */
        List<SolutionList.Solution> getSolutionList();

    }

    interface View extends BaseView {
        /**
         * Getter for the View Context (needed by the presenter in order to do stuff with
         * Shared Preferences)
         *
         * @return the current view's context (getApplicationContext())
         */
        Context getViewContext();

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

        void updateSolution(int elementIndex);

//        void showOfflineWithMessage(String message);

        /**
         * Sets the status of the favourite button depending on if the current journey is favourite
         * or not
         *
         * @param isPreferred the status to be set
         */
        void setFavouriteButtonStatus(boolean isPreferred);

        void setStationNames(String departure, String arrival);

        /**
         * Shows an error
         * @param message to be send to the user
         */
        void showSnackbar(String message);

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
