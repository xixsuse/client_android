package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
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

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Message;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults.JourneyResultsActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch.JourneySearchActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dift.ui.SwipeToAction;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C.I_STATIONS;

public class FavouriteJourneysActivity extends AppCompatActivity implements FavouritesContract.View {

    private FirebaseAnalytics mFirebaseAnalytics;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_journeys);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        presenter = new FavouritesPresenter(this);
        adapter = new FavouriteJourneysAdapter(presenter.getPreferredJourneys());
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));

        new SwipeToAction(rvFavouriteJourneys, new SwipeToAction.SwipeListener<PreferredJourney>() {
            @Override
            public boolean swipeLeft(PreferredJourney itemData) {
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(getBundle(itemData.swapStations()));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public boolean swipeRight(PreferredJourney itemData) {
                Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneyResultsActivity.class);
                myIntent.putExtras(getBundle(itemData));
                FavouriteJourneysActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public void onClick(PreferredJourney itemData) {
                Log.d("clicked");
            }

            @Override
            public void onLongClick(PreferredJourney itemData) {
                Log.d("long clicked");
            }
        });
    }

    @OnClick({R.id.fab_search_journey, R.id.ll_add_favourite})
    public void search() {
        FirebaseCrash.log("Clicked on FAB");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "01");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "favourite");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "fab");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Intent myIntent = new Intent(FavouriteJourneysActivity.this, JourneySearchActivity.class);
        FavouriteJourneysActivity.this.startActivity(myIntent);
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
    protected void onPause() {
        super.onPause();
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
    public void updateDashboard(Message message) {
        this.tvTitle.setText(message.getTitle());
        this.tvBody.setText(message.getBody());
    }

    @Override
    public void displayFavouriteJourneys() {
        Log.d("displayFavouriteJourneys:");
        rvFavouriteJourneys.setVisibility(View.VISIBLE);
        llAddFavourite.setVisibility(View.GONE);
    }

    @Override
    public void displayEntryButton() {
        Log.d("displayEntryButton:");
        rvFavouriteJourneys.setVisibility(View.GONE);
        llAddFavourite.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateFavouritesList(List<PreferredJourney> preferredJourneys) {
        Log.d("Favourite journeys list UPDATED", preferredJourneys.size());
        adapter.notifyDataSetChanged();
    }

    private Bundle getBundle(PreferredJourney journey) {
        Bundle bundle = new Bundle();
        bundle.putString(I_STATIONS, new Gson().toJson(journey));
        Log.d("Sending", bundle);
        return bundle;
    }


    @Override
    public void showSnackbar(String message, String action, INTENT_C.SNACKBAR_ACTIONS intent) {
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
}
