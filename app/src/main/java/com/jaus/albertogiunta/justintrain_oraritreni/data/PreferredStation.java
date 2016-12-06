package com.jaus.albertogiunta.justintrain_oraritreni.data;

@SuppressWarnings("unused")
public class PreferredStation {

    private String stationShortId;
    private String stationLongId;
    private String nameShort;
    private String nameLong;

    public PreferredStation() {
    }

    public PreferredStation(Station4Database station) {
        this.stationShortId = station.getStationShortId();
        this.stationLongId = station.getStationLongId();
        this.nameShort = station.getNameShort();
        this.nameLong = station.getNameLong();
    }

    public PreferredStation(String stationShortId, String stationLongId, String nameShort, String nameLong) {
        this.stationShortId = stationShortId;
        this.stationLongId = stationLongId;
        this.nameShort = nameShort;
        this.nameLong = nameLong;
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

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public String getNameLong() {
        return nameLong;
    }

    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
    }

    @Override
    public String toString() {
        return "PreferredStation{" +
                "stationShortId='" + stationShortId + '\'' +
                ", stationLongId='" + stationLongId + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", nameLong='" + nameLong + '\'' +
                '}';
    }
}
