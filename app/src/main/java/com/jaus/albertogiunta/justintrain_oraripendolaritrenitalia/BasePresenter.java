package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.os.Bundle;

/**
 * Created by albertogiunta on 24/05/16.
 */
public interface BasePresenter {

    /**
     * Sets the view.
     * @param baseView
     */
    void subscribe(BaseView baseView);

    /**
     * Should be called in the onDestroy method of the view
     * Sets the view as null
     */
    void unsubscribe();

    /**
     * Should be called in the onResume and onRestoreInstanceState method of the view
     * Reinstatiates stuff
     * @param bundle
     */
    void onResuming(Bundle bundle);

    /**
     * Should be called in the onSaveInstanceState of the view
     * @param bundle
     */
    void onLeaving(Bundle bundle);

}
