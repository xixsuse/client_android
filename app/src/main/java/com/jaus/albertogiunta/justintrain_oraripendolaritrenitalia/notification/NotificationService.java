package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.NotificationData;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread
 */
public class NotificationService extends IntentService {
    public static final String ACTION_START_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.START_NOTIFICATION";
    public static final String ACTION_STOP_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.STOP_NOTIFICATION";
    public static final String ACTION_UPDATE_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.UPDATE_NOTIFICATION";
    public static final String EXTRA_NOTIFICATION_DATA = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.extra.NOTIFICATION_DATA";

    public NotificationService() {
        super("NotificationService");
    }

    public static void startActionStartNotification(Context context,
                                                    String departureStationName,
                                                    long departureTime,
                                                    String departureTimeReadable,
                                                    String arrivalStationName,
                                                    long arrivalTime,
                                                    String arrivalTimeReadable,
                                                    String trainId) {

        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_START_NOTIFICATION);

        ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT)
                .getNotificationData(trainId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NotificationData>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.getMessage());
                    }

                    @Override
                    public void onNext(NotificationData notificationData) {
                        notificationData.setDepartureStationName(departureStationName);
                        notificationData.setDepartureTime(departureTime);
                        notificationData.setDepartureTimeReadable(departureTimeReadable);
                        notificationData.setArrivalStationName(arrivalStationName);
                        notificationData.setArrivalTime(arrivalTime);
                        notificationData.setArrivalTimeReadable(arrivalTimeReadable);
                        Log.d("Notification Data: ", notificationData.toString());

                        intent.putExtra(EXTRA_NOTIFICATION_DATA, new Gson().toJson(notificationData));
                        context.startService(intent);
                    }
                });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Gson gson = new Gson();
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(action);
            if (ACTION_START_NOTIFICATION.equals(action)) {
                final String notificationData = intent.getStringExtra(EXTRA_NOTIFICATION_DATA);
                handleActionStartNotification(gson.fromJson(notificationData, NotificationData.class));
            } else if (ACTION_STOP_NOTIFICATION.equals(action)) {
                handleActionStopNotification();
            } else if (ACTION_UPDATE_NOTIFICATION.equals(action)) {
                final String notificationData = intent.getStringExtra(EXTRA_NOTIFICATION_DATA);
                handleActionUpdateNotification(gson.fromJson(notificationData, NotificationData.class));
            }
        }
    }

    private void handleActionStartNotification(NotificationData notificationData) {
        TrainNotification.notify(this, notificationData, true);
    }

    private void handleActionStopNotification() {
        TrainNotification.cancel(this);
    }

    private void handleActionUpdateNotification(NotificationData notificationData) {
        TrainNotification.notify(this, notificationData, false);
    }
}
