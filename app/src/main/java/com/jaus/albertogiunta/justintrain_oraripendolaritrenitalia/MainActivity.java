package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyActivity;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.PreferredStationsHelper;

import co.dift.ui.SwipeToAction;
import trikita.log.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rvFavouriteJourneys = (RecyclerView) findViewById(R.id.rv_favourite_journeys);
        FavouriteJourneysAdapter adapter = new FavouriteJourneysAdapter(PreferredStationsHelper.getAllAsObject(this));
        rvFavouriteJourneys.setAdapter(adapter);
        rvFavouriteJourneys.setHasFixedSize(true);
        rvFavouriteJourneys.setLayoutManager(new LinearLayoutManager(this));

        SwipeToAction swipeToAction = new SwipeToAction(rvFavouriteJourneys, new SwipeToAction.SwipeListener<PreferredJourney>() {
            @Override
            public boolean swipeLeft(PreferredJourney itemData) {
                PreferredJourney pj = itemData;
                Intent myIntent = new Intent(MainActivity.this, JourneyActivity.class);
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                bundle.putString("departure", gson.toJson(pj.getStation2()));
                bundle.putString("arrival", gson.toJson(pj.getStation1()));
                myIntent.putExtras(bundle);
                Log.d(bundle);
                MainActivity.this.startActivity(myIntent);
                return true;
            }

            @Override
            public boolean swipeRight(PreferredJourney itemData) {
                PreferredJourney pj = itemData;
                Intent myIntent = new Intent(MainActivity.this, JourneyActivity.class);
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                bundle.putString("departure", gson.toJson(pj.getStation1()));
                bundle.putString("arrival", gson.toJson(pj.getStation2()));
                myIntent.putExtras(bundle);
                Log.d(bundle);
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



}
