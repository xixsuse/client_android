package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

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
    @BindView(R.id.rv_journey_solutions)
    RecyclerView rvJourneySolutions;
    @BindView(R.id.btn_refresh)
    ImageButton btnRefresh;

    JourneyResultsAdapter journeyResultsAdapter;
    JourneyResultsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_results);
        ButterKnife.bind(this);
        presenter = new JourneyResultsPresenter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar();

        btnHeaderSwapStationNames.setOnClickListener(v -> presenter.onSwapButtonClick());
        btnHeaderToggleFavorite.setOnClickListener(v -> presenter.onFavouriteButtonClick());

        journeyResultsAdapter = new JourneyResultsAdapter(this, presenter);
        rvJourneySolutions.setAdapter(journeyResultsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnRefresh.setOnClickListener(v -> presenter.searchFromSearch(true));
        presenter.setState(getIntent().getExtras());
        presenter.searchFromSearch(true);
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
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showSnackbar(String message, INTENT_C.SNACKBAR_ACTIONS intent) {
        journeyResultsAdapter.notifyItemChanged(0);
        journeyResultsAdapter.notifyItemChanged(journeyResultsAdapter.getItemCount() - 1);
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(rvJourneySolutions, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case REFRESH:
                snackbar.setAction("Aggiorna", view -> presenter.searchFromSearch(true)).setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
        }
        snackbar.show();
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
            Intent i;
            switch (intent) {
                case CONN_SETTINGS:
                    Log.d("intent a settings");
                    i = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(i);
                    break;
                case SEND_REPORT:
                    Log.d("intent a report");
                    break;
                case NO_SOLUTIONS:
                    Log.d("intent a ricerca");
                    finish();
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
        rvJourneySolutions.getRecycledViewPool().clear();
        journeyResultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSolution(int elementIndex) {
        Journey.Solution s = presenter.getSolutionList().get(elementIndex + 1);
        PreferredStationsHelper.log(this, "solution_update_requested",
                "trainId: " + s.getTrainId()
                        + " journeyAepartureStation: " + s.getDepartureStationName()
                        + " journeyDepartureStation: " + s.getArrivalStationName());

        journeyResultsAdapter.notifyItemChanged(elementIndex + 1);
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
}
