package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.IntroActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch.JourneySearchActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dift.ui.SwipeToAction;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;

public class FavouriteJourneysActivity extends AppCompatActivity implements FavouritesContract.View {

    FavouritesContract.Presenter presenter;

    @BindView(R.id.rv_favourite_journeys)
    RecyclerView rvFavouriteJourneys;
    @BindView(R.id.ll_add_favourite)
    LinearLayout llAddFavourite;
    FavouriteJourneysAdapter adapter;
    @BindView(R.id.rl_dashboard)
    RelativeLayout rlDashboard;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_body)
    TextView tvBody;
    @BindView(R.id.fab_search_journey)
    FloatingActionButton fabSearchJourney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_journeys);
        ButterKnife.bind(this);
        checkIntro();
        presenter = new FavouritesPresenter(this);
        adapter = new FavouriteJourneysAdapter(presenter.getPreferredJourneys());
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));

        new SwipeToAction(rvFavouriteJourneys, new SwipeToAction.SwipeListener<PreferredJourney>() {
            @Override
            public boolean swipeLeft(PreferredJourney preferredJourney) {
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(bundleJourney(preferredJourney.withStationsSwapped()));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public boolean swipeRight(PreferredJourney preferredJourney) {
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(bundleJourney(preferredJourney));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public void onClick(PreferredJourney preferredJourney) {
                Log.d("clicked");
            }

            @Override
            public void onLongClick(PreferredJourney preferredJourney) {
                Log.d("long clicked");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setState(getIntent().getExtras());
    }

    @Override
    public void onDestroy() {
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
                .make(this.rvFavouriteJourneys, message, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.txt_white));
        switch (intent) {
            case NONE:
                break;
        }
        snackbar.show();
    }

    @OnClick({R.id.fab_search_journey, R.id.ll_add_favourite})
    public void search() {
        Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
        FavouriteJourneysActivity.this.startActivity(myIntent);
    }

    @Override
    public void updateFavouritesList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateDashboard(Message message) {
        this.tvTitle.setText(message.getTitle());
        this.tvBody.setText(message.getBody());
    }

    @Override
    public void displayFavouriteJourneys() {
        rvFavouriteJourneys.setVisibility(View.VISIBLE);
        llAddFavourite.setVisibility(View.GONE);
    }

    @Override
    public void displayEntryButton() {
        rvFavouriteJourneys.setVisibility(View.GONE);
        llAddFavourite.setVisibility(View.VISIBLE);
    }

    private void checkIntro() {
        //  Declare a new thread to do a preference check
        Thread t = new Thread(() -> {
            //  Initialize SharedPreferences
            SharedPreferences getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getBaseContext());

            //  Create a new boolean and preference and set it to true
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

            //  If the activity has never started before...
            if (isFirstStart) {

                //  Launch app intro
                Intent i = new Intent(FavouriteJourneysActivity.this, IntroActivity.class);
                startActivity(i);

                //  Make a new preferences editor
                SharedPreferences.Editor e = getPrefs.edit();

                //  Edit preference to make it false because we don't want this to run again
                e.putBoolean("firstStart", false);

                //  Apply changes
                e.apply();
            }
        });

        // Start the thread
        t.start();
    }

    private Bundle bundleJourney(PreferredJourney journey) {
        Bundle bundle = new Bundle();
        bundle.putString(I_STATIONS, new Gson().toJson(journey));
        return bundle;
    }
}
