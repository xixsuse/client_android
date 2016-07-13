package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by albertogiunta on 18/06/16.
 */
public class SolutionList {

    public List<Solution> solutionList = new LinkedList<>();

    @Override
    public String toString() {
        return "SolutionList{" +
                "solutionList=" + solutionList +
                '}';
    }

    public class Solution {

        public String journeyDepartureStationId;
        public String journeyArrivalStationId;
        public Change solution;
        public boolean hasChanges;
        public ChangesInfo changes;

        @Override
        public String toString() {
            return "Solution{" +
                    "journeyDepartureStationId='" + journeyDepartureStationId + '\'' +
                    ", journeyArrivalStationId='" + journeyArrivalStationId + '\'' +
                    ", solution=" + solution +
                    ", hasChanges=" + hasChanges +
                    ", changes=" + changes +
                    '}';
        }

        public class Change {
            public String trainCategory;
            public String trainId;

            public String departureStationId;
            public String departureStationName;
            public long departureTime;
            public String departureTimeReadable;
            public String departurePlatform;

            public String arrivalStationName;
            public long arrivalTime;
            public String arrivalTimeReadable;

            public Integer timeDifference;
            public Integer progress;

            public String duration;

            @Override
            public String toString() {
                return "Change{" +
                        "tvTrainCategory='" + trainCategory + '\'' +
                        ", trainId='" + trainId + '\'' +
                        ", departureStationId='" + departureStationId + '\'' +
                        ", departureStationName='" + departureStationName + '\'' +
                        ", departureTime=" + departureTime +
                        ", departureTimeReadable='" + departureTimeReadable + '\'' +
                        ", departurePlatform='" + departurePlatform + '\'' +
                        ", arrivalStationName='" + arrivalStationName + '\'' +
                        ", arrivalTime=" + arrivalTime +
                        ", arrivalTimeReadable='" + arrivalTimeReadable + '\'' +
                        ", timeDifference=" + timeDifference +
                        ", duration='" + duration + '\'' +
                        '}';
            }
        }

        public class ChangesInfo {
            public int changesNumber;
            public List<ShortTrainDetails> quickTrainDetails = new ArrayList<>();
            public List<Change> changesList = new ArrayList<>();

            @Override
            public String toString() {
                return "ChangesInfo{" +
                        "changesNumber=" + changesNumber +
                        ", quickTrainDetails=" + quickTrainDetails +
                        ", changesList=" + changesList +
                        '}';
            }
        }

        public class ShortTrainDetails {
            public String trainCategory;
            public String trainId;
            public String departureStationId;

            @Override
            public String toString() {
                return "ShortTrainDetails{" +
                        "tvTrainCategory='" + trainCategory + '\'' +
                        ", trainId='" + trainId + '\'' +
                        ", departureStationId='" + departureStationId + '\'' +
                        '}';
            }
        }
    }
}