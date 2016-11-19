package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import android.os.Bundle;
import android.util.Log;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.MessageService;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.networking.Service2Factory;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private List<PreferredJourney> preferredJourneys;
    private List<Message> messages;

    FavouritesPresenter(FavouritesContract.View view) {
        this.view = view;
        preferredJourneys = new LinkedList<>();
        updatePreferredJourneys();
        updateAllMessages();
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void setState(Bundle bundle) {
        updatePreferredJourneys();
        if (this.preferredJourneys != null && this.preferredJourneys.size() > 0) {
            view.displayFavouriteJourneys();
            view.updateFavouritesList();
        } else {
            view.displayEntryButton();
        }
    }

    @Override
    public Bundle getState(Bundle bundle) {
        return bundle;
    }

    @Override
    public List<PreferredJourney> getPreferredJourneys() {
        return this.preferredJourneys;
    }

    private void updateAllMessages() {
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
                        Message m = messages.get(0);
                        view.updateDashboard(m);
                    }
                });
    }

    private void updatePreferredJourneys() {
        preferredJourneys.clear();
        List<PreferredJourney> pj = PreferredStationsHelper.getAllAsObject(view.getViewContext());
        if (pj != null) {
            preferredJourneys.addAll(pj);
        }
    }
}
