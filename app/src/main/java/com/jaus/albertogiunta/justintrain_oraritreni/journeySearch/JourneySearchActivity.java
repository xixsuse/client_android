package com.jaus.albertogiunta.justintrain_oraritreni.journeySearch;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraritreni.R;
import com.jaus.albertogiunta.justintrain_oraritreni.journeyResults.JourneyResultsActivity;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST;

import butterknife.BindView;
import butterknife.ButterKnife;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_DATE_CLICK;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_DATE_MINUS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_DATE_PLUS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_REMOVE_FAVOURITE_FROM_SEARCH;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_SEARCH_JOURNEY_FROM_SEARCH;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_SELECT_ARRIVAL;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_SELECT_DEPARTURE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_SET_FAVOURITE_FROM_SEARCH;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_SWAP_STATIONS_FROM_SEARCH;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TIME_CLICK;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TIME_MINUS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TIME_PLUS;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ERROR_NOT_FOUND_STATION;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.SCREEN_SEARCH_JOURNEY;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_CODE_ARRIVAL;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST.I_CODE_DEPARTURE;

public class JourneySearchActivity extends AppCompatActivity implements JourneySearchContract.View {

    JourneySearchContract.Presenter presenter;
    Analytics analytics;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_departure)
    RelativeLayout rlDeparture;
    @BindView(R.id.tv_departure)
    TextView tvDeparture;
    @BindView(R.id.rl_arrival)
    RelativeLayout rlArrival;
    @BindView(R.id.tv_arrival)
    TextView tvArrival;
    @BindView(R.id.btn_swap_station_names)
    ImageButton btnSwapStationNames;
    @BindView(R.id.tv_minus_one_hour)
    TextView tvMinusOneHour;
    @BindView(R.id.tv_plus_one_hour)
    TextView tvPlusOneHour;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_minus_one_day)
    TextView tvMinusOneDay;
    @BindView(R.id.tv_plus_one_day)
    TextView tvPlusOneDay;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_search)
    Button btnSearchJourney;
    @BindView(R.id.btn_add_new_favourite)
    ImageButton btnHeaderToggleFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_search);
        ButterKnife.bind(this);
        analytics = Analytics.getInstance(getViewContext());
        presenter = new JourneySearchPresenter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.tvDeparture.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvArrival.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.rlDeparture.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_SELECT_DEPARTURE);
            onStationNameTextViewClick(I_CODE_DEPARTURE);
        });
        this.rlArrival.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_SELECT_ARRIVAL);
            onStationNameTextViewClick(I_CODE_ARRIVAL);
        });

        btnSwapStationNames.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_SWAP_STATIONS_FROM_SEARCH);
            presenter.onSwapButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString());
        });
        btnHeaderToggleFavorite.setOnClickListener(v -> presenter.onFavouriteButtonClick());

        // TIME PANEL
        tvTime.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_TIME_CLICK);
            onTimeClick();
        });
        tvMinusOneHour.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_TIME_MINUS);
            presenter.onTimeChanged(-1);
        });
        tvPlusOneHour.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_TIME_PLUS);
            presenter.onTimeChanged(1);
        });
        presenter.onTimeChanged(0);
        tvDate.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_DATE_CLICK);
            onDateClick();
        });
        tvMinusOneDay.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_DATE_MINUS);
            presenter.onDateChanged(-1);
        });
        tvPlusOneDay.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_DATE_PLUS);
            presenter.onDateChanged(1);
        });
        presenter.onDateChanged(0);

        btnSearchJourney.setOnClickListener(v -> {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_SEARCH_JOURNEY_FROM_SEARCH);
            presenter.onSearchButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString());
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(presenter.getState(outState));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setState(getIntent().getExtras());
    }

    @Override
    protected void onDestroy() {
        presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void showSnackbar(String message, INTENT_CONST.SNACKBAR_ACTIONS intent) {
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(this.rlDeparture, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case SELECT_DEPARTURE:
                analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ERROR_NOT_FOUND_STATION);
                snackbar.setAction("Seleziona", view -> onStationNameTextViewClick(I_CODE_DEPARTURE))
                        .setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
            case SELECT_ARRIVAL:
                analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ERROR_NOT_FOUND_STATION);
                snackbar.setAction("Seleziona", view -> onStationNameTextViewClick(I_CODE_ARRIVAL))
                        .setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
        }
        snackbar.show();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String stationName = data.getStringExtra("stationName");
            if (requestCode == I_CODE_DEPARTURE) {
                this.tvDeparture.setText(stationName);
                presenter.onDepartureStationNameChanged(stationName);
            } else if (requestCode == I_CODE_ARRIVAL) {
                this.tvArrival.setText(stationName);
                presenter.onArrivalStationNameChanged(stationName);
            }
        }
    }

    @Override
    public void setStationNames(String departureStationName, String arrivalStationName) {
        this.tvDeparture.setText(departureStationName);
        this.tvArrival.setText(arrivalStationName);
    }

    @Override
    public void setTime(String time) {
        tvTime.setText(time);
    }

    @Override
    public void setDate(String date) {
        tvDate.setText(date);
    }


    @Override
    public void onValidSearchParameters() {
        Intent myIntent = new Intent(JourneySearchActivity.this, JourneyResultsActivity.class);
        myIntent.putExtras(presenter.getState(getIntent().getExtras()));
        startActivity(myIntent);
    }

    @Override
    public void onInvalidSearchParameters() {
    }

    private void onTimeClick() {
        TimePickerDialog mTimePicker;
        //noinspection CodeBlock2Expr
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            presenter.onTimeChanged(selectedHour, selectedMinute);
        }, presenter.getSearchDateTime().getHourOfDay(), presenter.getSearchDateTime().getMinuteOfHour(), true);
        mTimePicker.show();
    }

    private void onDateClick() {
        DatePickerDialog mTimePicker;
        //noinspection CodeBlock2Expr
        mTimePicker = new DatePickerDialog(this, (timePicker, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            presenter.onDateChanged(selectedYear, selectedMonth + 1, selectedDayOfMonth);
        }, presenter.getSearchDateTime().getYear(), presenter.getSearchDateTime().getMonthOfYear() - 1, presenter.getSearchDateTime().getDayOfMonth());
        mTimePicker.show();
    }

    private void onStationNameTextViewClick(int code) {
        Intent myIntent = new Intent(JourneySearchActivity.this, StationSearchActivity.class);
        startActivityForResult(myIntent, code);
    }

    @Override
    public void setFavouriteButtonStatus(boolean isPreferred) {
        if (isPreferred) {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_SET_FAVOURITE_FROM_SEARCH);
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_black);
        } else {
            analytics.logScreenEvent(SCREEN_SEARCH_JOURNEY, ACTION_REMOVE_FAVOURITE_FROM_SEARCH);
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_border);
        }
    }
}
