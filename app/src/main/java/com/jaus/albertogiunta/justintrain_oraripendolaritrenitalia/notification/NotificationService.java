package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.TrainHeader;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import org.joda.time.DateTime;

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

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
            .create();

    public NotificationService() {
        super("NotificationService");
    }

    public static void startActionStartNotification(Context context, String journeyDepartureStationId, String journeyArrivalStationId, Journey.Solution solution) {
        Log.d("startActionStartNotification:", "start");
        PreferredStationsHelper.log(context, "notification_requested",
                "journeyDepartureStationId " + journeyDepartureStationId
                        + " journeyArrivalStationId " + journeyArrivalStationId
                        + " data: " + new Gson().toJson(solution));

        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_START_NOTIFICATION);
        String trainId;
        if (solution.isHasChanges()) {
            trainId = solution.getChangesList().get(0).getTrainId();
            journeyArrivalStationId = solution.getChangesList().get(0).getArrivalStationName();
        } else {
            trainId = solution.getTrainId();
        }
        getData(journeyDepartureStationId,
                journeyArrivalStationId,
                trainId, context);
    }

    private static void getData(String journeyDepartureStationId, String journeyArrivalStationId, String trainId, Context context) {
        Log.d("getData:");
        ServiceFactory.createRetrofitService(JourneyService.class, JourneyService.SERVICE_ENDPOINT)
                .getDelay(journeyDepartureStationId, journeyArrivalStationId, trainId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TrainHeader>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(e.getMessage());
                    }

                    @Override
                    public void onNext(TrainHeader trainHeader) {
                        TrainNotification.notify(context, trainHeader, true);
                    }
                });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.d(action);
            if (ACTION_START_NOTIFICATION.equals(action)) {
                Log.d("onHandleIntent:", "start");
            } else if (ACTION_STOP_NOTIFICATION.equals(action)) {
                Log.d("onHandleIntent:", "stop");
                TrainNotification.cancel(this);
            } else if (ACTION_UPDATE_NOTIFICATION.equals(action)) {
                Log.d("onHandleIntent:", "update");
                TrainHeader trainHeader = gson.fromJson(intent.getStringExtra(EXTRA_NOTIFICATION_DATA), TrainHeader.class);
                getData(trainHeader.getJourneyDepartureStationId(),
                        trainHeader.getJourneyArrivalStationId(),
                        trainHeader.getTrainId(), getApplicationContext());
            }
        }
    }
}
