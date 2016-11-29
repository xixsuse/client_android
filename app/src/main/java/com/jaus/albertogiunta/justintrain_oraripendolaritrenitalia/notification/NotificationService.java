package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.TrainHeader;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.APINetworkingFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.JourneyService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.ConfigsHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils;

import org.joda.time.DateTime;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_REFRESH_NOTIFICAITON;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_REMOVE_NOTIFICATION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.SCREEN_NOTIFICATION;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread
 */
public class NotificationService extends IntentService {
    public static final String ACTION_START_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.START_NOTIFICATION";
    public static final String ACTION_STOP_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.STOP_NOTIFICATION";
    public static final String ACTION_UPDATE_NOTIFICATION = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.action.UPDATE_NOTIFICATION";
    public static final String EXTRA_NOTIFICATION_DATA = "com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.extra.NOTIFICATION_DATA";

    public static PreferredJourney preferredJourney;
    public static Journey.Solution solution;

    Analytics analytics;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
            .create();

    public NotificationService() {
        super("NotificationService");
        analytics = Analytics.getInstance(this);
    }

    public static void startActionStartNotification(Context context, PreferredStation journeyDepartureStation, PreferredStation journeyArrivalStation, Journey.Solution sol, Integer indexOfJourneyToBeNotified) {
        Log.d("startActionStartNotification:", "start");

        String trainId;
        String journeyDepartureStationId = journeyDepartureStation.getStationShortId();
        String journeyArrivalStationId = journeyArrivalStation.getStationShortId();
        Intent intent = new Intent(context, NotificationService.class);

        preferredJourney = new PreferredJourney(journeyArrivalStation, journeyArrivalStation);
        solution = sol;

        intent.setAction(ACTION_START_NOTIFICATION);

        if (sol.hasChanges() && indexOfJourneyToBeNotified != null) {
            trainId = sol.getChangesList().get(indexOfJourneyToBeNotified).getTrainId();
            journeyDepartureStationId = StationRealmUtils.getStation4DatabaseObject(sol.getChangesList().get(indexOfJourneyToBeNotified).getDepartureStationName()).getStationShortId();
            journeyArrivalStationId = StationRealmUtils.getStation4DatabaseObject(sol.getChangesList().get(indexOfJourneyToBeNotified).getArrivalStationName()).getStationShortId();
        } else {
            trainId = sol.getTrainId();
        }
        getData(journeyDepartureStationId,
                journeyArrivalStationId,
                trainId, context);
    }

    private static void getData(String journeyDepartureStationId, String journeyArrivalStationId, String trainId, Context context) {
        Log.d("getData:");
        APINetworkingFactory.createRetrofitService(JourneyService.class, ConfigsHelper.getAPIEndpoint(context))
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
//                analytics.logScreenEvent(SCREEN_NOTIFICATION, ACTION_START_NOTIFICATION);
                Log.d("onHandleIntent:", "start");
            } else if (ACTION_STOP_NOTIFICATION.equals(action)) {
                analytics.logScreenEvent(SCREEN_NOTIFICATION, ACTION_REMOVE_NOTIFICATION);
                Log.d("onHandleIntent:", "stop");
                TrainNotification.cancel(this);
            } else if (ACTION_UPDATE_NOTIFICATION.equals(action)) {
                analytics.logScreenEvent(SCREEN_NOTIFICATION, ACTION_REFRESH_NOTIFICAITON);
                Log.d("onHandleIntent:", "update");
                TrainHeader trainHeader = gson.fromJson(intent.getStringExtra(EXTRA_NOTIFICATION_DATA), TrainHeader.class);
                getData(trainHeader.getJourneyDepartureStationId(),
                        trainHeader.getJourneyArrivalStationId(),
                        trainHeader.getTrainId(), getApplicationContext());
            }
        }
    }
}
