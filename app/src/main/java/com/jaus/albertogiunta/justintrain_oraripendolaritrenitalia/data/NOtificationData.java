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

    public NotificationData setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
        return this;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public NotificationData setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
        return this;
    }

    public String getDepartureTimeReadable() {
        return departureTimeReadable;
    }

    public NotificationData setDepartureTimeReadable(String departureTimeReadable) {
        this.departureTimeReadable = departureTimeReadable;
        return this;
    }

    public String getArrivalStationName() {
        return arrivalStationName;
    }

    public NotificationData setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
        return this;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public NotificationData setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
        return this;
    }

    public String getArrivalTimeReadable() {
        return arrivalTimeReadable;
    }

    public NotificationData setArrivalTimeReadable(String arrivalTimeReadable) {
        this.arrivalTimeReadable = arrivalTimeReadable;
        return this;
    }

    public String getTrainCategory() {
        return trainCategory;
    }

    public NotificationData setTrainCategory(String trainCategory) {
        this.trainCategory = trainCategory;
        return this;
    }

    public String getTrainId() {
        return trainId;
    }

    public NotificationData setTrainId(String trainId) {
        this.trainId = trainId;
        return this;
    }

    public String getTrainDepartureStationId() {
        return trainDepartureStationId;
    }

    public NotificationData setTrainDepartureStationId(String trainDepartureStationId) {
        this.trainDepartureStationId = trainDepartureStationId;
        return this;
    }

    public int getTimeDifference() {
        return timeDifference;
    }

    public NotificationData setTimeDifference(int timeDifference) {
        this.timeDifference = timeDifference;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public NotificationData setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public String getLastSeenStationName() {
        return lastSeenStationName;
    }

    public NotificationData setLastSeenStationName(String lastSeenStationName) {
        this.lastSeenStationName = lastSeenStationName;
        return this;
    }

    public String getLastSeenTimeReadable() {
        return lastSeenTimeReadable;
    }

    public NotificationData setLastSeenTimeReadable(String lastSeenTimeReadable) {
        this.lastSeenTimeReadable = lastSeenTimeReadable;
        return this;
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
