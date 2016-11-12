package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import com.google.gson.Gson;

import android.os.Bundle;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;
import java.util.Locale;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_TIME;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils.getStation4DatabaseObject;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils.isStationNameValid;

class JourneySearchPresenter implements JourneySearchContract.Presenter {

    private JourneySearchContract.View view;
    private RealmResults<Station4Database> stationList;
    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;

    JourneySearchPresenter(JourneySearchContract.View view) {
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
            Log.d("no bundle found");
            // Probably initialize members with default values for a new instance
        }
    }

    @Override
    public void onTimeChanged(int delta) {
        dateTime = dateTime.plusHours(delta);
        view.setTime(dateTime.toString(DateTimeFormat.forPattern("HH:mm")));
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM").withLocale(Locale.ITALY)));
    }

    @Override
    public void onTimeChanged(int newHour, int newMinute) {
        dateTime = dateTime.withHourOfDay(newHour).withMinuteOfHour(newMinute);
        view.setTime(dateTime.toString(DateTimeFormat.forPattern("HH:mm")));
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM").withLocale(Locale.ITALY)));
    }

    @Override
    public void onDateChanged(int delta) {
        dateTime = dateTime.plusDays(delta);
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM").withLocale(Locale.ITALY)));
    }

    @Override
    public void onDateChanged(int newYear, int newMonth, int newDay) {
        dateTime = dateTime.withYear(newYear).withMonthOfYear(newMonth).withDayOfMonth(newDay);
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("d MMMM").withLocale(Locale.ITALY)));
    }

    @Override
    public DateTime getSearchDateTime() {
        return this.dateTime;
    }

    @Override
    public List<String> searchStationName(String stationName) {
        return Stream
                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
                .map(Station4Database::getName).collect(Collectors.toList());
    }

    @Override
    public void onSwapButtonClick(String departure, String arrival) {
        if (!(departure.equals("Seleziona") && arrival.equals("Seleziona"))) {
            PreferredStation temp = departureStation;
            departureStation = arrivalStation;
            arrivalStation = temp;
            view.setStationNames(arrival, departure);
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

        if (isStationNameValid(departureStationName, stationList)) {
            departureStation = new PreferredStation(getStation4DatabaseObject(departureStationName, stationList));
            departureFound = true;
        } else {
            Log.d(arrivalStationName + " not found!");
        }

        if (isStationNameValid(arrivalStationName, stationList)) {
            arrivalStation = new PreferredStation(getStation4DatabaseObject(arrivalStationName, stationList));
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
