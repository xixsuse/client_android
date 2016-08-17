package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

/**
 * Created by albertogiunta on 04/08/16.
 */
public class NotificationData {

    private String departureStationName;
    private long departureTime;
    private String departureTimeReadable;
    private String arrivalStationName;
    private long arrivalTime;
    private String arrivalTimeReadable;

    private String trainCategory;
    private String trainId;
    private String trainDepartureStationId;

    private int timeDifference;
    private int progress;

    private String lastSeenStationName;
    private String lastSeenTimeReadable;

    public NotificationData() {
    }

    public String getDepartureStationName() {
        return departureStationName;
    }

    public void setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public String getDepartureTimeReadable() {
        return departureTimeReadable;
    }

    public void setDepartureTimeReadable(String departureTimeReadable) {
        this.departureTimeReadable = departureTimeReadable;
    }

    public String getArrivalStationName() {
        return arrivalStationName;
    }

    public void setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalTimeReadable() {
        return arrivalTimeReadable;
    }

    public void setArrivalTimeReadable(String arrivalTimeReadable) {
        this.arrivalTimeReadable = arrivalTimeReadable;
    }

    public String getTrainCategory() {
        return trainCategory;
    }

    public void setTrainCategory(String trainCategory) {
        this.trainCategory = trainCategory;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainDepartureStationId() {
        return trainDepartureStationId;
    }

    public void setTrainDepartureStationId(String trainDepartureStationId) {
        this.trainDepartureStationId = trainDepartureStationId;
    }

    public int getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(int timeDifference) {
        this.timeDifference = timeDifference;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getLastSeenStationName() {
        return lastSeenStationName;
    }

    public void setLastSeenStationName(String lastSeenStationName) {
        this.lastSeenStationName = lastSeenStationName;
    }

    public String getLastSeenTimeReadable() {
        return lastSeenTimeReadable;
    }

    public void setLastSeenTimeReadable(String lastSeenTimeReadable) {
        this.lastSeenTimeReadable = lastSeenTimeReadable;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "departureStationName='" + departureStationName + '\'' +
                ", arrivalStationName='" + arrivalStationName + '\'' +
                ", trainCategory='" + trainCategory + '\'' +
                ", trainId='" + trainId + '\'' +
                ", trainDepartureStationId='" + trainDepartureStationId + '\'' +
                ", timeDifference=" + timeDifference +
                ", progress=" + progress +
                ", lastSeenStationName='" + lastSeenStationName + '\'' +
                ", lastSeenTimeReadable='" + lastSeenTimeReadable + '\'' +
                '}';
    }
}
