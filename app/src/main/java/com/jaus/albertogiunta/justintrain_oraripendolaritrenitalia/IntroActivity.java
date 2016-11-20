package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("ALLORA RAGA", "Quando avete una tratta favorita dovete swipare a destra e sinistra", R.drawable.tut, ContextCompat.getColor(this, R.color.btn_dark_cyan)));
        addSlide(AppIntroFragment.newInstance("CAPITO!?", "Cioè io veramente non so più come dirvelo...", R.drawable.tut1, ContextCompat.getColor(this, R.color.btn_dark_cyan)));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
