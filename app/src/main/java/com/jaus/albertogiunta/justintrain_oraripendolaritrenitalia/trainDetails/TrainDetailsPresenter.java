package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.trainDetails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Train;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.APINetworkingFactory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.DateTimeAdapter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.PostProcessingEnabler;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.TrainService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.notification.NotificationService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.NetworkingHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.NotificationDataHelper;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.ServerConfigsHelper;

import org.joda.time.DateTime;

import java.net.ConnectException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST.I_SOLUTION;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST.I_STATIONS;

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
                if (solution == null) {
                    // todo fai salvataggio dell'ultimo notified nei preferiti e ripescalo da qui<
                } else {
                    if (solution.hasChanges()) {
                        Arrays.copyOf(trainList.toArray(), solution.getChangesList().size());
                    }
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
            public void onError(Throwable exception) {
                Log.d(exception.getMessage());
                if (view != null) {
                    if (exception.getMessage().equals("HTTP 404 ")) {
                        view.showErrorMessage("La tratta inserita non ha viaggi disponibili", "Torna alle soluzioni", INTENT_CONST.ERROR_BTN.NO_SOLUTIONS);
                    } else {
                        Log.e("onServerError: ", exception.toString());
                        if (exception instanceof HttpException) {
                            Log.d(((HttpException) exception).response().errorBody(), ((HttpException) exception).response().code());
                            if (((HttpException) exception).response().code() == 500) {
                                view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_CONST.ERROR_BTN.SEND_REPORT);
                            }
                        } else if (exception instanceof ConnectException) {
                            if (NetworkingHelper.isNetworkAvailable(view.getViewContext())) {
                                view.showErrorMessage("Il server sta avendo dei problemi", "Segnala il problema", INTENT_CONST.ERROR_BTN.SEND_REPORT);
                            } else {
                                view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_CONST.ERROR_BTN.CONN_SETTINGS);
                            }
                        } else if (exception instanceof SocketException) {
                            view.showErrorMessage("Assicurati di essere connesso a Internet", "Attiva connessione", INTENT_CONST.ERROR_BTN.CONN_SETTINGS);
                        }
                    }
                }
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

    @Override
    public Journey.Solution getSolution() {
        return this.solution;
    }

    private Iterable<Observable<Train>> searchTrainDetails(List<String> trainId) {
        Log.d("searchTrainDetails: searching train with id:", trainId);
        List<Observable<Train>> o = new LinkedList<>();
        for (String s : trainId) {
            o.add(APINetworkingFactory.createRetrofitService(TrainService.class, ServerConfigsHelper.getAPIEndpoint(view.getViewContext())).getTrainDetails(s)
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
    public Integer getTrainIndexForAdapterPosition(int position) {
        int past = 0;
        for (Train t : trainList) {
            if (position <= past + t.getStops().size()) {
                return trainList.indexOf(t);
            }
            past += t.getStops().size() + 1;
        }
        return null;
    }

    @Override
    public void onNotificationRequested(int position) {
        NotificationDataHelper.setNotificationData(view.getViewContext(), preferredJourney, solution);
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
