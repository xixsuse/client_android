package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import java.util.LinkedList;
import java.util.List;

class FavouritesPresenter implements FavouritesContract.Presenter {

    private FavouritesContract.View view;
    private List<PreferredJourney> preferredJourneys;

    FavouritesPresenter(FavouritesContract.View view) {
        this.view = view;
        preferredJourneys = new LinkedList<>();
        updatePreferredJourneys();
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
