package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.trainDetails;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.BaseView;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Train;

import java.util.List;

public interface TrainDetailsContract {

    interface Presenter extends BasePresenter {

        void updateRequested();

        void refreshRequested();

        List<Object> getFlatTrainList();

        Train getTrainForAdapterPosition(int position);

        void onNotificationRequested(int position);
    }

    interface View extends BaseView {

        /**
         * Hides everything and shows a loader
         */
        void showProgress();

        /**
         * Hides the loader and shows everything else
         */
        void hideProgress();


        void updateTrainDetails();

    }

}
