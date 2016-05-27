package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;

public class JourneyActivity extends AppCompatActivity implements JourneySearchFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
    }

    @Override
    public void onFragmentInteraction() {

    }
}
