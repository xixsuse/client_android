package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import android.os.Bundle;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_TIME;

public class JourneySearchPresenter implements JourneySearchContract.Presenter {

    private JourneySearchContract.View view;
    private RealmResults<Station4Database> stationList;
    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;

    public JourneySearchPresenter(JourneySearchContract.View view) {
        this.view = view;
        this.stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        dateTime = DateTime.now().withMinuteOfHour(0);
        Log.d(dateTime);
    }

    @Override
    public void subscribe(BaseView baseView) {
        view = (JourneySearchContract.View) baseView;
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void onLeaving(Bundle bundle) {
        // Save value of member in saved state
        bundle.putString(I_STATIONS, new Gson().toJson(new PreferredJourney(departureStation, arrivalStation)));
    }

    @Override
    public void onResuming(Bundle bundle) {
        if (bundle != null) {
            // Restore value of members from saved state
            PreferredJourney journey = new Gson().fromJson(bundle.getString(I_STATIONS), PreferredJourney.class);
            this.departureStation = journey.getStation1();
            this.arrivalStation = journey.getStation2();
            view.setStationNames(departureStation.getName(), arrivalStation.getName());
        } else {
            // Probably initialize members with default values for a new instance
        }
    }

    @Override
    public void onTimeChanged(int delta) {
        dateTime = dateTime.plusHours(delta);
        view.setTime(dateTime.toString(DateTimeFormat.forPattern("HH:mm")));
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM")));
    }

    @Override
    public void onDateChanged(int delta) {
        dateTime = dateTime.plusDays(delta);
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM")));
    }

    @Override
    public List<String> searchStationName(String stationName) {
        return Stream
                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
                .map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public void onSwapButtonClick() {
        if (departureStation != null && arrivalStation != null) {
            PreferredStation temp = departureStation;
            departureStation = arrivalStation;
            arrivalStation = temp;
            view.setStationNames(departureStation.getName(), arrivalStation.getName());
        }
    }

    @Override
    public Bundle getBundle() {
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        bundle.putString(I_STATIONS, gson.toJson(new PreferredJourney(departureStation, arrivalStation)));
        Log.d(bundle.getString(I_STATIONS));
        bundle.putLong(I_TIME, dateTime.getMillis());
        Log.d(dateTime.toString());
        return bundle;
    }

    @Override
    public void onSearchButtonClick(String departureStationName, String arrivalStationName) {
        boolean departureFound = false;
        boolean arrivalFound = false;

        if (PresenterUtilities.isStationNameValid(departureStationName, stationList)) {
            departureStation = new PreferredStation(PresenterUtilities.getStationObject(departureStationName, stationList));
            departureFound = true;
        } else {
            Log.d(arrivalStationName + " not found!");
        }

        if (PresenterUtilities.isStationNameValid(arrivalStationName, stationList)) {
            arrivalStation = new PreferredStation(PresenterUtilities.getStationObject(arrivalStationName, stationList));
            arrivalFound = true;
        } else {
            Log.d(departureStationName + " not found!");
        }

        if (departureFound && arrivalFound) {
            view.onValidSearchParameters();
            Log.d("Searching for: " + departureStation.toString(), arrivalStation.toString());
        }
    }
}
