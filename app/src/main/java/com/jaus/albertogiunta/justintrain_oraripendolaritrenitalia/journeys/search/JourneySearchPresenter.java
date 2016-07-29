package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import trikita.log.Log;

/**
 * Created by albertogiunta on 25/05/16.
 */
public class JourneySearchPresenter implements JourneyContract.Search.Presenter {

    private JourneyContract.Search.View journeySearchView;

    private List<Station4Database> objectList;
    private RealmResults<Station4Database> realmObjectList;
    private List<Station4Database> searchedStations;

    private int originalHourOfDay;
    private int hourOfDay;

    public JourneySearchPresenter(JourneyContract.Search.View journeySearchView) {
        this.journeySearchView = journeySearchView;

        Realm realm = Realm.getDefaultInstance();
        realmObjectList = realm.where(Station4Database.class).findAll();
        searchedStations = new ArrayList<>(2);

        originalHourOfDay = LocalDateTime.now().getHourOfDay();
        hourOfDay = originalHourOfDay;
    }


    @Override
    public boolean isThisJourneyPreferred() {
        return searchedStations.size() > 0 &&
                PreferredStationsHelper.isJourneyAlreadyPreferred(journeySearchView.getViewContext(), searchedStations);
    }

    @Override
    public void toggleFavouriteJourneyButton() {
        Log.d(PreferredStationsHelper.getAll(journeySearchView.getViewContext()).toString());
        if (!isThisJourneyPreferred()) {
            journeySearchView.togglePreferredJourneyButton(false);
        } else {
            journeySearchView.togglePreferredJourneyButton(true);
        }
    }

    @Override
    public void toggleFavouriteJourneyOnClick() {
        if (!isThisJourneyPreferred()) {
            PreferredStationsHelper.setPreferredJourney(journeySearchView.getViewContext(), new PreferredJourney(searchedStations));
            journeySearchView.togglePreferredJourneyButton(true);
        } else {
            PreferredStationsHelper.removePreferredJourney(journeySearchView.getViewContext(), searchedStations);
            journeySearchView.togglePreferredJourneyButton(false);
        }
    }

    @Override
    // TODO modify database to save info about searched stations
    public List<String> getRecentStations() {
        objectList = realmObjectList.where().findAll();
        return Stream.of(objectList).map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> searchDbForMatchingStation(String constraint) {
        objectList = realmObjectList.where().beginsWith("name", constraint, Case.INSENSITIVE).findAll();
        return Stream.of(objectList).map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public boolean search(String departureStationName, String arrivalStationName) {
        searchedStations.clear();

        if (!validateStationName(departureStationName)) {
            journeySearchView.showDepartureStationNameError(departureStationName + " not found!");
            Log.d(arrivalStationName + " not found!");
            return false;
        }

        if (!validateStationName(arrivalStationName)) {
            journeySearchView.showArrivalStationNameError(arrivalStationName + " not found!");
            Log.d(departureStationName + " not found!");
            return false;
        }

        Log.d("Searching for: " + searchedStations.toString());
        return true;
    }

    @Override
    public List<Station4Database> getSearchedStations() {
        return searchedStations;
    }

    @Override
    public int getHourOfDay() {
        return hourOfDay;
    }

    @Override
    public boolean userHasModifiedTime() {
        return originalHourOfDay != hourOfDay;
    }

    @Override
    public void changeTime(int delta) {
        hourOfDay += delta;
        if (delta != 0) {
            if (hourOfDay < 0) {
                hourOfDay = 23;
            } else if (hourOfDay > 24) {
                hourOfDay = 1;
            }
        }
        journeySearchView.setTime((hourOfDay < 10 ? "0" + hourOfDay : Integer.toString(hourOfDay)).concat(":00"));
    }


    private boolean validateStationName(String stationName) {
        objectList = realmObjectList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll();
        if (objectList.size() == 1 && !stationName.isEmpty()) {
            searchedStations.add(objectList.get(0));
            return true;
        }
        return false;
    }
}
