package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

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
    public static final String ERROR_REFRESH = "action";
    public static final String ERROR_CONNECTIVITY = "action";
    public static final String ERROR_SERVER = "action";
    public static final String ERROR_NOT_FOUND_JOURNEY = "action";
    public static final String ERROR_NOT_FOUND_JOURNEY_BEFORE = "action";
    public static final String ERROR_NOT_FOUND_JOURNEY_AFTER = "action";
    public static final String ERROR_NOT_FOUND_SOLUTION = "action";
    public static final String ERROR_NOT_FOUND_STATION = "action";

    // TUTORIAL
    public static final String ACTION_TUTORIAL_NEXT = "action";
    public static final String ACTION_TUTORIAL_SKIP = "action";
    public static final String ACTION_TUTORIAL_DONE = "action";

    // FAVOURITES
    public static final String ACTION_FAVOURITES_ADD_FIRST = "action";
    public static final String ACTION_SEARCH_JOURNEY_FROM_FAVOURITES = "action";
    public static final String ACTION_SWIPE_LEFT_TO_RIGHT = "action";
    public static final String ACTION_SWIPE_RIGHT_TO_LEFT = "action";
    public static final String ACTION_NO_SWIPE_BUT_CLICK = "action";
    public static final String ACTION_NO_SWIPE_BUT_LONG_CLICK = "action";

    // SEARCH
    public static final String ACTION_SELECT_DEPARTURE = "action";
    public static final String ACTION_SELECT_ARRIVAL = "action";
    public static final String ACTION_SWAP_STATIONS_FROM_SEARCH = "action";
    public static final String ACTION_TIME_CLICK = "action";
    public static final String ACTION_TIME_MINUS = "action";
    public static final String ACTION_TIME_PLUS = "action";
    public static final String ACTION_DATE_CLICK = "action";
    public static final String ACTION_DATE_MINUS = "action";
    public static final String ACTION_DATE_PLUS = "action";
    public static final String ACTION_SET_FAVOURITE_FROM_SEARCH = "action";
    public static final String ACTION_REMOVE_FAVOURITE_FROM_SEARCH = "action";
    public static final String ACTION_SEARCH_JOURNEY_FROM_SEARCH = "action";

    // RESULTS
    public static final String ACTION_SWAP_STATIONS_FROM_RESULTS = "action";
    public static final String ACTION_SET_FAVOURITE_FROM_RESULTS = "action";
    public static final String ACTION_REMOVE_FAVOURITE_FROM_RESULTS = "action";
    public static final String ACTION_SEARCH_MORE_BEFORE = "action";
    public static final String ACTION_SEARCH_MORE_AFTER = "action";
    public static final String ACTION_REFRESH_JOURNEY = "action";

    // RESULTS ITEM
    public static final String ACTION_SOLUTION_CLICK = "action";
    public static final String ACTION_SET_NOTIFICATION_FROM_RESULTS = "action";
    public static final String ACTION_REFRESH_DELAY_FIRST = "action";
    public static final String ACTION_REFRESH_DELAY_ALREADY = "action";

    // SOLUTION
    public static final String ACTION_REFRESH_SOLUTION = "action";
    public static final String ACTION_SET_NOTIFICATION_FROM_SOLUTION = "action";

    // NOTIFICATION
    public static final String ACTION_REFRESH_NOTIFICAITON = "action";
    public static final String ACTION_REMOVE_NOTIFICATION = "action";
    public static final String ACTION_OPEN_NOTIFICATION = "action";
    public static final String ACTION_EXPAND_NOTIFICATION = "action";

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
        firebase.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
