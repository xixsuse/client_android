package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.HideShowScrollListener;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

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
    private long refreshBtnLastClickTime = SystemClock.elapsedRealtime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_results);
        ButterKnife.bind(this);
        presenter = new JourneyResultsPresenter(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnHeaderSwapStationNames.setOnClickListener(v -> presenter.onSwapButtonClick());
        btnHeaderToggleFavorite.setOnClickListener(v -> presenter.onFavouriteButtonClick());

        journeyResultsAdapter = new JourneyResultsAdapter(this, presenter);
        rvJourneySolutions.setAdapter(journeyResultsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        rvJourneySolutions.addOnScrollListener(new HideShowScrollListener() {
            @Override
            public void onHide() {
                btnRefresh.animate().setInterpolator(new LinearInterpolator()).translationY(150).setDuration(100);
            }

            @Override
            public void onShow() {
                btnRefresh.animate().setInterpolator(new LinearInterpolator()).translationY(0).setDuration(100);
            }
        });

        btnRefresh.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - refreshBtnLastClickTime > 1000) {
                presenter.searchFromSearch(true);
            }
            refreshBtnLastClickTime = SystemClock.elapsedRealtime();
        });
        presenter.setState(getIntent().getExtras());
        presenter.searchFromSearch(true);
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.setState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        presenter.setState(getIntent().getExtras());
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
    public void updateSolutionsList() {
        rvJourneySolutions.getRecycledViewPool().clear();
        journeyResultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSolution(int elementIndex) {
        Journey.Solution s = presenter.getSolutionList().get(elementIndex);
        Log.d("updateSolution: ", s.toString());
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
