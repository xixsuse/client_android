package com.jaus.albertogiunta.justintrain_oraritreni;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics;

import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TUTORIAL_DONE;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TUTORIAL_NEXT;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.ACTION_TUTORIAL_SKIP;
import static com.jaus.albertogiunta.justintrain_oraritreni.utils.Analytics.SCREEN_TUTORIAL;

public class IntroActivity extends AppIntro2 {

    Analytics analytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = Analytics.getInstance(this);
        addSlide(SampleSlide.newInstance(R.layout.activity_tutorial_1));
        addSlide(SampleSlide.newInstance(R.layout.activity_tutorial_2));
        addSlide(SampleSlide.newInstance(R.layout.activity_tutorial_3));
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
