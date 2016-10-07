package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_CODE_ARRIVAL;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_CODE_DEPARTURE;

public class JourneySearchActivity extends AppCompatActivity implements JourneySearchContract.View {

    //  SEARCH
    private JourneySearchContract.Presenter presenter;

    @BindView(R.id.tv_departure) TextView tvDeparture;
    @BindView(R.id.tv_arrival) TextView tvArrival;
    @BindView(R.id.btn_swap_station_names) ImageButton btnSwapStationNames;
    @BindView(R.id.tv_minus_one_hour) TextView tvMinusOneHour;
    @BindView(R.id.tv_plus_one_hour) TextView tvPlusOneHour;
    @BindView(R.id.tv_time) TextView tvTime;
    @BindView(R.id.tv_minus_one_day) TextView tvMinusOneDay;
    @BindView(R.id.tv_plus_one_day) TextView tvPlusOneDay;
    @BindView(R.id.tv_date) TextView tvDate;
    @BindView(R.id.btn_search) Button btnSearchJourney;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_search);
        ButterKnife.bind(this);
        presenter = new JourneySearchPresenter(this);
//        this.btnSwapStations = (ImageButton) findViewById(R.id.btn_swap_station_names);

        onStationNameTextViewClick(this.tvDeparture, I_CODE_DEPARTURE);
        onStationNameTextViewClick(this.tvArrival, I_CODE_ARRIVAL);

        btnSwapStationNames.setOnClickListener(v -> presenter.onSwapButtonClick(tvDeparture.getText().toString(), tvArrival.getText().toString()));

        // TIME PANEL
        tvMinusOneHour.setOnClickListener(v -> presenter.onTimeChanged(-1));
        tvPlusOneHour.setOnClickListener(v -> presenter.onTimeChanged(1));
        presenter.onTimeChanged(0);
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
        JourneySearchActivity.this.startActivity(myIntent);
    }

    @Override
    public void onInvalidSearchParameters() {
        Log.d("Parameters: NOT VALID");
    }

    private void onStationNameTextViewClick(TextView tv, int code) {
        tv.setOnClickListener(view -> {
            Intent myIntent = new Intent(JourneySearchActivity.this, StationSearchActivity.class);
            JourneySearchActivity.this.startActivityForResult(myIntent, code);
        });
    }
}
