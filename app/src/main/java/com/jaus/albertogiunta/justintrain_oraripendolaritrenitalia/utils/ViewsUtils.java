package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils;

import android.view.View;

import butterknife.ButterKnife;

public class ViewsUtils {

    public static final ButterKnife.Action<View> VISIBLE = (view, index) -> view.setVisibility(View.VISIBLE);

    public static final ButterKnife.Action<View> GONE = (view, index) -> view.setVisibility(View.GONE);

}
