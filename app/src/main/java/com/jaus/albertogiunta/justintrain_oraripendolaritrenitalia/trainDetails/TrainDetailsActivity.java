package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.trainDetails;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import butterknife.BindView;
import butterknife.ButterKnife;
import trikita.log.Log;

public class TrainDetailsActivity extends AppCompatActivity implements TrainDetailsContract.View {

    TrainDetailsContract.Presenter presenter;
    @BindView(R.id.loading_spinner)
    ProgressBar progressBar;
    @BindView(R.id.rv_train_details)
    RecyclerView rvTrainDetails;
    @BindView(R.id.btn_refresh)
    ImageButton btnRefresh;

    TrainDetailsAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_details);
        ButterKnife.bind(this);
        presenter = new TrainDetailsPresenter(this);
        presenter.setState(getIntent().getExtras());

        adapter = new TrainDetailsAdapter(getApplicationContext(), presenter);
        rvTrainDetails.setAdapter(adapter);
        rvTrainDetails.setHasFixedSize(true);
        rvTrainDetails.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnRefresh.setOnClickListener(v -> presenter.refreshRequested());
        presenter.updateRequested();
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
        Log.w(android.R.id.message);
        Snackbar snackbar = Snackbar
                .make(rvTrainDetails, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
            case REFRESH:
                snackbar.setAction("Aggiorna", view -> presenter.refreshRequested()).setActionTextColor(ContextCompat.getColor(this, R.color.btn_cyan));
                break;
        }
        snackbar.show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        rvTrainDetails.setVisibility(View.GONE);
//        rlEmptyJourneyBox.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        rvTrainDetails.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateTrainDetails() {
        rvTrainDetails.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }
}
