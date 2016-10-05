package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import trikita.log.Log;

public class JourneyResultsActivity extends AppCompatActivity implements JourneyResultsContract.View {

    @BindView(R.id.cv_header_1) CardView cvHeader;
    @BindView(R.id.tv_departure_station_name) TextView tvHeaderDepartureStation;
    @BindView(R.id.tv_arrival_station_name) TextView tvHeaderArrivalStation;
    @BindView(R.id.btn_header_swap_station_names) ImageButton btnHeaderSwapStationNames;
    @BindView(R.id.btn_toggle_favourite) ImageButton btnHeaderToggleFavorite;

    @BindView(R.id.loading_spinner) ProgressBar progressBar;

    // NO SOLUTION FOUND
    @BindView(R.id.rl_empty_journey_box) RelativeLayout rlEmptyJourneyBox;
    @BindView(R.id.btn_change_stations) Button btnChangeStations;

    //  RESULTS
    @BindView(R.id.rl_journey_solutions) RelativeLayout rlJourneySolutions;
    @BindView(R.id.rv_journey_solutions) RecyclerView rvJourneySolutions;
    @BindView(R.id.btn_refresh) ImageButton btnRefresh;

    JourneyResultsAdapter mJourneyResultsAdapter;
    JourneyResultsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_results);
        ButterKnife.bind(this);
        presenter = new JourneyResultsPresenter(this);

        mJourneyResultsAdapter = new JourneyResultsAdapter(this, presenter);
        rvJourneySolutions.setAdapter(mJourneyResultsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @OnClick(R.id.btn_toggle_favourite)
    void onToggleFavoriteClicked() {
        presenter.onFavouriteButtonClick();
    }

    @OnClick(R.id.btn_refresh)
    void onRefreshClicked() {
        presenter.searchFromSearch();
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
    public Context getViewContext() {
        return getApplicationContext();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        rlJourneySolutions.setVisibility(View.GONE);
        rlEmptyJourneyBox.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        rlJourneySolutions.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSolutionsList(List<SolutionList.Solution> solutionList) {
        Log.d("Successfully updated list");
        mJourneyResultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSolution(int elementIndex) {
        Log.d("Successfully updated item");
        mJourneyResultsAdapter.notifyItemChanged(elementIndex+1);
    }


    @Override
    public void showSnackbar(String message) {
        Log.d(message);
        Snackbar snackbar = Snackbar
                .make(rlJourneySolutions, message, Snackbar.LENGTH_LONG);
        snackbar.show();
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
