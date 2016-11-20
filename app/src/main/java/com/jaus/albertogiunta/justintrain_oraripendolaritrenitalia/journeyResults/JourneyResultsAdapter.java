package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Journey;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.ViewsUtils;

import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

class JourneyResultsAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Journey.Solution> solutionList;
    private JourneyResultsContract.Presenter presenter;

    JourneyResultsAdapter(Context context, JourneyResultsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
        this.solutionList = presenter.getSolutionList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPES.Normal:
                return new JourneyHolder(layoutInflater.inflate(R.layout.item_journey, parent, false));
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
        if (holder instanceof LoadMoreAfterHolder && solutionList.size() > 0) {
            ((LoadMoreAfterHolder) holder).readyButton();
        }
        if (holder instanceof LoadMoreBeforeHolder && solutionList.size() > 0) {
            ((LoadMoreBeforeHolder) holder).readyButton();
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
        @BindView(R.id.tv_changes_text)
        TextView tvChangesText;

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
//        @BindView(R.id.tv_time_difference_text)
//        TextView tvTimeDifferenceText;

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

        @BindView(R.id.ic_bolt)
        ImageView icBolt;

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

        void bind(Journey.Solution solution) {

            tvDepartureTime.setText(solution.getDepartureTimeReadable());
            tvArrivalTime.setText(solution.getArrivalTimeReadable());
            tvLastingTime.setText(solution.getDuration());
            llNotification.setOnClickListener(v -> presenter.onNotificationRequested(getAdapterPosition() - 1));

            // se non è una soluzione di oggi non è possibile richiedere l'orario (controlla meglio la condizione)

            if (solution.getTimeDifference() != null) {
                ButterKnife.apply(solutionWithoutDelay, ViewsUtils.GONE);
                ButterKnife.apply(solutionWithDelay, ViewsUtils.VISIBLE);
                tvTimeDifference.setText(Integer.toString(solution.getTimeDifference()) + "'");
//                tvTimeDifferenceText.setText(setProgress(solution.getProgress()));
                if (solution.getTimeDifference() != 0) {
                    tvDepartureTimeWithDelay.setText(solution.getDepartureTimeWithDelayReadable());
                    tvArrivalTimeWithDelay.setText(solution.getArrivalTimeWithDelayReadable());
                } else {
                    ButterKnife.apply(timesWithDelay, ViewsUtils.GONE);
                }
                setColors(context, solution.getTimeDifference());
                rlTimeDifference.setOnLongClickListener(view -> {
                    presenter.onJourneyRefreshRequested(getAdapterPosition() - 1);
                    return true;
                });
            } else {
                ButterKnife.apply(solutionWithDelay, ViewsUtils.GONE);
                ButterKnife.apply(solutionWithoutDelay, ViewsUtils.VISIBLE);
                btnRefreshJourney.setOnClickListener(view -> presenter.onJourneyRefreshRequested(getAdapterPosition() - 1));
            }

//            Log.d("\ndep", solution.getDepartureTime().toString(), "\narr", solution.getArrivalTime().toString(), "\nnow", DateTime.now().toString(), "\n\n");
//            Log.d("");
//            Log.d("3", DateTime.now().withHourOfDay(3).withMinuteOfHour(0).toString());
//            Log.d("22", DateTime.now().withHourOfDay(22).withMinuteOfHour(0));
//            Log.d("");
            if ((solution.getDepartureTime().isAfter(DateTime.now().withHourOfDay(23).withMinuteOfHour(59)) ||
                    solution.getArrivalTime().isBefore(DateTime.now().withHourOfDay(0).withMinuteOfHour(0)))) {
                if (solution.getDepartureTime().getHourOfDay() > 3 ||
                        DateTime.now().getHourOfDay() < 22) {
                    ButterKnife.apply(solutionWithDelay, ViewsUtils.GONE);
                    ButterKnife.apply(solutionWithoutDelay, ViewsUtils.GONE);
                }
            }

            if (!solution.isHasChanges()) {
                ButterKnife.apply(solutionChangesViews, ViewsUtils.GONE);
                ButterKnife.apply(solutionDirectViews, ViewsUtils.VISIBLE);
                tvTrainCategory.setText(solution.getTrainCategory());
                tvTrainNumber.setText(solution.getTrainId());
            } else {
                ButterKnife.apply(solutionDirectViews, ViewsUtils.GONE);
                ButterKnife.apply(solutionChangesViews, ViewsUtils.VISIBLE);
                int changesNumber = solution.getChangesList().size() - 1;
                String changesText = changesNumber == 1 ? "Cambio" : "Cambi";
                tvChangesNumber.setText(Integer.toString(changesNumber));
                tvChangesText.setText(changesText);
                tvChange.setText(setChangesString(solution));
            }

            if (solution.getDeparturePlatform() != null && !solution.isHasChanges()) {
                ButterKnife.apply(rlPlatform, ViewsUtils.VISIBLE);
                tvPlatform.setText(solution.getDeparturePlatform());
            } else {
                ButterKnife.apply(rlPlatform, ViewsUtils.GONE);
            }

            if (solution.isArrivesFirst()) {
                ButterKnife.apply(icBolt, ViewsUtils.VISIBLE);
            } else {
                ButterKnife.apply(icBolt, ViewsUtils.GONE);
            }
        }

        private String setChangesString(Journey.Solution solution) {
            List<String> changesStrings = new LinkedList<>();
            //noinspection Convert2streamapi
            for (Journey.Solution.Change c : solution.getChangesList()) {
                changesStrings.add(c.getTrainCategory() + " " + c.getTrainId());
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
    }

    @SuppressWarnings("WeakerAccess")
    class LoadMoreBeforeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_before_btn)
        ImageButton btn;
        @BindView(R.id.rl_search_before_loading_spinner)
        RelativeLayout relativeLayout;

        LoadMoreBeforeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btn.setOnClickListener(view -> {
                presenter.onLoadMoreItemsBefore();
                busyButton();
            });
        }

        void busyButton() {
            ButterKnife.apply(relativeLayout, ViewsUtils.VISIBLE);
            ButterKnife.apply(btn, ViewsUtils.GONE);
        }

        void readyButton() {
            ButterKnife.apply(relativeLayout, ViewsUtils.GONE);
            ButterKnife.apply(btn, ViewsUtils.VISIBLE);
        }
    }

    class LoadMoreAfterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_search_after)
        ImageButton btn;
        @BindView(R.id.rl_search_after_loading_spinner)
        RelativeLayout relativeLayout;

        LoadMoreAfterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btn.setOnClickListener(view -> {
                presenter.onLoadMoreItemsAfter();
                busyButton();
            });
        }

        void busyButton() {
            ButterKnife.apply(relativeLayout, ViewsUtils.VISIBLE);
            ButterKnife.apply(btn, ViewsUtils.GONE);
        }

        void readyButton() {
            ButterKnife.apply(relativeLayout, ViewsUtils.GONE);
            ButterKnife.apply(btn, ViewsUtils.VISIBLE);
        }
    }
}
