package com.jaus.albertogiunta.justintrain_oraritreni.journeySearch;

import com.google.gson.Gson;

import android.os.Bundle;

import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraritreni.data.PreferredStation;
import com.jaus.albertogiunta.justintrain_oraritreni.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.DatabaseHelper;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.PreferredStationsHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_TIME;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.DatabaseHelper.getStation4DatabaseObject;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.DatabaseHelper.isStationNameValid;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.helpers.DatabaseHelper.isThisJourneyPreferred;

class JourneySearchPresenter implements JourneySearchContract.Presenter {

    private JourneySearchActivity view;
    private RealmResults<Station4Database> stationList;
    private PreferredStation departureStation;
    private PreferredStation arrivalStation;
    private DateTime dateTime;

    JourneySearchPresenter(JourneySearchActivity view) {
        this.view = view;
        this.stationList = Realm.getDefaultInstance().where(Station4Database.class).findAll();
        dateTime = DateTime.now().minusMinutes(10);
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
            view.setStationNames(departureStation.getNameLong(), arrivalStation.getNameLong());
            setFavouriteButtonStatus();
            Log.d("onResuming: resuming bundle", journey.toString());
        } else {
            if (this.departureStation != null && this.arrivalStation != null) {
                setFavouriteButtonStatus();
            }
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
    public void onDepartureStationNameChanged(String name) {
        departureStation = new PreferredStation(DatabaseHelper.getStation4DatabaseObject(name));
        setFavouriteButtonStatus();
    }

    @Override
    public void onArrivalStationNameChanged(String name) {
        arrivalStation = new PreferredStation(DatabaseHelper.getStation4DatabaseObject(name));
        setFavouriteButtonStatus();
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
//                .map(Station4Database::getNameLong).collect(Collectors.toList());
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
    public void onFavouriteButtonClick() {
        if (isDataValid()) {
            if (isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
                PreferredStationsHelper.removePreferredJourney(view.getViewContext(),
                        departureStation,
                        arrivalStation);
                setFavouriteButtonStatus();
                view.showSnackbar("Tratta rimossa dai Preferiti", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
            } else {
                if (PreferredStationsHelper.isPossibleToSaveMore(view.getViewContext())) {
                    PreferredStationsHelper.setPreferredJourney(view.getViewContext(),
                            new PreferredJourney(departureStation, arrivalStation));
                    setFavouriteButtonStatus();
                    view.showSnackbar("Tratta aggiunta ai Preferiti", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                } else {
                    view.showSnackbar("Impossibile salvare più di 10 tratte nei Preferiti!", INTENT_CONST.SNACKBAR_ACTIONS.NONE);
                }
            }
        }
    }

    @Override
    public void setFavouriteButtonStatus() {
        if (isThisJourneyPreferred(departureStation, arrivalStation, view.getViewContext())) {
            view.setFavouriteButtonStatus(true);
        } else {
            view.setFavouriteButtonStatus(false);
        }
    }

    @Override
    public void onSearchButtonClick(String departureStationName, String arrivalStationName) {
        if (isDataValid()) {
            view.onValidSearchParameters();
            Log.d("Searching for: " + departureStation.toString(), arrivalStation.toString());
        }
    }

    private void setDateTime() {
        view.setTime(dateTime.toString(DateTimeFormat.forPattern("HH:mm")));
        view.setDate(dateTime.toString(DateTimeFormat.forPattern("EE d MMM").withLocale(Locale.ITALY)));
    }

    private boolean isDataValid() {
        if (departureStation != null && isStationNameValid(departureStation.getNameShort(), stationList)) {
            departureStation = new PreferredStation(getStation4DatabaseObject(departureStation.getNameShort(), stationList));
        } else {
            view.showSnackbar("Stazione di partenza mancante!", INTENT_CONST.SNACKBAR_ACTIONS.SELECT_DEPARTURE);
            if (departureStation != null) Log.d(departureStation.getNameShort() + " not found!");
            return false;
        }

        if (arrivalStation != null && isStationNameValid(arrivalStation.getNameShort(), stationList)) {
            arrivalStation = new PreferredStation(getStation4DatabaseObject(arrivalStation.getNameShort(), stationList));
        } else {
            view.showSnackbar("Stazione di arrivo mancante!", INTENT_CONST.SNACKBAR_ACTIONS.SELECT_ARRIVAL);
            if (arrivalStation != null) Log.d(arrivalStation.getNameShort() + " not found!");
            return false;
        }

        return true;
    }
}
