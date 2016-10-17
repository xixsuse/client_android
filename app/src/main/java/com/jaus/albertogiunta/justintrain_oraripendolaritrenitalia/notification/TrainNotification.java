package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.NotificationData;

import org.joda.time.DateTime;

import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService.ACTION_STOP_NOTIFICATION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService.ACTION_UPDATE_NOTIFICATION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService.EXTRA_NOTIFICATION_DATA;

/**
 * Helper class for showing and canceling train
 * notifications.
 */
class TrainNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String TRAIN_NOTIFICATION_TAG = "trainNotification";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     */
    static void notify(final Context context, NotificationData notificationData, boolean hasVibration) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        final String title = buildTitle(notificationData);
        final String text = buildBody(notificationData);
        final String smallText = buildSmallText(notificationData);

        Intent iUpdate = new Intent(context, NotificationService.class);
        iUpdate.putExtra(EXTRA_NOTIFICATION_DATA, new Gson().toJson(notificationData));
        iUpdate.setAction(ACTION_UPDATE_NOTIFICATION);

        Intent iStop = new Intent(context, NotificationService.class);
        iStop.setAction(ACTION_STOP_NOTIFICATION);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_SOUND)
                // Set required fields, including the small icon, the notification title, and text.
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle(title)
                .setContentText(text)
                // Use a default priority (recognized on devices running Android 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set ticker text (preview) information for this notification.
                .setTicker(buildTicker(notificationData))
                // Show expanded text content on devices running Android 4.1 or later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText(smallText))
                .addAction(
                        R.drawable.ic_refresh,
                        res.getString(R.string.action_refresh),
                        PendingIntent.getService(context, 0, iUpdate, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_clear,
                        res.getString(R.string.action_end),
                        PendingIntent.getService(context, 1, iStop, PendingIntent.FLAG_UPDATE_CURRENT))
                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(false)
                .setOngoing(true);

        if (!hasVibration) {
            builder.setVibrate(null);
        }

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(TRAIN_NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(TRAIN_NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(TRAIN_NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(TRAIN_NOTIFICATION_TAG.hashCode());
        }
    }

    private static String buildTitle(NotificationData data) {
        return new Builder()
                .withString(data.getTrainCategory())
                .withSpace()
                .withString(data.getTrainId())
                .withEndingSymbol("|")
                .withSeparatedWords(data.getDepartureStationName(), String.valueOf((char) 187), data.getArrivalStationName())
                .build();
    }

    private static String buildBody(NotificationData data) {
        String delayPlusProgress = new Builder()
                .withString(buildTimeDifferenceString(data.getTimeDifference()))
                .withEndingSymbol("|")
                .withString(buildProgressString(data.getProgress()))
                .build();

        return delayPlusProgress + "\n" + buildPredictor(data);
    }

    private static String buildSmallText(NotificationData data) {
        return buildLastSeenString(data.getLastSeenTimeReadable(), data.getLastSeenStationName());
    }

    private static String buildTicker(NotificationData data) {
        return new Builder()
                .withString(buildTimeDifferenceString(data.getTimeDifference()))
                .withEndingSymbol("|")
                .withString(buildProgressString(data.getProgress()))
                .build();
    }

    private static String trimStationName(String stationName) {
        return (stationName.length() > 5 ? stationName.substring(0, 3) + "." : stationName).toUpperCase();
    }

    private static String buildTimeDifferenceString(int timeDifference) {
        String time = Integer.toString(Math.abs(timeDifference)) + "'";
        String delay = "Ritardo: ";
        String ontime = "Anticipo: ";
        if (timeDifference > 0) {
            time = delay + time;
        } else if (timeDifference < 0) {
            time = ontime + time;
        } else {
            time = "In orario";
        }
        return time;
    }

    private static String buildProgressString(int progress) {
        switch (progress) {
            case 0:
                return "Costante";
            case 1:
                return "Recuperando";
            case 2:
                return "Rallentando";
            default:
                return "";
        }
    }

    private static String buildPredictor(NotificationData data) {
        DateTime now = DateTime.now();
        DateTime dep = new DateTime(data.getDepartureTime() * 1000).minusHours(2).plusMinutes(data.getTimeDifference());
        DateTime arr = new DateTime(data.getArrivalTime() * 1000).minusHours(2).plusMinutes(data.getTimeDifference());
        Log.d("now", now.toString());

        if (now.isBefore(dep)) {
            return buildPrediction(data.getDepartureStationName(), (dep.minuteOfDay().get() - now.minuteOfDay().get()));
        } else if (now.isBefore(arr)) {
            return buildPrediction(data.getArrivalStationName(), (arr.minuteOfDay().get() - now.minuteOfDay().get()));
        } else if (now.isAfter(arr)) {
            return "Treno arrivato a " + data.getArrivalStationName();
        }
        return "";
    }

    private static String buildPrediction(String station, long minutes) {
        String minutesString = "Arrivo a " + station;
        if (minutes == 0) {
            return minutesString + " adesso ";
        } else if (minutes == 1) {
            return minutesString + (" tra " + minutes + " minuto");
        } else if (minutes > 1) {
            if (minutes < 60) {
                return minutesString + (" tra " + minutes + " minuti");
            } else if (minutes / 60 < 2) {
                return minutesString + (" tra più di " + 1 + " ora");
            } else {
                int hours = (int) (minutes / 60);
                return minutesString + (" tra più di " + hours + " ore");
            }
        }
        return "";
    }


    private static String buildLastSeenString(String time, String station) {
        return time.length() > 0 && station.length() > 0 ? "Visto alle " + time + " a " + station : "";
    }

    private static class Builder {

        String string = "";

        Builder() {
        }

        Builder withString(String s) {
            string += s;
            return this;
        }

        Builder withSpace() {
            string += " ";
            return this;
        }

        Builder withEndingSymbol(String symbol) {
            string += string.length() > 0 ? " " + symbol + " " : "";
            return this;
        }

        Builder withSeparatedWords(String first, String separator, String second) {
            string += first.length() == 0 || second.length() == 0 ? first + second : first + " " + separator + " " + second;
            return this;
        }

        String build() {
            return string;
        }
    }
}
