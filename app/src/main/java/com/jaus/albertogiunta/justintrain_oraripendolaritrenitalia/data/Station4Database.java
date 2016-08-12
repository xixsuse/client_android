package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by albertogiunta on 31/07/16.
 */
public class Station4Database extends RealmObject {

    @Required
    private String stationShortId;
    @Required
    private String stationLongId;
    @Required
    private String name;


    public String getStationShortId() {
        return stationShortId;
    }

    public void setStationShortId(String stationShortId) {
        this.stationShortId = stationShortId;
    }

    public String getStationLongId() {
        return stationLongId;
    }

    public void setStationLongId(String stationLongId) {
        this.stationLongId = stationLongId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Station4Database{" +
                "stationShortId='" + stationShortId + '\'' +
                ", stationLongId='" + stationLongId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
