package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.ViewsUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import trikita.log.Log;

public class JourneyResultsAdapterNew extends RecyclerView.Adapter {

    private Context context;
    private List<SolutionList.Solution> solutionList;
    private JourneyResultsContract.Presenter presenter;

    JourneyResultsAdapterNew(Context context, JourneyResultsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
        this.solutionList = presenter.getSolutionList();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPES.Normal:
                return new JourneyHolder(layoutInflater.inflate(R.layout.item_journey_new, parent, false));
            case VIEW_TYPES.Header:
                return new LoadMoreBeforeHolder(layoutInflater.inflate(R.layout.item_load_before, parent, false));
            case VIEW_TYPES.Footer:
                return new LoadMoreAfterHolder(layoutInflater.inflate(R.layout.item_load_after, parent, false));
            default:
                throw new RuntimeException("there is no type that matches the type " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof JourneyHolder && solutionList.size() > 0) {
            ((JourneyHolder) holder).bind(solutionList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        if (solutionList.isEmpty())
            return solutionList.size();
        else
            return solutionList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPES.Header;
        if (position - 1 == solutionList.size())
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;
    }

    private class VIEW_TYPES {
        static final int Header = 1;
        static final int Normal = 2;
        static final int Footer = 3;
    }


    class JourneyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_train_category)
        TextView tvTrainCategory;
        @BindView(R.id.tv_train_number)
        TextView tvTrainNumber;

        @BindView(R.id.tv_changes_number)
        TextView tvChangesNumber;
//        @BindView(R.id.tv_changes_text)
//        TextView tvChangesText;

        @BindView(R.id.tv_departure_time)
        TextView tvDepartureTime;
        @BindView(R.id.tv_departure_time_with_delay)
        TextView tvDepartureTimeWithDelay;

        @BindView(R.id.tv_arrival_time)
        TextView tvArrivalTime;
        @BindView(R.id.tv_arrival_time_with_delay)
        TextView tvArrivalTimeWithDelay;

        @BindView(R.id.rl_time_difference)
        RelativeLayout rlTimeDifference;
        @BindView(R.id.tv_time_difference)
        TextView tvTimeDifference;
        @BindView(R.id.tv_time_difference_text)
        TextView tvTimeDifferenceText;

        @BindView(R.id.btn_refresh_journey)
        ImageButton btnRefreshJourney;

        ///

        @BindView(R.id.tv_lasting_time)
        TextView tvLastingTime;

        @BindView(R.id.rl_platform)
        RelativeLayout rlPlatform;
        @BindView(R.id.tv_platform)
        TextView tvPlatform;

        @BindView(R.id.tv_change)
        TextView tvChange;

//        @BindView(R.id.ic_bolt)
//        ImageView icBolt;

        @BindView(R.id.ll_notification)
        LinearLayout llNotification;

        @BindViews({R.id.ll_changes_number, R.id.rl_changes})
        List<View> solutionChangesViews;

        @BindViews({R.id.ll_train_identification})
        List<View> solutionDirectViews;

        @BindViews({R.id.rl_time_difference, R.id.tv_arrival_time_with_delay, R.id.tv_departure_time_with_delay})
        List<View> solutionWithDelay;

        @BindViews({R.id.btn_refresh_journey})
        List<View> solutionWithoutDelay;

        @BindViews({R.id.tv_arrival_time_with_delay, R.id.tv_departure_time_with_delay})
        List<View> timesWithDelay;

        JourneyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(SolutionList.Solution solution) {
            SolutionList.Solution.Change s = solution.solution;

            tvDepartureTime.setText(s.departureTimeReadable);
            tvArrivalTime.setText(s.arrivalTimeReadable);
            tvLastingTime.setText(s.duration);
            llNotification.setOnClickListener(v -> presenter.onNotificationRequested(getAdapterPosition() - 1));

            if (solution.solution.timeDifference != null) {
                ButterKnife.apply(solutionWithoutDelay, ViewsUtils.GONE);
                ButterKnife.apply(solutionWithDelay, ViewsUtils.VISIBLE);
                tvTimeDifference.setText(Integer.toString(s.timeDifference) + "'");
                tvTimeDifferenceText.setText(setProgress(s.progress));
                if (s.timeDifference != 0) {
                    tvDepartureTimeWithDelay.setText(sumTimes(s.departureTimeReadable, s.timeDifference));
                    tvArrivalTimeWithDelay.setText(sumTimes(s.arrivalTimeReadable, s.timeDifference));
                } else {
                    ButterKnife.apply(timesWithDelay, ViewsUtils.GONE);
                }
                setColors(context, s.timeDifference);
            } else {
                ButterKnife.apply(solutionWithDelay, ViewsUtils.GONE);
                ButterKnife.apply(solutionWithoutDelay, ViewsUtils.VISIBLE);
                btnRefreshJourney.setOnClickListener(view -> presenter.onJourneyRefreshRequested(getAdapterPosition() - 1));
            }

            if (!solution.hasChanges) {
                ButterKnife.apply(solutionChangesViews, ViewsUtils.GONE);
                ButterKnife.apply(solutionDirectViews, ViewsUtils.VISIBLE);
                tvTrainCategory.setText(s.trainCategory);
                tvTrainNumber.setText(s.trainId);
            } else {
                ButterKnife.apply(solutionDirectViews, ViewsUtils.GONE);
                ButterKnife.apply(solutionChangesViews, ViewsUtils.VISIBLE);
                Log.d("bind:", solution.changes.changesNumber);
                tvChangesNumber.setText(Integer.toString(solution.changes.changesNumber));
                tvChange.setText(setChangesString(solution));
            }

            if (!s.departurePlatform.equals("") && !solution.hasChanges) {
                ButterKnife.apply(rlPlatform, ViewsUtils.VISIBLE);
                tvPlatform.setText(s.departurePlatform);
            } else {
                ButterKnife.apply(rlPlatform, ViewsUtils.GONE);
            }

            //TODO controlla se primo treno, in caso mostra bolt
        }

        private String setChangesString(SolutionList.Solution solution) {
            List<String> changesStrings = new LinkedList<>();
            for (SolutionList.Solution.Change c : solution.changes.changesList) {
                changesStrings.add(c.trainCategory + " " + c.trainId);
            }
            String string = "";
            for (String strings : changesStrings) {
                string += strings + " > ";
            }
            if (string.length() > 3) {
                return string.subSequence(0, string.length() - 3).toString();
            }
            return string;
        }

        private String sumTimes(String initialTime, int timeDifference) {
            return DateTimeFormat.forPattern("HH:mm").print(
                    DateTime.parse(initialTime, DateTimeFormat.forPattern("HH:mm"))
                            .plusMinutes(timeDifference));
        }

        private void setColors(Context context, int timeDifference) {
            int color = ContextCompat.getColor(context, R.color.txt_white);
            if (timeDifference == 0) {
                color = ContextCompat.getColor(context, R.color.ontime);
            } else if (timeDifference > 0) {
                color = ContextCompat.getColor(context, R.color.late1);
            } else if (timeDifference < 0) {
                color = ContextCompat.getColor(context, R.color.early1);
            }

            tvTimeDifference.setTextColor(color);
            tvDepartureTimeWithDelay.setTextColor(color);
            tvArrivalTimeWithDelay.setTextColor(color);
        }

        private String setProgress(Integer progress) {
            switch (progress) {
                case 0:
                    return "Costante";
                case 1:
                    return "Recuperando";
                case 2:
                    return "Rallentando";
                default:
                    return "";
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class LoadMoreBeforeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_before_btn)
        ImageButton btn;

        LoadMoreBeforeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btn.setOnClickListener(view -> presenter.onLoadMoreItemsBefore());
        }
    }

    class LoadMoreAfterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_after_btn)
        ImageButton btn;

        LoadMoreAfterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btn.setOnClickListener(view -> presenter.onLoadMoreItemsAfter());
        }
    }
}
