package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics;

import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_TUTORIAL_DONE;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_TUTORIAL_NEXT;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.ACTION_TUTORIAL_SKIP;
import static com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.Analytics.SCREEN_TUTORIAL;

public class IntroActivity extends AppIntro2 {

    Analytics analytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = Analytics.getInstance(this);

        addSlide(AppIntroFragment.newInstance("ALLORA RAGA", "Quando avete una tratta favorita dovete swipare a destra e sinistra", R.drawable.tut, ContextCompat.getColor(this, R.color.btn_dark_cyan)));
        addSlide(AppIntroFragment.newInstance("CAPITO!?", "Cioè io veramente non so più come dirvelo...", R.drawable.tut1, ContextCompat.getColor(this, R.color.btn_dark_cyan)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        analytics.logScreenEvent(SCREEN_TUTORIAL, ACTION_TUTORIAL_DONE);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        analytics.logScreenEvent(SCREEN_TUTORIAL, ACTION_TUTORIAL_SKIP);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        analytics.logScreenEvent(SCREEN_TUTORIAL, ACTION_TUTORIAL_NEXT);
    }

}
