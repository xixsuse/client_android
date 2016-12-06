package com.jaus.albertogiunta.justintrain_oraritreni.utils;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.os.Bundle;

public class Analytics {

    /*

    adb shell setprop log.tag.FA VERBOSE
    adb shell setprop log.tag.FA-SVC VERBOSE
    adb logcat -v time -s FA FA-SVC

     */

    public static final String SCREEN_TUTORIAL = "TUT";
    public static final String SCREEN_FAVOURITE_JOURNEYS = "FAV";
    public static final String SCREEN_SEARCH_JOURNEY = "SEAJ";
    public static final String SCREEN_JOURNEY_RESULTS = "RESJ";
    public static final String SCREEN_SOLUTION_DETAILS = "RESJ";
    public static final String SCREEN_NOTIFICATION = "NOT";

    // ERRORS
    public static final String ERROR_REFRESH = "Error refresh";
    public static final String ERROR_CONNECTIVITY = "Error connectivity";
    public static final String ERROR_SERVER = "Error server";
    public static final String ERROR_NOT_FOUND_JOURNEY = "Error journey not found";
    public static final String ERROR_NOT_FOUND_JOURNEY_BEFORE = "Error journey before not found";
    public static final String ERROR_NOT_FOUND_JOURNEY_AFTER = "Error journey after not found";
    public static final String ERROR_NOT_FOUND_SOLUTION = "Error solution not found";
    public static final String ERROR_NOT_FOUND_STATION = "Error station not found";

    // TUTORIAL
    public static final String ACTION_TUTORIAL_NEXT = " Next slide";
    public static final String ACTION_TUTORIAL_SKIP = " Skip Tutorial";
    public static final String ACTION_TUTORIAL_DONE = " Done Tutorial";

    // FAVOURITES
    public static final String ACTION_FAVOURITES_ADD_FIRST = "Add first favourite";
    public static final String ACTION_SEARCH_JOURNEY_FROM_FAVOURITES = "Search journey from favourites";
    public static final String ACTION_SWIPE_LEFT_TO_RIGHT = "Swipe left to right";
    public static final String ACTION_SWIPE_RIGHT_TO_LEFT = "Swipe right to left";
    public static final String ACTION_NO_SWIPE_BUT_CLICK = "No swipe but click";
    public static final String ACTION_NO_SWIPE_BUT_LONG_CLICK = "No swipe but long click";

    // SEARCH
    public static final String ACTION_SELECT_DEPARTURE = "Select departure";
    public static final String ACTION_SELECT_ARRIVAL = "Select arrival";
    public static final String ACTION_SWAP_STATIONS_FROM_SEARCH = "Swap station from search";
    public static final String ACTION_TIME_CLICK = "Click time";
    public static final String ACTION_TIME_MINUS = "Minus time";
    public static final String ACTION_TIME_PLUS = "Plus time";
    public static final String ACTION_DATE_CLICK = "Date click";
    public static final String ACTION_DATE_MINUS = "Minus date";
    public static final String ACTION_DATE_PLUS = "Plus date";
    public static final String ACTION_SET_FAVOURITE_FROM_SEARCH = "Set favourite from search";
    public static final String ACTION_REMOVE_FAVOURITE_FROM_SEARCH = "Remove favourite from search";
    public static final String ACTION_SEARCH_JOURNEY_FROM_SEARCH = "Search journey from search";

    // RESULTS
    public static final String ACTION_SWAP_STATIONS_FROM_RESULTS = "Swap stations from results";
    public static final String ACTION_SET_FAVOURITE_FROM_RESULTS = "Set favourite from results";
    public static final String ACTION_REMOVE_FAVOURITE_FROM_RESULTS = "Remove favourite from results";
    public static final String ACTION_SEARCH_MORE_BEFORE = "Search more before";
    public static final String ACTION_SEARCH_MORE_AFTER = "Search more after";
    public static final String ACTION_REFRESH_JOURNEY = "Refresh journey";

    // RESULTS ITEM
    public static final String ACTION_SOLUTION_CLICK = "Click solution";
    public static final String ACTION_SET_NOTIFICATION_FROM_RESULTS = "set notification from results";
    public static final String ACTION_REFRESH_DELAY_FIRST = "Refresh delay first";
    public static final String ACTION_REFRESH_DELAY_ALREADY = "Refresh delay already";

    // SOLUTION
    public static final String ACTION_REFRESH_SOLUTION = "Refresh solution";
    public static final String ACTION_SET_NOTIFICATION_FROM_SOLUTION = "Set notification from solution";

    // NOTIFICATION
    public static final String ACTION_REFRESH_NOTIFICAITON = "Refresh notification";
    public static final String ACTION_REMOVE_NOTIFICATION = "Remove notification";
    public static final String ACTION_OPEN_NOTIFICATION = "Open notification";
    public static final String ACTION_EXPAND_NOTIFICATION = "Expand notification";

//    public static final String ACTION_ = "action";

    private FirebaseAnalytics firebase = null;

    private static Analytics analytics;

    public static Analytics getInstance(Context context) {
        if (analytics == null) {
            analytics = new Analytics(context);
        }
        return analytics;
    }

    private Analytics(Context context) {
        firebase = FirebaseAnalytics.getInstance(context);
        firebase.setMinimumSessionDuration(3000);
//        firebase.setUserId();
//        firebase.setUserProperty();
    }

    public void logScreenEvent(String screen, String action) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, screen);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, action);
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, action);
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
