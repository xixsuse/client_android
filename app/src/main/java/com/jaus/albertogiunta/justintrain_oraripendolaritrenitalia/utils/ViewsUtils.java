package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.support.annotation.NonNull;
import android.view.View;

import butterknife.ButterKnife;

public class ViewsUtils {

    public static final ButterKnife.Action<View> VISIBLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setVisibility(View.VISIBLE);
        }
    };

    public static final ButterKnife.Action<View> GONE = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            view.setVisibility(View.GONE);
        }
    };

}
