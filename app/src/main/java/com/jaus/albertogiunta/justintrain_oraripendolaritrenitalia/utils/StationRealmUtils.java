package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.content.Context;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

@SuppressWarnings("unused")
public class StationRealmUtils {

    private static RealmResults<Station4Database> stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();

    public static List<String> getElement(String stationName) {
        return Stream
                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
                .map(Station4Database::getName).collect(Collectors.toList());
    }

    public static boolean isThisJourneyPreferred(PreferredStation departureStation, PreferredStation arrivalStation, Context context) {
        return departureStation != null &&
                arrivalStation != null &&
                PreferredStationsHelper.isJourneyAlreadyPreferred(
                        context,
                        departureStation,
                        arrivalStation);
    }

    public static boolean isStationNameValid(String stationName, RealmResults<Station4Database> stationList) {
        RealmResults<Station4Database> matchingStations = stationList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll();
        return matchingStations.size() == 1 && !stationName.isEmpty();
    }

    public static Station4Database getStation4DatabaseObject(String stationName, RealmResults<Station4Database> stationList) {
        return stationList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll().get(0);
    }
}
