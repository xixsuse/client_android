package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by albertogiunta on 13/07/16.
 */
public class JourneyItemFactory {

    private static final boolean ON = true;
    private static final boolean OFF = false;

    public static void toggleHolder(Context context, JourneySolutionsAdapter.ItemHolder h, SolutionList.Solution s) {

        // always
        setText(h.tvDepartureTime, s.solution.departureTimeReadable);
        setText(h.tvArrivalTime, s.solution.arrivalTimeReadable);
        setText(h.tvLastingTime, s.solution.duration);

        if (s.solution.timeDifference != null) {
            // with time difference

            // show time difference
            // hide refresh
            setVisibility(h.rlTimeDifference, ON);
            setVisibility(h.btnRefresh, OFF);

            // show platform
            setVisibility(h.rlPlatform, ON);

            // show schedules with delay
            setVisibility(h.tvDepartureTimeWithDelay, ON);
            setVisibility(h.tvArrivalTimeWithDelay, ON);

            // show pin
            setVisibility(h.btnPin, ON);

            setText(h.tvTimeDifference, s.solution.timeDifference, "'");
            setProgress(h.tvTimeDifferenceText, s.solution.progress);
            setText(h.tvPlatform, s.solution.departurePlatform);

            setText(h.tvDepartureTimeWithDelay, sumTimes(s.solution.departureTimeReadable, s.solution.timeDifference));
            setText(h.tvArrivalTimeWithDelay, sumTimes(s.solution.arrivalTimeReadable, s.solution.timeDifference));

            setColors(context, h, s.solution.timeDifference);

        } else {
            // without time difference

            // show refresh
            // hide time difference
            setVisibility(h.btnRefresh, ON);
            setVisibility(h.rlTimeDifference, OFF);

            // hide schedules with delay
            setVisibility(h.tvDepartureTimeWithDelay, OFF);
            setVisibility(h.tvArrivalTimeWithDelay, OFF);

            // hide platform
            setVisibility(h.rlPlatform, OFF);

            // hide pin
            setVisibility(h.btnPin, OFF);

        }

        if (s.hasChanges) {
            // with changes

            // show changes nubmer
            // hide train category
            setVisibility(h.llChangesNumber, ON);
            setVisibility(h.tvTrainCategory, OFF);

            // hide platform
            setVisibility(h.rlPlatform, OFF);

            // show expand
            // hide pin
            setVisibility(h.btnExpandCard, ON);
            setVisibility(h.btnPin, OFF);

            setText(h.tvChangesNumber, s.changes.changesNumber);
            setText(h.tvChangesText, checkPlural("cambio", s.changes.changesNumber));

        } else {
            // without changes

            // show train category
            // hide changes number
            setVisibility(h.tvTrainCategory, ON);
            setVisibility(h.llChangesNumber, OFF);

            // hide expand
            setVisibility(h.btnExpandCard, OFF);

            setText(h.tvTrainCategory, s.solution.trainCategory);
        }

    }

    private static void setVisibility(View view, boolean setAsVisible) {
        if (setAsVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private static void setText(TextView view, String text) {
        view.setText(text);
    }

    private static void setProgress(TextView view, Integer progress) {
        switch (progress) {
            case 0:
                setText(view, "Costante");
                break;
            case 1:
                setText(view, "Recuperando");
                break;
            case 2:
                setText(view, "Rallentando");
                break;
        }
    }

    private static void setText(TextView view, int value) {
        setText(view, Integer.toString(value));
    }

    private static void setText(TextView view, int value, String appendix) {
        setText(view, Integer.toString(value).concat(appendix));
    }

    private static String checkPlural(String singular, int cardinality) {
        if (singular.equals("cambio")) {
            if (cardinality == 1) {
                return singular;
            } else {
                return "cambi";
            }
        }
        return "";
    }

    private static String sumTimes(String initialTime, int timeDifference) {
        return DateTimeFormat.forPattern("HH:mm").print(
                DateTime.parse(initialTime, DateTimeFormat.forPattern("HH:mm"))
                        .plusMinutes(timeDifference));
    }

    private static void setColors(Context context, JourneySolutionsAdapter.ItemHolder h, int timeDifference) {
        int color = ContextCompat.getColor(context, R.color.textLight);
        if (timeDifference == 0) {
            color = ContextCompat.getColor(context, R.color.ontime);
        } else if (timeDifference > 0) {
            color = ContextCompat.getColor(context, R.color.late1);
        } else if (timeDifference < 0) {
            color = ContextCompat.getColor(context, R.color.early1);
        }

        h.tvTimeDifference.setTextColor(color);
        h.tvDepartureTimeWithDelay.setTextColor(color);
        h.tvArrivalTimeWithDelay.setTextColor(color);
    }


    private void old(JourneySolutionsAdapter.ItemHolder itemHolder, SolutionList.Solution solution) {
        Integer timeDifference = solution.solution.timeDifference;
        String c = timeDifference != null ? String.format("%  d'", timeDifference) : "";
        itemHolder.tvTrainCategory.setText(solution.solution.trainCategory);
        itemHolder.tvDepartureTime.setText(solution.solution.departureTimeReadable);
        itemHolder.tvDepartureTimeWithDelay.setText(timeDifference == null ? "" : DateTimeFormat.forPattern("HH:mm").print(DateTime.parse(solution.solution.departureTimeReadable, DateTimeFormat.forPattern("HH:mm")).plusMinutes(solution.solution.timeDifference)));
        itemHolder.tvArrivalTime.setText(solution.solution.arrivalTimeReadable);
        itemHolder.tvArrivalTimeWithDelay.setText(timeDifference == null ? "" : DateTimeFormat.forPattern("HH:mm").print(DateTime.parse(solution.solution.arrivalTimeReadable, DateTimeFormat.forPattern("HH:mm")).plusMinutes(solution.solution.timeDifference)));
        itemHolder.tvLastingTime.setText(solution.solution.duration);
        itemHolder.tvPlatform.setText(solution.solution.departurePlatform);
        itemHolder.tvTimeDifference.setText(c);
    }

    private void delayNoChange(JourneySolutionsAdapter.ItemHolder h, SolutionList.Solution s) {

        // change
        h.tvTrainCategory.setVisibility(View.VISIBLE);
        h.llChangesNumber.setVisibility(View.GONE);

        //delay + change
        h.tvDepartureTimeWithDelay.setVisibility(View.VISIBLE);
        h.tvDepartureStationName.setVisibility(View.GONE);

        //delay + change
        h.tvArrivalTimeWithDelay.setVisibility(View.VISIBLE);
        h.tvArrivalStationName.setVisibility(View.GONE);

        //delay
        h.rlTimeDifference.setVisibility(View.VISIBLE);
        h.btnRefresh.setVisibility(View.GONE);

        //delay
        h.rlPlatform.setVisibility(View.VISIBLE);

        //change
        h.btnPin.setVisibility(View.VISIBLE);
        h.btnExpandCard.setVisibility(View.GONE);

        h.tvTrainCategory.setText(s.solution.trainCategory);
        h.tvDepartureTime.setText(s.solution.departureTimeReadable);
        h.tvDepartureTimeWithDelay.setText(s.solution.departureTimeReadable);
        h.tvArrivalTime.setText(s.solution.arrivalTimeReadable);
        h.tvArrivalTimeWithDelay.setText(s.solution.arrivalTimeReadable);
        h.tvTimeDifference.setText(s.solution.timeDifference + "");
        h.tvLastingTime.setText(s.solution.duration);
        h.tvPlatform.setText(s.solution.departurePlatform);


    }

    private void noDelaynoChange(JourneySolutionsAdapter.ItemHolder h, SolutionList.Solution s) {

        // change
        h.tvTrainCategory.setVisibility(View.VISIBLE);
        h.llChangesNumber.setVisibility(View.GONE);

        //delay + change
        h.tvDepartureTimeWithDelay.setVisibility(View.GONE);
        h.tvDepartureStationName.setVisibility(View.GONE);

        //delay + change
        h.tvArrivalTimeWithDelay.setVisibility(View.GONE);
        h.tvArrivalStationName.setVisibility(View.GONE);

        //delay
        h.rlTimeDifference.setVisibility(View.GONE);
        h.btnRefresh.setVisibility(View.VISIBLE);

        //delay
        h.rlPlatform.setVisibility(View.GONE);

        //change
        h.btnPin.setVisibility(View.GONE);
        h.btnExpandCard.setVisibility(View.GONE);

        h.tvTrainCategory.setText(s.solution.trainCategory);
        h.tvDepartureTime.setText(s.solution.departureTimeReadable);
        h.tvArrivalTime.setText(s.solution.arrivalTimeReadable);
        h.tvLastingTime.setText(s.solution.duration);
    }

    private void change(JourneySolutionsAdapter.ItemHolder h, SolutionList.Solution s) {
        h.llChangesNumber.setVisibility(View.VISIBLE);
        h.tvTrainCategory.setVisibility(View.GONE);

        h.btnExpandCard.setVisibility(View.VISIBLE);
        h.btnPin.setVisibility(View.GONE);

        h.tvChangesNumber.setText(s.changes.changesNumber + "");

    }

}
