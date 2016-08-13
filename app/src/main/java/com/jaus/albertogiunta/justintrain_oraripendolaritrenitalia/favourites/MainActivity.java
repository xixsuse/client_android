package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.favourites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;

import java.util.List;

import co.dift.ui.SwipeToAction;
import trikita.log.Log;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity.JOURNEY_PARAM;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity.SEARCH_PANEL_STATUS_PARAM;

public class MainActivity extends AppCompatActivity implements FavouritesContract.View {

    FavouritesContract.Presenter presenter;
    RecyclerView rvFavouriteJourneys;
    FavouriteJourneysAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new FavouritesPresenter(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvFavouriteJourneys = (RecyclerView) findViewById(R.id.rv_favourite_journeys);
        adapter = new FavouriteJourneysAdapter(presenter.getPreferredJourneys());
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setHasFixedSize(true);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));

        SwipeToAction swipeToAction = new SwipeToAction(rvFavouriteJourneys, new SwipeToAction.SwipeListener<PreferredJourney>() {
            @Override
            public boolean swipeLeft(PreferredJourney itemData) {
                Intent myIntent = new Intent(MainActivity.this, JourneyActivity.class);
                myIntent.putExtras(getBundle(itemData.swapStations()));
                MainActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public boolean swipeRight(PreferredJourney itemData) {
                Intent myIntent = new Intent(MainActivity.this, JourneyActivity.class);
                myIntent.putExtras(getBundle(itemData));
                MainActivity.this.startActivity(myIntent);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search);
        fab.setOnClickListener(view -> {
            Intent myIntent = new Intent(MainActivity.this, JourneyActivity.class);
            MainActivity.this.startActivity(myIntent);
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
    public Context getViewContext() {
        return this;
    }

    @Override
    public void updateFavouritesList(List<PreferredJourney> preferredJourneys) {
        Log.d("Favourite journeys list UPDATED");
        adapter.notifyDataSetChanged();
    }

    private Bundle getBundle(PreferredJourney journey) {
        Bundle bundle = new Bundle();
        bundle.putString(JOURNEY_PARAM, new Gson().toJson(journey));
        bundle.putString(SEARCH_PANEL_STATUS_PARAM, JourneyContract.View.SEARCH_PANEL_STATUS.INACTIVE.toString());
        Log.d("Sending",bundle);
        return bundle;
    }
}
