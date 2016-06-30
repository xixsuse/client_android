package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albertogiunta on 19/06/16.
 */
public class Train {

    private String trainCategory;
    private String trainId;

    private String departureStationId;
    private String departureStationName;
    private String departurePlatform;

    private String arrivalStationId;
    private String arrivalStationName;

    private int timeDifference; //TODO vedi se calcolarlo anche a mano
    private int progress;

    private String lastSeenStationName;
    private String lastSeenTimeReadable; // in HH:mm

    private List<Stop> stops = new ArrayList<>();
    private String cancelle1dStopsInfo; // riguarda solo soppressioni di fermate

    private int firstClassOrientationCode;
    private int trainStatusCode;
    private boolean isDeparted;
    private boolean isArrivedToDestination;

    public class Stop {

        private String stationId;
        private String stationName;

        private boolean isVisited;

        private int currentStopTypeCode;
        private int currentStopStatusCode;
        private int currentAndNextStopStatusCode;

        private long plannedArrivalTime;
        private long actualArrivalTime;

        private long plannedDepartureTime;
        private String plannedDepartureTimeReadable;
        private String plannedDeparturePlatform;

        private long actualDepartureTime;
        private String actualDepartureTimeReadable;
        private String actualDeparturePlatform;

        private int timeDifference;

    }

}
