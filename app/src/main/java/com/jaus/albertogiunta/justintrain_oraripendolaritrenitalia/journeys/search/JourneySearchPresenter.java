package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;

import org.joda.time.LocalTime;

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

    private JourneyContract.Search.View mJourneySearchView;

    private List<Station4Database> mObjectList;
    private RealmResults<Station4Database> mRealmObjectList;
    private List<Station4Database> mSearchedStations;
    private int hourOfDay;

    public JourneySearchPresenter(JourneyContract.Search.View journeySearchView) {
        mJourneySearchView = journeySearchView;
        Realm realm = Realm.getDefaultInstance();
        mRealmObjectList = realm.where(Station4Database.class).findAll();
        mSearchedStations = new ArrayList<>(2);
        hourOfDay = LocalTime.now().getHourOfDay();
    }

//    @Override
//    public void subscribe() {
//
//    }
//
//    @Override
//    public void unsubscribe() {
//
//    }

    // TODO modify database to save info about searched stations
    @Override
    public List<String> getLastSearchedStations() {
        mObjectList = mRealmObjectList.where().findAll();
        return Stream.of(mObjectList).map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> searchDbForMatchingStation(String constraint) {
        mObjectList = mRealmObjectList.where().beginsWith("name", constraint, Case.INSENSITIVE).findAll();
        return Stream.of(mObjectList).map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public boolean search(String departureStationName, String arrivalStationName, int hourOfDay) {
        mSearchedStations.clear();

        if (!validateStationName(departureStationName)) {
            mJourneySearchView.showDepartureStationNameError(departureStationName + " Not found!");
            Log.d(arrivalStationName + " not found!");
            return false;
        }

        if (!validateStationName(arrivalStationName)) {
            mJourneySearchView.showArrivalStationNameError(arrivalStationName + " Not found!");
            Log.d(departureStationName + " not found!");
            return false;
        }

        Log.d("searching for: " + mSearchedStations.toString());
        return true;
    }

    private boolean validateStationName(String stationName) {
        mObjectList = mRealmObjectList.where().equalTo("name", stationName, Case.INSENSITIVE).findAll();
        if (mObjectList.size() == 1 && !stationName.isEmpty()) {
            mSearchedStations.add(mObjectList.get(0));
            return true;
        }
        return false;
    }

    @Override
    public List<Station4Database> getSearchedStations() {
        return mSearchedStations;
    }

    @Override
    public int getHourOfDay() {
        return hourOfDay;
    }

}
