package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

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

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import butterknife.BindView;
import butterknife.ButterKnife;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_CODE_ARRIVAL;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_CODE_DEPARTURE;

public class JourneySearchActivity extends AppCompatActivity implements JourneySearchContract.View {

    private JourneySearchContract.Presenter presenter;

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
        presenter = new JourneySearchPresenter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.tvDeparture.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvArrival.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.rlDeparture.setOnClickListener(v -> onStationNameTextViewClick(I_CODE_DEPARTURE));
        this.rlArrival.setOnClickListener(v -> onStationNameTextViewClick(I_CODE_ARRIVAL));

        btnSwapStationNames.setOnClickListener(v -> presenter.onSwapButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString()));
        btnHeaderToggleFavorite.setOnClickListener(v -> presenter.onFavouriteButtonClick());

        // TIME PANEL
        tvTime.setOnClickListener(v -> onTimeClick());
        tvMinusOneHour.setOnClickListener(v -> presenter.onTimeChanged(-1));
        tvPlusOneHour.setOnClickListener(v -> presenter.onTimeChanged(1));
        presenter.onTimeChanged(0);
        tvDate.setOnClickListener(v -> onDateClick());
        tvMinusOneDay.setOnClickListener(v -> presenter.onDateChanged(-1));
        tvPlusOneDay.setOnClickListener(v -> presenter.onDateChanged(1));
        presenter.onDateChanged(0);

        btnSearchJourney.setOnClickListener(v -> presenter.onSearchButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString()));
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
    public void showSnackbar(String message, INTENT_C.SNACKBAR_ACTIONS intent) {
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(this.rlDeparture, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case SELECT_DEPARTURE:
                snackbar.setAction("Seleziona", view -> onStationNameTextViewClick(I_CODE_DEPARTURE))
                        .setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
            case SELECT_ARRIVAL:
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
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_black);
        } else {
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_border);
        }
    }
}
