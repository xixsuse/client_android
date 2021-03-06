package com.jaus.albertogiunta.justintrain_oraritreni.utils;

public class INTENT_CONST {

    public static final int I_CODE_DEPARTURE = 1;
    public static final int I_CODE_ARRIVAL = 2;

    public static final String I_FIRST_START = "firstStart";
    public static final String I_SERVER_CONFIG = "serverConfig";

    public static final String I_STATIONS = "stations";
    public static final String I_TIME = "time";

    public static final String I_SOLUTION = "solution";
    public static final String I_TRAIN_DETAILS = "trainDetails";

    public static final String I_NOTIFICATION_SOLUTION = "notificationSolution";
    public static final String I_NOTIFICATION_PREF_JOURNEY = "notificationPrefJourney";

    public enum ERROR_BTN {
        CONN_SETTINGS, SEND_REPORT, NO_SOLUTIONS, SERVICE_UNAVAILABLE
    }

    public enum SNACKBAR_ACTIONS {
        NONE, REFRESH, SELECT_DEPARTURE, SELECT_ARRIVAL
    }
}
