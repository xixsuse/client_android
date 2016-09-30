package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.List;

import trikita.log.Log;

public class JourneyActivity extends AppCompatActivity implements JourneyContract.View {

    public static final String JOURNEY_PARAM = "journey";
    public static final String SEARCH_PANEL_STATUS_PARAM = "searchPanelStatus";
    public static final int DEPARTURE_WANTED = 1;
    public static final int ARRIVAL_WANTED = 2;


    //  SEARCH
    private JourneyContract.Presenter presenter;

    private CoordinatorLayout clHeader;

    private FloatingActionButton btnSearchJourney;

    private TextView tvDepartureStation;
    private TextView tvArrivalStation;
    private ImageButton btnSwapStations;
    private TextView tvMinusHour;
    private TextView tvMinusMinusHour;
    private TextView tvPlusHour;
    private TextView tvPlusPlusHour;
    private TextView tvDepartureTime;

    private CardView cvHeader;
    private TextView tvHeaderDepartureStation;
    private TextView tvHeaderArrivalStation;
    private ImageButton btnHeaderSwapStationNames;
    private ImageButton btnHeaderToggleFavorite;

    private SEARCH_PANEL_STATUS journeySearchFragmentViewState;

    private ProgressBar progressBar;

    // NO SOLUTION FOUND
    private RelativeLayout rlEmptyJourneyBox;
    private Button btnChangeStations;

    //  RESULTS
    private RelativeLayout rlJourneySolutions;
    private JourneySolutionsAdapter journeySolutionsAdapter;
    private RecyclerView rvJourneySolutions;
    private ImageButton btnRefresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        presenter = new JourneyPresenter(this);

        this.clHeader = (CoordinatorLayout) findViewById(R.id.cl_header_0);
        this.btnSearchJourney = (FloatingActionButton) findViewById(R.id.fab_search_journeys);

        this.tvDepartureStation = (TextView) findViewById(R.id.tv_departure_station);
        this.tvArrivalStation = (TextView) findViewById(R.id.tv_arrival_station);
        this.btnSwapStations = (ImageButton) findViewById(R.id.btn_swap_station_names);
        this.tvMinusHour = (TextView) findViewById(R.id.tv_minus_hour);
        this.tvMinusMinusHour = (TextView) findViewById(R.id.tv_minus_minus_hour);
        this.tvDepartureTime = (TextView) findViewById(R.id.tv_selected_time);
        this.tvPlusHour = (TextView) findViewById(R.id.plus_hour);
        this.tvPlusPlusHour = (TextView) findViewById(R.id.plus_plus_hour);

        this.cvHeader = (CardView) findViewById(R.id.cv_header_1);
        this.tvHeaderDepartureStation = (TextView) findViewById(R.id.tv_departure_station_name);
        this.tvHeaderArrivalStation = (TextView) findViewById(R.id.tv_arrival_station_name);
        this.btnHeaderSwapStationNames = (ImageButton) findViewById(R.id.btn_header_swap_station_names);
        this.btnHeaderToggleFavorite = (ImageButton) findViewById(R.id.btn_toggle_favourite);

        onStationNameTextViewClick(this.tvDepartureStation, DEPARTURE_WANTED);
        onStationNameTextViewClick(this.tvArrivalStation, ARRIVAL_WANTED);

        // SWAP BUTTON
        this.btnSwapStations.setOnClickListener(v -> {
            swapStations(this.tvDepartureStation, this.tvArrivalStation);
        });

        // TIME PANEL
        tvMinusHour.setOnClickListener(v -> presenter.onTimeChanged(-1));
        tvMinusMinusHour.setOnClickListener(v -> presenter.onTimeChanged(-4));
        tvPlusHour.setOnClickListener(v -> presenter.onTimeChanged(1));
        tvPlusPlusHour.setOnClickListener(v -> presenter.onTimeChanged(4));
        presenter.onTimeChanged(0);


        // SEARCH BUTTON
        this.btnSearchJourney.setOnClickListener(this::performSearch);


        // PANEL: INACTIVE //

        // PANEL
        this.cvHeader.setOnClickListener(view -> {
            setJourneySearchFragmentViewStatus(SEARCH_PANEL_STATUS.ACTIVE);
        });

        // BUTTONS
        this.btnHeaderSwapStationNames.setOnClickListener(view -> {
            presenter.onSwapButtonClick();
            performSearch(this.btnSearchJourney);
        });

        this.btnHeaderToggleFavorite.setOnClickListener(view -> {
            presenter.onFavouriteButtonClick();
        });

        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // NO SOLUTIONS FOUND
        rlEmptyJourneyBox = (RelativeLayout) findViewById(R.id.rl_empty_journey_box);
        btnChangeStations = (Button) findViewById(R.id.btn_change_stations);

        btnChangeStations.setOnClickListener(view -> {
            setJourneySearchFragmentViewStatus(SEARCH_PANEL_STATUS.ACTIVE);
        });


        // RESULTS
        rlJourneySolutions = (RelativeLayout) findViewById(R.id.rl_journey_solutions);
        rvJourneySolutions = (RecyclerView) findViewById(R.id.rv_journey_solutions);
        btnRefresh = (ImageButton) findViewById(R.id.btn_refresh);
        journeySolutionsAdapter = new JourneySolutionsAdapter(this, presenter);
        rvJourneySolutions.setAdapter(journeySolutionsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btnRefresh.setOnClickListener(view -> {
            presenter.searchFromSearch();
        });
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
            if (requestCode == DEPARTURE_WANTED) {
                this.tvDepartureStation.setText(stationName);
            } else if (requestCode == ARRIVAL_WANTED) {
                this.tvArrivalStation.setText(stationName);
            }
        }
    }

    @Override
    public Context getViewContext() {
        return getApplicationContext();
    }

    @Override
    public void setJourneySearchFragmentViewStatus(SEARCH_PANEL_STATUS status) {
        journeySearchFragmentViewState = status;
        if (status == SEARCH_PANEL_STATUS.INACTIVE) {
            this.cvHeader.setVisibility(View.VISIBLE);
            this.clHeader.setVisibility(View.GONE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            this.presenter.setFavouriteButtonStatus();
//            this.btnRefresh.setVisibility(View.VISIBLE);
//            InputMethodManager imm = (InputMethodManage).getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(this.btnSearchJourney.getWindowToken(), 0);
        } else if (status == SEARCH_PANEL_STATUS.ACTIVE) {
            this.cvHeader.setVisibility(View.GONE);
            this.clHeader.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//            this.btnRefresh.setVisibility(View.GONE);
        }
    }

    @Override
    public SEARCH_PANEL_STATUS getJourneySearchFragmentViewStatus() {
        return this.journeySearchFragmentViewState;
    }

    @Override
    public void setStationNames(String departureStationName, String arrivalStationName) {
        this.tvHeaderDepartureStation.setText(departureStationName);
        this.tvHeaderArrivalStation.setText(arrivalStationName);
        this.tvDepartureStation.setText(departureStationName);
        this.tvArrivalStation.setText(arrivalStationName);
    }

    @Override
    public void setTime(String time) {
        tvDepartureTime.setText(time);
    }

    @Override
    public void onValidSearchParameters() {
        Log.d("Parameters: VALID");
        presenter.searchFromSearch();
        setJourneySearchFragmentViewStatus(SEARCH_PANEL_STATUS.INACTIVE);
    }

    @Override
    public void onInvalidSearchParameters() {
        Log.d("Parameters: NOT VALID");
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
        journeySolutionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateSolution(int elementIndex) {
        Log.d("Successfully updated item");
        journeySolutionsAdapter.notifyItemChanged(elementIndex+1);
    }

    @Override
    public void showSnackbar(String message) {
        Log.d(message);
        Snackbar snackbar = Snackbar
                .make(rlJourneySolutions, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void onStationNameTextViewClick(TextView tv, int code) {
       tv.setOnClickListener(view -> {
            Intent myIntent = new Intent(JourneyActivity.this, StationSearchActivity.class);
            JourneyActivity.this.startActivityForResult(myIntent, code);
        });
    }

    /**
     * This interacepts a touch outside of a textview and hides the keyboard after it happens.
     *
     * @param event
     * @return
     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    v.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }


    /**
     * Swap station names between text views
     *
     * @param v1
     * @param v2
     */
    private void swapStations(TextView v1, TextView v2) {
        setStationNames(v2.getText().toString(), v1.getText().toString());
    }


    /**
     * Dismisses dropdown, switches from big search panel to small card, sets station names,
     * hides keyboard and interacts with activity
     *
     * @param v
     */
    private void performSearch(View v) {
        presenter.onSearchButtonClick(tvDepartureStation.getText().toString(), tvArrivalStation.getText().toString());
    }

    /**
     * Hide the keyboard when touching out of the bounds of an editText
     *
     * @param editText
     */
//    private void hideSoftKeyboard(EditText editText) {
//        if (JourneyActivity.this.getCurrentFocus() != null && (JourneyActivity.this).getCurrentFocus() instanceof EditText) {
//            InputMethodManager imm = (InputMethodManager) JourneyActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//        }
//    }

    /**
     * Detect if should substitute ENTER key in soft keyboard with SEARCH key
     *
     * @param editText
     */
//    private void setOnEditorAction(EditText editText) {
//        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
//            boolean handled = false;
//            if (i == EditorInfo.IME_ACTION_SEARCH) {
//                performSearch(textView);
//                handled = true;
//            }
//            return handled;
//        });
//    }
}
