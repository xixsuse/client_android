package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

public class PreferredStation {

    private String name;
    private String stationShortId;
    private String stationLongId;

    public PreferredStation() {
    }

    public PreferredStation(String name, String stationShortId, String stationLongId) {
        this.name = name;
        this.stationShortId = stationShortId;
        this.stationLongId = stationLongId;
    }

    public PreferredStation(Station4Database s) {
        this.name = s.getName();
        this.stationShortId = s.getStationShortId();
        this.stationLongId = s.getStationLongId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
}
