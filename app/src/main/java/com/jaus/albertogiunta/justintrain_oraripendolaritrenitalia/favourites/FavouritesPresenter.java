package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.favourites;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import java.util.LinkedList;
import java.util.List;

public class FavouritesPresenter implements FavouritesContract.Presenter {

    FavouritesContract.View view;
    List<PreferredJourney> preferredJourneys;

    public FavouritesPresenter(FavouritesContract.View view) {
        this.view = view;
        preferredJourneys = new LinkedList<>(PreferredStationsHelper.getAllAsObject(view.getViewContext()));
    }

    @Override
    public void subscribe(BaseView baseView) {
        view  = (FavouritesContract.View) baseView;
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void onResuming(Bundle bundle) {
        if (bundle != null) {
            // no intent expected at this time, therefore this shouldn't happen
        } else {
            updatePreferredJourneys();
        }
    }

    @Override
    public void onLeaving(Bundle bundle) {

    }

    @Override
    public List<PreferredJourney> getPreferredJourneys() {
        return this.preferredJourneys != null ? this.preferredJourneys : new LinkedList<>();
    }

    private void updatePreferredJourneys() {
        preferredJourneys.clear();
        preferredJourneys.addAll(PreferredStationsHelper.getAllAsObject(view.getViewContext()));
        view.updateFavouritesList(this.preferredJourneys);
    }
}
