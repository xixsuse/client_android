package com.jaus.albertogiunta.justintrain_oraritreni.journeyFavourites;

import com.google.gson.Gson;

import android.os.Bundle;
import android.util.Log;

import com.jaus.albertogiunta.justintrain_oraritreni.data.Message;
import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraritreni.data.ServerConfig;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.ConfigsNetworkingFactory;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.ConfigsService;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils.COLORS;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.PreferredStationsHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.SharedPreferencesHelper;

import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_SERVER_CONFIG;

class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private List<PreferredJourney> preferredJourneys;
    private List<Message> messages;

    FavouritesPresenter(FavouritesContract.View view) {
        this.view = view;
        preferredJourneys = new LinkedList<>();
        updatePreferredJourneys();
        updateServerConfigs();
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

    private void updateServerConfigs() {
        final String address = "46.101.130.226:8081";
        ConfigsNetworkingFactory.createRetrofitService(ConfigsService.class, ConfigsService.SERVICE_ENDPOINT)
                .getAllServerConfigs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ServerConfig>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ServerConfig config = new ServerConfig(address, true, 0);
                        SharedPreferencesHelper.setSharedPreferenceObject(view.getViewContext(), I_SERVER_CONFIG, new Gson().toJson(config));
                    }

                    @Override
                    public void onNext(List<ServerConfig> serverConfigs) {
                        ServerConfig config;
                        if (serverConfigs.size() == 0) {
                            config = new ServerConfig(address, true, 0);
                        } else {
                            config = serverConfigs.get(0);
                        }
                        SharedPreferencesHelper.setSharedPreferenceObject(view.getViewContext(), I_SERVER_CONFIG, new Gson().toJson(config));
                    }
                });
    }

    private void updateAllMessages() {
        ConfigsNetworkingFactory.createRetrofitService(ConfigsService.class, ConfigsService.SERVICE_ENDPOINT)
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
                        Log.d("onNext: ", m.toString());
                        switch (m.getCategory()) {
                            case "STRIKE":
                                view.updateDashboard(m, COLORS.RED);
                                break;
                            case "WARNING":
                                view.updateDashboard(m, COLORS.ORANGE);
                                break;
                            case "TIP":
                                view.updateDashboard(m, COLORS.GREEN);
                                break;
                            case "UPDATE":
                                view.updateDashboard(m, COLORS.BLUE);
                                break;
                            default:
                                view.hideDashboard();
                                break;
                        }
                    }
                });
    }

    private void updatePreferredJourneys() {
        preferredJourneys.clear();
        List<PreferredJourney> preferredJourneys = PreferredStationsHelper.getAllAsObject(view.getViewContext());
        if (preferredJourneys != null) {
            this.preferredJourneys.addAll(preferredJourneys);
        }
    }
}
