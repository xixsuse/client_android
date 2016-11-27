package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.content.Context;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.INTENT_C;

public interface BaseView {

    /*

    public void onCreate(Bundle savedInstanceState)

    public void onResume()

    public void onDestroy()

    protected void onSaveInstanceState(Bundle outState)

     */

    /**
     * Getter for the view context. (Needed when dealing with shared prefs and such).
     */
    Context getViewContext();

    /**
     * Because everybody might need a snackbar at some point.
     *
     * @param message The message to display
     * @param intent use it to understand what to do. It also should determine the action string.
     */
    void showSnackbar(String message, INTENT_C.SNACKBAR_ACTIONS intent);
}
