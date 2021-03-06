package com.jaus.albertogiunta.justintrain_oraritreni.notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jaus.albertogiunta.justintrain_oraritreni.R;
import com.jaus.albertogiunta.justintrain_oraritreni.data.TrainHeader;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraritreni.trainDetails.TrainDetailsActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.NotificationDataHelper;

import org.joda.time.DateTime;

import java.util.Random;

import static com.jaus.albertogiunta.justintrain_oraritreni.notification.NotificationService.ACTION_STOP_NOTIFICATION;
import static com.jaus.albertogiunta.justintrain_oraritreni.notification.NotificationService.ACTION_UPDATE_NOTIFICATION;
import static com.jaus.albertogiunta.justintrain_oraritreni.notification.NotificationService.EXTRA_NOTIFICATION_DATA;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_SOLUTION;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_STATIONS;

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
    static void notify(final Context context, TrainHeader trainHeader, boolean hasVibration) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
                .create();

        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        final String title = buildTitle(trainHeader);
        final String text = buildBody(trainHeader);
        final String smallText = buildSmallText(trainHeader);

        Intent iUpdate = new Intent(context, NotificationService.class);
        iUpdate.putExtra(EXTRA_NOTIFICATION_DATA, gson.toJson(trainHeader));
        iUpdate.setAction(ACTION_UPDATE_NOTIFICATION);

        Intent iStop = new Intent(context, NotificationService.class);
        iStop.setAction(ACTION_STOP_NOTIFICATION);

        Intent notificationIntent = new Intent(context, TrainDetailsActivity.class);
        notificationIntent.putExtra(I_SOLUTION, NotificationDataHelper.getNotificationSolutionString(context));
        notificationIntent.putExtra(I_STATIONS, NotificationDataHelper.getNotificationPreferredJourneyString(context));
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, new Random().nextInt(),
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int refreshIc = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_refresh : R.drawable.ic_refresh2;
        int closeIc = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.ic_close : R.drawable.ic_close2;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_SOUND)
                // Set required fields, including the small icon, the notification title, and text.
                .setSmallIcon(R.drawable.ic_notification2)
                .setContentTitle(title)
                .setContentText(text)
                // Use a default priority (recognized on devices running Android 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set ticker text (preview) information for this notification.
                .setTicker(buildTicker(trainHeader))
                // Show expanded text content on devices running Android 4.1 or later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText(smallText))
                .addAction(
                        refreshIc,
                        res.getString(R.string.action_refresh),
                        PendingIntent.getService(context, 1000, iUpdate, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        closeIc,
                        res.getString(R.string.action_end),
                        PendingIntent.getService(context, 1001, iStop, PendingIntent.FLAG_UPDATE_CURRENT))
                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(intent);



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

    private static String buildTitle(TrainHeader data) {
        return new Builder()
                .withString(data.getTrainCategory())
                .withSpace()
                .withString(data.getTrainId())
                .withEndingSymbol("|")
                .withSeparatedWords(data.getJourneyDepartureStationName(), String.valueOf((char) 187), data.getJourneyArrivalStationName())
                .build();
    }

    private static String buildBody(TrainHeader data) {
        String delayPlusProgress = new Builder()
                .withString(buildTimeDifferenceString(data.getTimeDifference()))
                .withEndingSymbol("|")
                .withString(buildProgressString(data.getProgress()))
                .build();

        return delayPlusProgress + "\n" + buildPredictor(data);
    }

    private static String buildSmallText(TrainHeader data) {
        return buildLastSeenString(data.getLastSeenTimeReadable(), data.getLastSeenStationName());
    }

    private static String buildTicker(TrainHeader data) {
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
        String progr = "Andamento ";
        switch (progress) {
            case 0:
                return progr + "Costante";
            case 1:
                return progr + "Recuperando";
            case 2:
                return progr + "Rallentando";
            default:
                return "";
        }
    }

    private static String buildPredictor(TrainHeader data) {
        String prediction = "Probabile arrivo a ";
        String station;

        if (!data.getJourneyDepartureStationVisited()) {
            station = data.getJourneyDepartureStationName();
            //TODO NEXT VERSION
//            station += " (Bin. " + data.getDeparturePlatform() + ")";
        } else if (!data.getJourneyArrivalStationVisited()) {
            station = data.getJourneyArrivalStationName();
        } else {
            return "Treno arrivato a " + data.getJourneyArrivalStationName();
        }

        prediction += station;

        int eta = data.getETAToNextJourneyStation();
        if (eta == 0) {
            prediction += " adesso ";
        } else if (eta == 1) {
            prediction += (" tra " + eta + " minuto");
        } else if (eta > 1) {
            if (eta < 60) {
                prediction += (" tra " + eta + " minuti");
            } else if (eta / 60 < 2) {
                prediction += (" tra più di " + 1 + " ora");
            } else {
                int hours = eta / 60;
                prediction += (" tra più di " + hours + " ore");
            }
        }

        return prediction;
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
