package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

@SuppressWarnings("unused")
public class TrainHeader {

    private String trainCategory;
    private String trainId;

    private String trainDepartureStationId;
    private String trainDeparturePlatform;
    private String departureStationName;

    private String arrivalStationId;
    private String arrivalStationName;

    private int timeDifference;
    private int progress;

    private String lastSeenStationName;
    private String lastSeenTimeReadable; // in HH:mm

    private String cancelledStopsInfo;

    private int firstClassOrientationCode;
    private int trainStatusCode;
    private boolean isDeparted;
    private boolean isArrivedToDestination;

    private String journeyDepartureStationId;
    private String journeyArrivalStationId;
    private String departurePlatform;
    private Boolean isVisited;

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

    public String getTrainDeparturePlatform() {
        return trainDeparturePlatform;
    }

    public void setTrainDeparturePlatform(String trainDeparturePlatform) {
        this.trainDeparturePlatform = trainDeparturePlatform;
    }

    public String getDepartureStationName() {
        return departureStationName;
    }

    public void setDepartureStationName(String departureStationName) {
        this.departureStationName = departureStationName;
    }

    public String getArrivalStationId() {
        return arrivalStationId;
    }

    public void setArrivalStationId(String arrivalStationId) {
        this.arrivalStationId = arrivalStationId;
    }

    public String getArrivalStationName() {
        return arrivalStationName;
    }

    public void setArrivalStationName(String arrivalStationName) {
        this.arrivalStationName = arrivalStationName;
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

    public String getCancelledStopsInfo() {
        return cancelledStopsInfo;
    }

    public void setCancelledStopsInfo(String cancelledStopsInfo) {
        this.cancelledStopsInfo = cancelledStopsInfo;
    }

    public int getFirstClassOrientationCode() {
        return firstClassOrientationCode;
    }

    public void setFirstClassOrientationCode(int firstClassOrientationCode) {
        this.firstClassOrientationCode = firstClassOrientationCode;
    }

    public int getTrainStatusCode() {
        return trainStatusCode;
    }

    public void setTrainStatusCode(int trainStatusCode) {
        this.trainStatusCode = trainStatusCode;
    }

    public boolean isDeparted() {
        return isDeparted;
    }

    public void setDeparted(boolean departed) {
        isDeparted = departed;
    }

    public boolean isArrivedToDestination() {
        return isArrivedToDestination;
    }

    public void setArrivedToDestination(boolean arrivedToDestination) {
        isArrivedToDestination = arrivedToDestination;
    }

    public String getJourneyDepartureStationId() {
        return journeyDepartureStationId;
    }

    public void setJourneyDepartureStationId(String journeyDepartureStationId) {
        this.journeyDepartureStationId = journeyDepartureStationId;
    }

    public String getJourneyArrivalStationId() {
        return journeyArrivalStationId;
    }

    public void setJourneyArrivalStationId(String journeyArrivalStationId) {
        this.journeyArrivalStationId = journeyArrivalStationId;
    }

    public String getDeparturePlatform() {
        return departurePlatform;
    }

    public void setDeparturePlatform(String departurePlatform) {
        this.departurePlatform = departurePlatform;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }
}
