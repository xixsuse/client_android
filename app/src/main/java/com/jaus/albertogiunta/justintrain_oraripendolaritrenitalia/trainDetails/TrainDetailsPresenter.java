package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.trainDetails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Train;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.PostProcessingEnabler;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.ServiceFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.TrainService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_SOLUTION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;

class TrainDetailsPresenter implements TrainDetailsContract.Presenter {

    private TrainDetailsContract.View view;
    private List<Train> trainList;
    private List<Object> trainStopList;
    private Journey.Solution solution;
    private PreferredJourney preferredJourney;

    TrainDetailsPresenter(TrainDetailsContract.View view) {
        this.view = view;
        trainList = new ArrayList<>();
        trainStopList = new ArrayList<>();
    }

    @Override
    public void setState(Bundle bundle) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeAdapter())
                .registerTypeAdapterFactory(new PostProcessingEnabler())
                .create();

        if (bundle != null) {
            // Restore value of members from saved state
            if (bundle.getString(I_SOLUTION) != null) {
                solution = gson.fromJson(bundle.getString(I_SOLUTION), Journey.Solution.class);
                if (solution.hasChanges()) {
                    Arrays.copyOf(trainList.toArray(), solution.getChangesList().size());
                }
                Log.d("Current bundled solution is: ", solution.toString());
            }
            if (bundle.getString(I_STATIONS) != null) {
                preferredJourney = gson.fromJson(bundle.getString(I_STATIONS), PreferredJourney.class);
                Log.d("Current bundled preferred journey is: ", preferredJourney.toString());
            }
        } else {
            Log.d("no bundle found");
        }
    }

    @Override
    public Bundle getState(Bundle bundle) {
        return bundle;
    }

    @Override
    public void unsubscribe() {
        this.view = null;
    }

    @Override
    public void updateRequested() {
        view.showProgress();
        List<String> trainIdList = new LinkedList<>();
        if (solution.hasChanges()) {
            for (Journey.Solution.Change c : solution.getChangesList()) {
                trainIdList.add(c.getTrainId());
            }
        } else {
            trainIdList.add(solution.getTrainId());
        }
        Observable.concatDelayError(Observable.from(searchTrainDetails(trainIdList))).subscribe(new Subscriber<Train>() {
            @Override
            public void onCompleted() {
                getFlatTrainList();
                view.hideProgress();
                view.updateTrainDetails();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Train train) {
                Log.d("onNext: ", train.toString());
                trainList.add(train);
            }
        });
    }

    @Override
    public void refreshRequested() {
        trainList.clear();
        trainStopList.clear();
        updateRequested();
    }

    private Iterable<Observable<Train>> searchTrainDetails(List<String> trainId) {
        Log.d("searchTrainDetails: searching train with id:", trainId);
        List<Observable<Train>> o = new LinkedList<>();
        for (String s : trainId) {
            o.add(ServiceFactory.createRetrofitService(TrainService.class, TrainService.SERVICE_ENDPOINT).getTrainDetails(s)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()));
        }
        return o;
    }

    @Override
    public List<Object> getFlatTrainList() {
        trainStopList.clear();
        for (int i = 0; i < trainList.size(); i++) {
            Train t = new Train(trainList.get(i));
            List<Train.Stop> sl = t.getStops() != null ? t.getStops() : new LinkedList<>();
            t.setStops(null);
            trainStopList.add(t);
            trainStopList.addAll(sl);
        }
        Log.w("getFlatTrainList: ", trainStopList.toString());
        return trainStopList;
    }

    @Override
    public Train getTrainForAdapterPosition(int position) {
        int past = 0;
        for (Train t : trainList) {
            if (position <= past + t.getStops().size()) {
                return t;
            }
            past += t.getStops().size() + 1;
        }
        return null;
    }

    @Override
    public void onNotificationRequested(int position) {
        Log.d("onNotificationRequested: ", position);
        NotificationService.startActionStartNotification(view.getViewContext(),
                preferredJourney.getStation1(),
                preferredJourney.getStation2(),
                solution,
                getIndexOfTrainFromPosition(position));
    }

    private Integer getIndexOfTrainFromPosition(int position) {
        int pos = position;
        for (Train t : trainList) {
            if (position == 0) return 0;
            if (pos - t.getStops().size() < 0) {
                return pos;
            }
            pos -= t.getStops().size();
        }
        return null;
    }
}
