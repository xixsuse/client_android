package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;


public class PreferredStation {

    private String stationShortId;
    private String stationLongId;
    private String name;

    public PreferredStation() {
    }

    public PreferredStation(Station4Database station) {
        this.stationShortId = station.getStationShortId();
        this.stationLongId = station.getStationLongId();
        this.name = station.getName();
    }

    public PreferredStation(String stationShortId, String stationLongId, String name) {
        this.stationShortId = stationShortId;
        this.stationLongId = stationLongId;
        this.name = name;
    }

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
        return "PreferredStation{" +
                "name='" + name + '\'' +
                ", stationLongId='" + stationLongId + '\'' +
                ", stationShortId='" + stationShortId + '\'' +
                '}';
    }
}
