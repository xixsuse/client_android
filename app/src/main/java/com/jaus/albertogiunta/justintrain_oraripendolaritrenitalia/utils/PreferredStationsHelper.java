package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import com.google.gson.Gson;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;

import java.util.List;

public class PreferredStationsHelper {

    private static boolean isJourneyAlreadyPreferred(Context context, String id1, String id2) {
        return SharedPrefsHelper.getSharedPreferenceObject(context, buildPrefId(id1, id2)) != null;
    }

//    public static boolean isJourneyAlreadyPreferred(Context context, List<Station4Database> list) {
//        return isJourneyAlreadyPreferred(context, list.get(0).getStationShortId(), list.get(1).getStationShortId());
//    }

    static boolean isJourneyAlreadyPreferred(Context context, PreferredStation departureStation, PreferredStation arrivalStation) {
        return isJourneyAlreadyPreferred(context, departureStation.getStationShortId(), arrivalStation.getStationShortId());
    }

    public static void setPreferredJourney(Context context, PreferredJourney journey) {
        SharedPrefsHelper.setSharedPreferenceObject(context,
                buildPrefId(journey.getStation1().getStationShortId(),
                        journey.getStation2().getStationShortId()),
                new Gson().toJson(journey));
    }


//    public static PreferredJourney getPreferredJourney(Context context, String id1, String id2) {
//        return new Gson().fromJson(SharedPrefsHelper.getSharedPreferenceObject(context, buildPrefId(id1, id2)), PreferredJourney.class);
//    }

    private static void removePreferredJourney(Context context, String id1, String id2) {
        SharedPrefsHelper.removeSharedPreferenceObject(context, buildPrefId(id1, id2));
    }

//    public static void removePreferredJourney(Context context, List<Station4Database> list) {
//        removePreferredJourney(context, list.get(0).getStationShortId(), list.get(1).getStationShortId());
//    }

    public static void removePreferredJourney(Context context, PreferredStation departureStation, PreferredStation arrivalStation) {
        removePreferredJourney(context, departureStation.getStationShortId(), arrivalStation.getStationShortId());
    }

//    public static Map<String, PreferredJourney> getAll(Context context) {
//        return (Map<String, PreferredJourney>) SharedPrefsHelper.getAll(context);
//    }

    public static List<PreferredJourney> getAllAsObject(Context context) {
        return SharedPrefsHelper.getAllAsObject(context);
    }

    private static String buildPrefId(String id1, String id2) {
        int cod1 = Integer.parseInt(id1);
        int cod2 = Integer.parseInt(id2);
        return Math.min(cod1, cod2) + "-" + Math.max(cod1, cod2);
    }
}
