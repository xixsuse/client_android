package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import trikita.log.Log;

public class JourneyResultsActivity extends AppCompatActivity implements JourneyResultsContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rl_header_2)
    RelativeLayout rlHeader2;

    @BindView(R.id.tv_departure_station_name)
    TextView tvHeaderDepartureStation;
    @BindView(R.id.tv_arrival_station_name)
    TextView tvHeaderArrivalStation;
    @BindView(R.id.btn_header_swap_station_names)
    ImageButton btnHeaderSwapStationNames;
    @BindView(R.id.btn_toggle_favourite)
    ImageButton btnHeaderToggleFavorite;

    @BindView(R.id.loading_spinner)
    ProgressBar progressBar;

    // NO SOLUTION FOUND
    @BindView(R.id.rl_error)
    RelativeLayout rlEmptyJourneyBox;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.btn_error_button)
    Button btnErrorMessage;

    //  RESULTS
//    @BindView(R.id.rl_journey_solutions)
//    RelativeLayout rlJourneySolutions;
    @BindView(R.id.rv_journey_solutions)
    RecyclerView rvJourneySolutions;
    @BindView(R.id.btn_refresh)
    ImageButton btnRefresh;

    JourneyResultsAdapter mJourneyResultsAdapter;
    JourneyResultsPresenter presenter;
//    Bundle state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_results);
        ButterKnife.bind(this);
        presenter = new JourneyResultsPresenter(this);

        setSupportActionBar(toolbar);
//        toolbar.setTitle("Soluzioni");
//        getSupportActionBar().setElevation(0);

//        rlHeader2.setOnClickListener(v -> presenter.getFirstFeasableSolution());
        btnHeaderSwapStationNames.setOnClickListener(v-> presenter.onSwapButtonClick());
        btnHeaderToggleFavorite.setOnClickListener(v -> presenter.onFavouriteButtonClick());

        mJourneyResultsAdapter = new JourneyResultsAdapter(this, presenter);
        rvJourneySolutions.setAdapter(mJourneyResultsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnRefresh.setOnClickListener(v -> presenter.searchFromSearch(true));
        presenter.onResuming(getIntent().getExtras());
        presenter.searchFromSearch(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("ONSAVEINSTANCESTATE");
//        presenter.onLeaving(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // non viene mai chiamato praticamente
        Log.d("ONRESTOREINSTANCESTATE");
//        super.onRestoreInstanceState(savedInstanceState);
        presenter.onResuming(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ONRESUME");
        // restore RecyclerView state
//        if (state != null) {
//            Parcelable listState = state.getParcelable(S_STATION_LIST);
//            rvJourneySolutions.getLayoutManager().onRestoreInstanceState(listState);
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ONRESTART");
    }

    @Override
    protected void onPause() {
        Log.d("ONPAUSE");
        // save RecyclerView state
//        state = new Bundle();
//        Parcelable rvState = rvJourneySolutions.getLayoutManager().onSaveInstanceState();
//        state.putParcelable(S_STATION_LIST, rvState);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("ONSTOP");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("ONDESTROY");
        presenter.unsubscribe();
        super.onDestroy();
    }

    @Override
    public Context getViewContext() {
        return getApplicationContext();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        rvJourneySolutions.setVisibility(View.GONE);
        rlEmptyJourneyBox.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        rvJourneySolutions.setVisibility(View.VISIBLE);
    }

    @Override
    public void showErrorMessage(String tvMessage, String btnMessage, INTENT_C.ERROR_BTN intent) {
        progressBar.setVisibility(View.GONE);
        rvJourneySolutions.setVisibility(View.GONE);
        rlEmptyJourneyBox.setVisibility(View.VISIBLE);
        btnErrorMessage.setText(btnMessage);
        tvErrorMessage.setText(tvMessage);
        btnErrorMessage.setOnClickListener(v -> {
            switch (intent) {
                case CONN_SETTINGS:
                    Log.d("intent a settings");
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(i);
                    break;
                case SEND_REPORT:
                    Log.d("intent a report");
                    break;
                case NO_SOLUTIONS:
                    Log.d("intent a ricerca");
                    break;
            }
        });
    }

//    @Override
//    public void scrollToFirstFeasibleSolution(int position) {
//        Log.d("scrolling to ", position);
//        ((LinearLayoutManager) rvJourneySolutions.getLayoutManager()).scrollToPositionWithOffset(position, 0);
//    }

    @Override
    public void updateSolutionsList(List<Journey.Solution> solutionList) {
        Log.d("Successfully updated list");
        rvJourneySolutions.getRecycledViewPool().clear();
        mJourneyResultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSolution(int elementIndex) {
        Log.d("Successfully updated item");
        mJourneyResultsAdapter.notifyItemChanged(elementIndex + 1);
    }

    @Override
    public void setFavouriteButtonStatus(boolean isPreferred) {
        if (isPreferred) {
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_black);
        } else {
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_border);
        }
    }

    @Override
    public void setStationNames(String departure, String arrival) {
        this.tvHeaderDepartureStation.setText(departure);
        this.tvHeaderArrivalStation.setText(arrival);
    }

    @Override
    public void showSnackbar(String message, String action, INTENT_C.SNACKBAR_ACTIONS intent) {
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(rvJourneySolutions, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case REFRESH:
                snackbar.setAction(action, view -> presenter.searchFromSearch(true)).setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
        }
        snackbar.show();
    }
}
