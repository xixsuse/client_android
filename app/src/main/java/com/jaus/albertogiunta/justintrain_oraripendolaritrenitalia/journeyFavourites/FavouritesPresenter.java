package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import android.os.Bundle;
import android.util.Log;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.MessageService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.Service2Factory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private List<PreferredJourney> preferredJourneys;
    private List<Message> messages;
    private Timer timer;
    private Random random = new Random(42);
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(android.os.Message msg) {
//            if (messages != null) {
//                Message m = messages.get(random.nextInt(messages.size()));
//                Log.d("handleMessage: ", m.toString());
//                view.updateDashboard(m);
//            }
//        }
//    };

    FavouritesPresenter(FavouritesContract.View view) {
        this.view = view;
        preferredJourneys = new LinkedList<>();
        updatePreferredJourneys();
        updateAllMessages();
    }

    @Override
    public void subscribe(BaseView baseView) {
        view = (FavouritesContract.View) baseView;
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void onResuming(Bundle bundle) {
//        startTimer();
        updateRequested();
    }

    @Override
    public void onLeaving(Bundle bundle) {
    }

    @Override
    public List<PreferredJourney> getPreferredJourneys() {
        return this.preferredJourneys;
    }

    @Override
    public void updateAllMessages() {
        Service2Factory.createRetrofitService(MessageService.class, MessageService.SERVICE_ENDPOINT)
                .getAllMessages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Message>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Message> msg) {
                        messages = msg;
                        Log.d("onNext: ", msg.toString());
                        Message m = messages.get(random.nextInt(messages.size()));
                        view.updateDashboard(m);
//                        startTimer();
                    }
                });
    }

    //    private void startTimer() {
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//    public void run() {
//                mHandler.obtainMessage(1).sendToTarget();
//            }
//        }, 0, 10000);
//    }


//        @Override
//        public void stopTimer () {
//            timer.cancel();
//        }

    @Override
    public void updateRequested() {
        updatePreferredJourneys();
        if (this.preferredJourneys != null && this.preferredJourneys.size() > 0) {
            view.displayFavouriteJourneys();
            view.updateFavouritesList(this.preferredJourneys);
        } else {
            view.displayEntryButton();
        }
    }

    private void updatePreferredJourneys() {
        preferredJourneys.clear();
        List<PreferredJourney> pj = PreferredStationsHelper.getAllAsObject(view.getViewContext());
        if (pj != null) {
            preferredJourneys.addAll(pj);
        }
    }
}
