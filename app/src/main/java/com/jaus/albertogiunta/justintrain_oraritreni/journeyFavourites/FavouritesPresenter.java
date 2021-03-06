package com.jaus.albertogiunta.justintrain_oraritreni.journeyFavourites;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraritreni.data.Message;
import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraritreni.data.ServerConfig;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.ConfigsNetworkingFactory;
import com.jaus.albertogiunta.justintrain_oraritreni.networking.ConfigsService;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.components.ViewsUtils.COLORS;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.PreferredStationsHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.ServerConfigsHelper;

import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import trikita.log.Log;

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
        final String address = "https://www.justintrain.it";
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
                        Log.d("onError: couldn't retrieve server address, going with ", address);
                        ServerConfig config = new ServerConfig(address, true, 0);
                        ServerConfigsHelper.setAPIEndpoint(view.getViewContext(), config);
                        Log.d("current configuration", ServerConfigsHelper.getAPIEndpoint(view.getViewContext()));
                    }

                    @Override
                    public void onNext(List<ServerConfig> serverConfigs) {
                        ServerConfig config;
                        if (serverConfigs.size() == 0) {
                            Log.d("onError: couldn't retrieve server address, going with ", address);
                            config = new ServerConfig(address, true, 0);
                        } else {
                            Log.d("retrieved ", serverConfigs.toString());
                            config = serverConfigs.get(0);
                            if (config.getAddress() == null) {
                                onError(null);
                                return;
                            }
                        }
                        ServerConfigsHelper.setAPIEndpoint(view.getViewContext(), config);
                        Log.d("current configuration", ServerConfigsHelper.getAPIEndpoint(view.getViewContext()));
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
                        Log.d("onError: ", e.toString());
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
                            case "UPGRADE":
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
