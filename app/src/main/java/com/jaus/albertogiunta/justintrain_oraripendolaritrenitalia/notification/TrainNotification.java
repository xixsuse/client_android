package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.NotificationData;

import org.joda.time.DateTime;

import trikita.log.Log;

/**
 * Helper class for showing and canceling train
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class TrainNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "trainNotification";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of train notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context, NotificationData notificationData) {
        final Resources res = context.getResources();

        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
//        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        String departureStationName = trimStationName(notificationData.getDepartureStationName());
        String arrivalStationName = trimStationName(notificationData.getArrivalStationName());

        final String title = buildTitle(notificationData);

        final String text = buildBody(notificationData);

        final String smallText = buildSmallText(notificationData);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_pin)
                .setContentTitle(title)
                .setContentText(text)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Provide a large icon, shown with the notification in the
                // notification drawer on devices running Android 3.0 or later.
//                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker(buildTicker(notificationData))

                // Show a number. This is useful when stacking notifications of
                // a single type.
//                .setNumber()

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
//                .setContentIntent(
//                        PendingIntent.getActivity(
//                                context,
//                                0,
//                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
//                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show expanded text content on devices running Android 4.1 or
                // later.
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        .setBigContentTitle(title)
                        .setSummaryText(smallText))

                // Example additional actions for this notification. These will
                // only show on devices running Android 4.1 or later, so you
                // should ensure that the activity in this notification's
                // content intent provides access to the same actions in
                // another way.
                .addAction(
                        R.drawable.ic_refresh,
                        res.getString(R.string.action_refresh),
                        null)
                .addAction(
                        R.drawable.ic_clear,
                        res.getString(R.string.action_end),
                        null)

                // Automatically dismiss the notification when it is touched.
//                .setAutoCancel(true);
                .setAutoCancel(false);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, NotificationData notificationData)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }

    private static String buildTitle(NotificationData data) {
        String categoryPlusDestinations = new Builder()
                .withString(data.getTrainCategory())
                .withSpace()
                .withString(data.getTrainId())
                .withEndingSymbol("|")
                .withSeparatedWords(data.getDepartureStationName(), String.valueOf((char) 187), data.getArrivalStationName())
                .build();

        return categoryPlusDestinations;
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
        String lastSeen = buildLastSeenString(data.getLastSeenTimeReadable(), data.getLastSeenStationName());
        return lastSeen;
    }

    private static String buildTicker(NotificationData data) {
        String ticker = new Builder()
                .withString(buildTimeDifferenceString(data.getTimeDifference()))
                .withEndingSymbol("|")
                .withString(buildProgressString(data.getProgress()))
                .build();
        return ticker;
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
        DateTime now = DateTime.now().plusHours(2);
        Log.d("now", now.toString());
        String s = "";
        if (now.isBefore(data.getDepartureTime() * 1000)) {
            s = buildPrediction(data.getDepartureStationName(),
                    (data.getDepartureTime() + (data.getTimeDifference() * 60) - now.getMillis() / 1000) / 60);
        } else if (now.isBefore(data.getArrivalTime() * 1000)) {
            s = buildPrediction(data.getArrivalStationName(),
                    (data.getArrivalTime() + (data.getTimeDifference() * 60) - now.getMillis() / 1000) / 60);
        }
        return s;
    }

    private static String buildPrediction(String station, long minutes) {
        String minutesString = minutes == 0 ? " adesso " : minutes == 1 ? " tra " + minutes + " minuto" : " tra " + minutes + " minuti";
        return "Arrivo a " + station + minutesString;
    }


    private static String buildLastSeenString(String time, String station) {
        return time.length() > 0 && station.length() > 0 ? "Visto alle " + time + " a " + station : "";
    }

    private static class Builder {

        String string = "";

        public Builder() {
        }

        public Builder withString(String s) {
            string += s;
            return this;
        }

        public Builder withSpace() {
            string += " ";
            return this;
        }

        public Builder withEndingSymbol(String symbol) {
            string += string.length() > 0 ? " " + symbol + " " : "";
            return this;
        }

        public Builder withSeparatedWords(String first, String separator, String second) {
            string += first.length() == 0 || second.length() == 0 ? first + second : first + " " + separator + " " + second;
            return this;
        }

        public String build() {
            return string;
        }
    }
}
