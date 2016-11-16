package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

    //  SEARCH
    private JourneySearchContract.Presenter presenter;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_search);
        ButterKnife.bind(this);
        presenter = new JourneySearchPresenter(this);
//        this.btnSwapStations = (ImageButton) findViewById(R.id.btn_swap_station_names);

        this.tvDeparture.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvArrival.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvTime.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        this.tvDate.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        this.rlDeparture.setOnClickListener(v -> onStationNameTextViewClick(I_CODE_DEPARTURE));
        this.rlArrival.setOnClickListener(v -> onStationNameTextViewClick(I_CODE_ARRIVAL));

        btnSwapStationNames.setOnClickListener(v -> presenter.onSwapButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString()));

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
    protected void onSaveInstanceState(Bundle outState) {
        presenter.onLeaving(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.onResuming(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResuming(getIntent().getExtras());
    }

    @Override
    protected void onDestroy() {
        presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String stationName = data.getStringExtra("stationName");
            if (requestCode == I_CODE_DEPARTURE) {
                this.tvDeparture.setText(stationName);
            } else if (requestCode == I_CODE_ARRIVAL) {
                this.tvArrival.setText(stationName);
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
        Log.d("Parameters: VALID");
        Intent myIntent = new Intent(JourneySearchActivity.this, JourneyResultsActivity.class);
        myIntent.putExtras(presenter.getBundle());
        startActivity(myIntent);
    }

    @Override
    public void onInvalidSearchParameters() {
        Log.d("Parameters: NOT VALID");
    }


    private void onTimeClick() {
        TimePickerDialog mTimePicker;
        //noinspection CodeBlock2Expr
        mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            presenter.onTimeChanged(selectedHour, selectedMinute);
        }, presenter.getSearchDateTime().getHourOfDay(), presenter.getSearchDateTime().getMinuteOfHour(), true);
        mTimePicker.setTitle("Selezionare orario di partenza");
        mTimePicker.show();
    }

    private void onDateClick() {
        DatePickerDialog mTimePicker;
        //noinspection CodeBlock2Expr
        mTimePicker = new DatePickerDialog(this, (timePicker, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            presenter.onDateChanged(selectedYear, selectedMonth + 1, selectedDayOfMonth);
        }, presenter.getSearchDateTime().getYear(), presenter.getSearchDateTime().getMonthOfYear() - 1, presenter.getSearchDateTime().getDayOfMonth());
        mTimePicker.setTitle("Selezionare data di partenza");
        mTimePicker.show();
    }

    private void onStationNameTextViewClick(int code) {
        Intent myIntent = new Intent(JourneySearchActivity.this, StationSearchActivity.class);
        startActivityForResult(myIntent, code);
    }

    @Override
    public void showSnackbar(String message, String action, INTENT_C.SNACKBAR_ACTIONS intent) {
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(this.rlDeparture, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case SELECT_DEPARTURE:
                snackbar.setAction(action, view -> onStationNameTextViewClick(I_CODE_DEPARTURE))
                        .setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
            case SELECT_ARRIVAL:
                snackbar.setAction(action, view -> onStationNameTextViewClick(I_CODE_ARRIVAL))
                        .setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
        }
        snackbar.show();
    }
}
