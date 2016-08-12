package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * SINGLETON CLASS
 */
public class JourneyItemFactory {

    private static final boolean ON = true;
    private static final boolean OFF = false;

    private static JourneyItemFactory self = null;
    private static Context context;


    private JourneyItemFactory() {
    }

    public static synchronized JourneyItemFactory getInstance(Context c) {
        if (self == null) {
            self = new JourneyItemFactory();
        }
        context = c;
        return self;
    }

    /**
     * This method will take care of toggling every view inside of a journey item, depending on its
     * current status (with/without change/delay)
     * @param listView in case the solution has changes it will hold a list of card_views
     * @param journeyHolder it's the holder of the main solution view
     * @param s it's the main solution data model
     */
    public void toggleHolder(List<View> listView, JourneySolutionsAdapter.JourneyHolder journeyHolder, SolutionList.Solution s) {

        JourneySolutionsAdapter.ChangeHolder jh = journeyHolder.holder;

        jh.llChanges.removeAllViews(); // needed to avoid "view has already parent exception"

        toggleAlways(jh, s.solution);

        if (s.solution.timeDifference != null) {
            withTimeDifference(jh, s.solution);
        } else {
            withoutTimeDifference(jh);
        }

        if (s.hasChanges) {
            withChanges(jh, s);

            // iterate over every change of the solution
            for (int i = 0; i < s.changes.changesList.size(); i++) {

                // get every change view passed along with the listview
                View v = listView.get(i);

                // create a new holder for this view
                JourneySolutionsAdapter.ChangeHolder ch = new JourneySolutionsAdapter.ChangeHolder(v);

                toggleAlways(ch, s.changes.changesList.get(i));
                if (s.changes.changesList.get(i).timeDifference != null) {
                    withTimeDifference(ch, s.changes.changesList.get(i));
                } else {
                    withoutTimeDifference(ch);
                }
                withoutChanges(ch, s.changes.changesList.get(i));
                setChangeInfo(ch, s.changes.changesList.get(i));

                if (v.getParent() != null) {
                    ((ViewGroup) v.getParent()).removeView(v);
                }
                jh.llChanges.addView(v);
            }
        } else {
            withoutChanges(jh, s.solution);
        }
    }


    private void toggleAlways(JourneySolutionsAdapter.ChangeHolder holder, SolutionList.Solution.Change solution) {
        // always
        setText(holder.tvDepartureTime, solution.departureTimeReadable);
        setText(holder.tvArrivalTime, solution.arrivalTimeReadable);
        setText(holder.tvLastingTime, solution.duration);
    }


    private void withTimeDifference(JourneySolutionsAdapter.ChangeHolder holder, SolutionList.Solution.Change solution) {
        // show time difference
        // hide refresh
        setVisibility(holder.rlTimeDifference, ON);
        setVisibility(holder.btnRefresh, OFF);

        // show platform
        setVisibility(holder.rlPlatform, ON);

        // show schedules with delay
        setVisibility(holder.tvDepartureTimeWithDelay, ON);
        setVisibility(holder.tvArrivalTimeWithDelay, ON);

        // show pin
        setVisibility(holder.btnPin, ON);


        setText(holder.tvTimeDifference, solution.timeDifference, "'");
        setProgress(holder.tvTimeDifferenceText, solution.progress);

        setText(holder.tvPlatform, solution.departurePlatform);

        setText(holder.tvDepartureTimeWithDelay, sumTimes(solution.departureTimeReadable, solution.timeDifference));
        setText(holder.tvArrivalTimeWithDelay, sumTimes(solution.arrivalTimeReadable, solution.timeDifference));

        setColors(context, holder, solution.timeDifference);
    }


    private void withoutTimeDifference(JourneySolutionsAdapter.ChangeHolder jh) {
        // show refresh
        // hide time difference
        setVisibility(jh.btnRefresh, ON);
        setVisibility(jh.rlTimeDifference, OFF);

        // hide schedules with delay
        setVisibility(jh.tvDepartureTimeWithDelay, OFF);
        setVisibility(jh.tvArrivalTimeWithDelay, OFF);

        // hide platform
        setVisibility(jh.rlPlatform, OFF);

        // hide pin
        setVisibility(jh.btnPin, OFF);
    }

    private void withChanges(JourneySolutionsAdapter.ChangeHolder jh, SolutionList.Solution s) {
        // show changes nubmer
        // hide train category
        setVisibility(jh.llChangesNumber, ON);
        setVisibility(jh.tvTrainCategory, OFF);

        // hide platform
        setVisibility(jh.rlPlatform, OFF);

        // show expand
        // hide pin
        setVisibility(jh.btnExpandCard, ON);
        setVisibility(jh.btnPin, OFF);

        // show changes
//        setVisibility(jh.llChanges, ON);

        setText(jh.tvChangesNumber, s.changes.changesNumber);
        setText(jh.tvChangesText, checkPlural("cambio", s.changes.changesNumber));

    }

    private void withoutChanges(JourneySolutionsAdapter.ChangeHolder jh, SolutionList.Solution.Change s) {
        // show train category
        // hide changes number
        setVisibility(jh.tvTrainCategory, ON);
        setVisibility(jh.llChangesNumber, OFF);

        // hide expand
        setVisibility(jh.btnExpandCard, OFF);
        setText(jh.tvTrainCategory, s.trainCategory);

        // hide changes
//        setVisibility(jh.llChanges, OFF);
    }

    private void setChangeInfo(JourneySolutionsAdapter.ChangeHolder jh, SolutionList.Solution.Change s) {
        setVisibility(jh.hsvDepartureStationName, ON);
        setVisibility(jh.hsvArrivalStationName, ON);

        setText(jh.tvDepartureStationName, s.departureStationName);
        setText(jh.tvArrivalStationName, s.arrivalStationName);
    }


    ///////


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

    private static void setColors(Context context, JourneySolutionsAdapter.ChangeHolder h, int timeDifference) {
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
}
