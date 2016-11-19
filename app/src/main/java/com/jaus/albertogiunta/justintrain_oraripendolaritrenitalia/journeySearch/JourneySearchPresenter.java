package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import com.google.gson.Gson;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_TIME;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils.getStation4DatabaseObject;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.StationRealmUtils.isStationNameValid;

class JourneySearchPresenter implements JourneySearchContract.Presenter {

    private JourneySearchActivity view;
    private RealmResults<Station4Database> stationList;
    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;

    JourneySearchPresenter(JourneySearchActivity view) {
        this.view = view;
        this.stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        dateTime = DateTime.now().withMinuteOfHour(0);
    }

    @Override
    public void unsubscribe() {
        view = null;
    }

    @Override
    public void setState(Bundle bundle) {
        if (bundle != null) {
            // Restore value of members from saved state
            PreferredJourney journey = new Gson().fromJson(bundle.getString(I_STATIONS), PreferredJourney.class);
            this.departureStation = journey.getStation1();
            this.arrivalStation = journey.getStation2();
            view.setStationNames(departureStation.getName(), arrivalStation.getName());
            Log.d("onResuming: resuming bundle", journey.toString());
        } else {
            Log.d("no bundle found");
        }
    }

    @Override
    public Bundle getState(Bundle bundle) {
        Gson gson = new Gson();
        if (bundle == null) bundle = new Bundle();
        bundle.putString(I_STATIONS, gson.toJson(new PreferredJourney(departureStation, arrivalStation)));
        if (departureStation != null)
            Log.d("Current bundled departure stations is: ", this.departureStation);
        if (arrivalStation != null)
            Log.d("Current bundled departure stations is: ", this.arrivalStation);
        bundle.putLong(I_TIME, dateTime.getMillis());
        Log.d("Current bundled DateTime is: ", dateTime);
        return bundle;
    }

    @Override
    public void onTimeChanged(int delta) {
        dateTime = dateTime.plusHours(delta);
        setDateTime();
    }

    @Override
    public void onTimeChanged(int newHour, int newMinute) {
        dateTime = dateTime.withHourOfDay(newHour).withMinuteOfHour(newMinute);
        setDateTime();
    }

    @Override
    public void onDateChanged(int delta) {
        dateTime = dateTime.plusDays(delta);
        setDateTime();
    }

    @Override
    public void onDateChanged(int newYear, int newMonth, int newDay) {
        dateTime = dateTime.withYear(newYear).withMonthOfYear(newMonth).withDayOfMonth(newDay);
        setDateTime();
    }

    @Override
    public DateTime getSearchDateTime() {
        return this.dateTime;
    }

//    @Override
//    public List<String> searchStationName(String stationName) {
//        return Stream
//                .of(stationList.where().beginsWith("name", stationName, Case.INSENSITIVE).findAll())
//                .map(Station4Database::getName).collect(Collectors.toList());
//    }

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
    public void onSearchButtonClick(String departureStationName, String arrivalStationName) {
        if (isStationNameValid(departureStationName, stationList)) {
            departureStation = new PreferredStation(getStation4DatabaseObject(departureStationName, stationList));
        } else {
            view.showSnackbar("Stazione di partenza mancante!", INTENT_C.SNACKBAR_ACTIONS.SELECT_DEPARTURE);
            Log.d(departureStationName + " not found!");
            return;
        }

        if (isStationNameValid(arrivalStationName, stationList)) {
            arrivalStation = new PreferredStation(getStation4DatabaseObject(arrivalStationName, stationList));
        } else {
            view.showSnackbar("Stazione di arrivo mancante!", INTENT_C.SNACKBAR_ACTIONS.SELECT_ARRIVAL);
            Log.d(arrivalStationName + " not found!");
            return;
        }

        view.onValidSearchParameters();
        Log.d("Searching for: " + departureStation.toString(), arrivalStation.toString());
    }

    private void setDateTime() {
        view.setTime(dateTime.toString(DateTimeFormat.forPattern("HH:mm")));
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("EE d MMMM").withLocale(Locale.ITALY)));
    }
}
