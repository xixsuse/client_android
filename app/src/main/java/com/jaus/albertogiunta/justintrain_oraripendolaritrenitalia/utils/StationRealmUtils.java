package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by albertogiunta on 28/09/16.
 */
public class StationRealmUtils {

    private static RealmResults<Station4Database> stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();;

    public static List<String> getElement(String stationName) {
        return Stream
                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
                .map(Station4Database::getName).collect(Collectors.toList());
    }
}
