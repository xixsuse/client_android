package com.jaus.albertogiunta.justintrain_oraritreni.trainDetails;

import com.jaus.albertogiunta.justintrain_oraritreni.BasePresenter;
import com.jaus.albertogiunta.justintrain_oraritreni.BaseView;
import com.jaus.albertogiunta.justintrain_oraritreni.data.Journey;
import com.jaus.albertogiunta.justintrain_oraritreni.data.Train;
import com.jaus.albertogiunta.justintrain_oraritreni.utils.INTENT_CONST;

import java.util.List;

public interface TrainDetailsContract {

    interface Presenter extends BasePresenter {

        void updateRequested();

        void refreshRequested();

        Journey.Solution getSolution();

        List<Object> getFlatTrainList();

        Train getTrainForAdapterPosition(int position);

        Integer getTrainIndexForAdapterPosition(int position);

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

        /**
         * Hides everything and shows an error message with an action button
         *
         * @param tvMessage message to be shown
         * @param btnMessage text for the button
         * @param intent action to execute on button press
         */
        void showErrorMessage(String tvMessage, String btnMessage, INTENT_CONST.ERROR_BTN intent);

    }

}
