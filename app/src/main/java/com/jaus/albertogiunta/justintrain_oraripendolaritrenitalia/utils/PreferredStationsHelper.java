package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import java.util.List;
import java.util.Map;

import trikita.log.Log;

public class PreferredStationsHelper {

    public static boolean isJourneyAlreadyPreferred(Context context, String id1, String id2) {
        boolean b = SharedPrefsHelper.getSharedPreferenceObject(context, buildPrefId(id1, id2)) != null;
        Log.d("isalreadypreferred", b, id1, id2);
        return b;
    }

    public static boolean isJourneyAlreadyPreferred(Context context, List<Station4Database> list) {
        return isJourneyAlreadyPreferred(context, list.get(0).getStationShortId(), list.get(1).getStationShortId());
    }

    public static void setPreferredJourney(Context context, PreferredJourney journey) {
        SharedPrefsHelper.setSharedPreferenceObject(context,
                buildPrefId(journey.getStation1().getStationShortId(),
                        journey.getStation2().getStationShortId()),
                journey);
    }


    public static PreferredJourney getPreferredJourney(Context context, String id1, String id2) {
        return (PreferredJourney) SharedPrefsHelper.getSharedPreferenceObject(context, buildPrefId(id1, id2));
    }

    public static void removePreferredJourney(Context context, String id1, String id2) {
        SharedPrefsHelper.removeSharedPreferenceObject(context, buildPrefId(id1, id2));
    }

    public static void removePreferredJourney(Context context, List<Station4Database> list) {
        removePreferredJourney(context, list.get(0).getStationShortId(), list.get(1).getStationShortId());
    }

    public static Map<String, PreferredJourney> getAll(Context context) {
        return (Map<String, PreferredJourney>) SharedPrefsHelper.getAll(context);
    }

    public static List<PreferredJourney> getAllAsObject(Context context) {
        return SharedPrefsHelper.getAllAsObject(context);
    }


    private static String buildPrefId(String id1, String id2) {
        int cod1 = Integer.parseInt(id1);
        int cod2 = Integer.parseInt(id2);
        return Math.min(cod1, cod2) + "-" + Math.max(cod1, cod2);
    }
}
