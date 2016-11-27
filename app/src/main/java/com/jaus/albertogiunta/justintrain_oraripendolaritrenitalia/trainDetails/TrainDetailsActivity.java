package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.trainDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.HideShowScrollListener;
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

    @BindView(R.id.rl_error)
    RelativeLayout rlEmptyJourneyBox;
    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.btn_error_button)
    Button btnErrorMessage;

    TrainDetailsAdapter adapter;
    private long refreshBtnLastClickTime = SystemClock.elapsedRealtime();


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
        rvTrainDetails.addOnScrollListener(new HideShowScrollListener() {
            @Override
            public void onHide() {
                btnRefresh.animate().setInterpolator(new LinearInterpolator()).translationY(150).setDuration(200);
            }

            @Override
            public void onShow() {
                btnRefresh.animate().setInterpolator(new LinearInterpolator()).translationY(0).setDuration(200);
            }
        });

        btnRefresh.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - refreshBtnLastClickTime > 1000) {
                presenter.refreshRequested();
            }
            refreshBtnLastClickTime = SystemClock.elapsedRealtime();
        });

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
        rlEmptyJourneyBox.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        rvTrainDetails.setVisibility(View.VISIBLE);
        rlEmptyJourneyBox.setVisibility(View.GONE);
    }

    @Override
    public void updateTrainDetails() {
        rvTrainDetails.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showErrorMessage(String tvMessage, String btnMessage, INTENT_C.ERROR_BTN intent) {
        progressBar.setVisibility(View.GONE);
        rvTrainDetails.setVisibility(View.GONE);
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
}
