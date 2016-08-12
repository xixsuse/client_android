package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import io.realm.Case;
import io.realm.RealmResults;

public class PresenterUtilities {

    public static boolean isThisJourneyPreferred(PreferredStation departureStation, PreferredStation arrivalStation, Context context) {
        return  departureStation != null &&
                arrivalStation != null &&
                PreferredStationsHelper.isJourneyAlreadyPreferred(
                        context,
                        departureStation,
                        arrivalStation);
    }

    public static boolean isStationNameValid(String stationName, RealmResults<Station4Database> stationList) {
        RealmResults<Station4Database> matchingStations = stationList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll();
        if (matchingStations.size() == 1 && !stationName.isEmpty()) {
            return true;
        }
        return false;
    }

    public static Station4Database getStationObject(String stationName, RealmResults<Station4Database> stationList) {
        return stationList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll().get(0);
    }

    public static boolean isInstant(int selectedHour, int originalHour) {
        return selectedHour == originalHour;
    }
}
