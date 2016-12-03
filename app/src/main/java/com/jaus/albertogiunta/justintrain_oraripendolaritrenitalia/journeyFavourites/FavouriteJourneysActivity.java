package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.helpers.SharedPreferencesHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dift.ui.SwipeToAction;
import trikita.log.Log;

import static butterknife.ButterKnife.apply;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_NO_SWIPE_BUT_CLICK;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_NO_SWIPE_BUT_LONG_CLICK;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_SEARCH_JOURNEY_FROM_FAVOURITES;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_SWIPE_LEFT_TO_RIGHT;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_SWIPE_RIGHT_TO_LEFT;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.SCREEN_FAVOURITE_JOURNEYS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_CONST.I_STATIONS;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.components.ViewsUtils.GONE;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.components.ViewsUtils.VISIBLE;

public class FavouriteJourneysActivity extends AppCompatActivity implements FavouritesContract.View {

    FavouritesContract.Presenter presenter;
    Analytics analytics;

    @BindView(R.id.rv_favourite_journeys)
    RecyclerView rvFavouriteJourneys;
    @BindView(R.id.ll_add_favourite)
    LinearLayout llAddFavourite;
    FavouriteJourneysAdapter adapter;
    @BindView(R.id.rl_message)
    RelativeLayout rlDashboard;
    @BindView(R.id.tv_message_title)
    TextView tvTitle;
    @BindView(R.id.tv_message_body)
    TextView tvBody;
    @BindView(R.id.fab_search_journey)
    FloatingActionButton fabSearchJourney;
    @BindViews({R.id.hint_left, R.id.hint_right})
    List<View> hints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_journeys);
        ButterKnife.bind(this);
        analytics = Analytics.getInstance(getViewContext());
        checkIntro();
        presenter = new FavouritesPresenter(this);
        adapter = new FavouriteJourneysAdapter(presenter.getPreferredJourneys());
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));

        new SwipeToAction(rvFavouriteJourneys, new SwipeToAction.SwipeListener<PreferredJourney>() {
            @Override
            public boolean swipeLeft(PreferredJourney preferredJourney) {
                analytics.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SWIPE_RIGHT_TO_LEFT);
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(bundleJourney(preferredJourney.withStationsSwapped()));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }


            @Override
            public boolean swipeRight(PreferredJourney preferredJourney) {
                analytics.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SWIPE_LEFT_TO_RIGHT);
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(bundleJourney(preferredJourney));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public void onClick(PreferredJourney preferredJourney) {
                Log.d("clicked");
                analytics.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_NO_SWIPE_BUT_CLICK);
            }

            @Override
            public void onLongClick(PreferredJourney preferredJourney) {
                Log.d("long clicked");
                analytics.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_NO_SWIPE_BUT_LONG_CLICK);
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
    public void showSnackbar(String message, INTENT_CONST.SNACKBAR_ACTIONS intent) {
        Log.w(message);
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
        analytics.logScreenEvent(SCREEN_FAVOURITE_JOURNEYS, ACTION_SEARCH_JOURNEY_FROM_FAVOURITES);
        Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
        FavouriteJourneysActivity.this.startActivity(myIntent);
    }

    @Override
    public void updateFavouritesList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateDashboard(Message message) {
        apply(this.rlDashboard, VISIBLE);
        this.tvTitle.setText(message.getTitle());
        this.tvBody.setText(message.getBody());
    }

    @Override
    public void displayFavouriteJourneys() {
        apply(hints, VISIBLE);
        apply(rvFavouriteJourneys, VISIBLE);
        apply(llAddFavourite, GONE);
    }

    @Override
    public void displayEntryButton() {
        apply(hints, GONE);
        apply(rvFavouriteJourneys, GONE);
        apply(llAddFavourite, VISIBLE);
    }

    private void checkIntro() {
        Thread t = new Thread(() -> {
            boolean isFirstStart = SharedPreferencesHelper.getSharedPreferenceBoolean(getViewContext(), "firstStart", true);
            Log.d("checkIntro: ", isFirstStart);
            if (isFirstStart) {
                SharedPreferencesHelper.setSharedPreferenceBoolean(getViewContext(), "firstStart", false);
                Log.d("checkIntro: set to false");
                Intent i = new Intent(FavouriteJourneysActivity.this, IntroActivity.class);
                startActivity(i);
            }
        });
        t.start();
    }

    private Bundle bundleJourney(PreferredJourney journey) {
        Bundle bundle = new Bundle();
        bundle.putString(I_STATIONS, new Gson().toJson(journey));
        return bundle;
    }
}
