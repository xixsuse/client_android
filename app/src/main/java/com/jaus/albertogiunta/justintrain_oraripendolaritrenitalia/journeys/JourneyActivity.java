package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results.JourneySolutionsFragment;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search.JourneySearchFragment;

import java.util.List;

public class JourneyActivity extends AppCompatActivity implements JourneySearchFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_journey);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        JourneySearchFragment fragment = JourneySearchFragment.newInstance(getIntent().getExtras() != null);
        transaction.replace(R.id.placeholder_journey_search, fragment);
        transaction.commit();
    }


    /**
     * This interacepts a touch outside of a textview and hides the keyboard after it happens.
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }


    /**
     * Instantiate the Solution Fragment
     * @param stationList
     * @param hourOfDay
     * @param userHasModifiedTime
     */
    @Override
    public void onFragmentInteraction(List<Station4Database> stationList, int hourOfDay, boolean userHasModifiedTime) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder_journey_solutions, JourneySolutionsFragment.newInstance(stationList, hourOfDay, userHasModifiedTime));
        ft.commit();
    }
}
