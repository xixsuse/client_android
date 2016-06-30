package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results.JourneySolutionsFragment;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search.JourneySearchFragment;

import java.util.List;

import trikita.log.Log;

public class JourneyActivity extends AppCompatActivity implements JourneySearchFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.journey_search_placeholder, JourneySearchFragment.newInstance());
        ft.commit();
    }


    @Override
    public void onFragmentInteraction(List<Station4Database> stationList, int hourOfDay) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.journey_solutions_placeholder, JourneySolutionsFragment.newInstance(stationList, hourOfDay));
        ft.commit();
        Log.d("Instantiated JourneySolutionsFragment");
    }
}
