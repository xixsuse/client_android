package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyResults;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import java.util.LinkedList;
import java.util.List;

import trikita.log.Log;

class JourneyResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private JourneyItemFactory factory;
    private List<SolutionList.Solution> solutionList;
    private JourneyResultsContract.Presenter presenter;

    JourneyResultsAdapter(Context context, JourneyResultsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
        this.solutionList = presenter.getSolutionList();
        factory = JourneyItemFactory.getInstance();
        factory.setContext(context);
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
        // if instance of JourneyHolder, create a list of views that will hold the changes (if there) and toggle the view
        if (holder instanceof JourneyHolder && solutionList.size() > 0) {
            List<View> listView = new LinkedList<>();
            if (solutionList.get(position - 1).hasChanges) {
                for (int i = 0; i < solutionList.get(position - 1).changes.changesList.size(); i++) {
                    listView.add(LayoutInflater.from(context).inflate(R.layout.card_journey, null));
                }
            }
            factory.toggleHolder(listView, (JourneyHolder) holder, solutionList.get(position - 1));
        }

//        if (holder instanceof LoadMoreBeforeHolder) {
//            ((LoadMoreBeforeHolder) holder).btn.setOnClickListener(v -> mOnItemClickListener.onItemClick(solutionList.get(solutionList.size() - 1)));
//        }
//        if (holder instanceof LoadMoreAfterHolder) {
//            ((LoadMoreAfterHolder) holder).btn.setOnClickListener(v -> mOnItemClickListener.onItemClick(solutionList.get(solutionList.size() - 1)));
//        }
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


    @Override
    public int getItemCount() {
        if (solutionList.isEmpty())
            return solutionList.size();
        else
            return solutionList.size() + 2;
    }


    private class VIEW_TYPES {
        static final int Header = 1;
        static final int Normal = 2;
        static final int Footer = 3;
    }


    /**
     * Class that holds the concept of a change (a change can be the main solution,
     * but also the various changes that it could have)
     * This was needed because (almost) the same view is used for the solution and the change view,
     * but che latter had to be nested inside the former, and this way a change view will be held
     * inside of changeHolder, and a solution view will be held inside of a JourneyHolder (which
     * also instantiate a changeHolder, besides other stuff).
     */
    static class ChangeHolder {

        TextView tvTrainCategory;

        LinearLayout llChangesNumber;
        TextView tvChangesNumber;
        TextView tvChangesText;

        LinearLayout llDepartureSchedule;
        TextView tvDepartureTime;
        TextView tvDepartureTimeWithDelay;
        HorizontalScrollView hsvDepartureStationName;
        TextView tvDepartureStationName;

        LinearLayout llArrivalSchedule;
        TextView tvArrivalTime;
        TextView tvArrivalTimeWithDelay;
        HorizontalScrollView hsvArrivalStationName;
        TextView tvArrivalStationName;

        RelativeLayout rlTimeDifference;
        TextView tvTimeDifference;
        TextView tvTimeDifferenceText;

        ImageButton btnRefresh;

        TextView tvLastingTime;

        RelativeLayout rlPlatform;
        TextView tvPlatform;

        ImageButton btnPin;
        ImageButton btnExpandCard;

        LinearLayout llChanges;


        ChangeHolder(View itemView) {


            this.tvTrainCategory = (TextView) itemView.findViewById(R.id.tv_train_category);
            this.tvChangesNumber = (TextView) itemView.findViewById(R.id.tv_changes_number);
            this.tvChangesText = (TextView) itemView.findViewById(R.id.tv_changes_text);

            this.llChangesNumber = (LinearLayout) itemView.findViewById(R.id.ll_changes_number);

            ///

            this.llDepartureSchedule = (LinearLayout) itemView.findViewById(R.id.ll_departure_schedule);
            this.tvDepartureTime = (TextView) itemView.findViewById(R.id.tv_departure_time);
            this.tvDepartureTimeWithDelay = (TextView) itemView.findViewById(R.id.tv_departure_time_with_delay);
            this.hsvDepartureStationName = (HorizontalScrollView) itemView.findViewById(R.id.hsv_departure_station_name);
            this.tvDepartureStationName = (TextView) itemView.findViewById(R.id.tv_departure_station_name);

            this.llArrivalSchedule = (LinearLayout) itemView.findViewById(R.id.ll_arrival_schedule);
            this.tvArrivalTime = (TextView) itemView.findViewById(R.id.tv_arrival_time);
            this.tvArrivalTimeWithDelay = (TextView) itemView.findViewById(R.id.tv_arrival_time_with_delay);
            this.hsvArrivalStationName = (HorizontalScrollView) itemView.findViewById(R.id.hsv_arrival_station_name);
            this.tvArrivalStationName = (TextView) itemView.findViewById(R.id.tv_arrival_station_name);

            ///

            this.rlTimeDifference = (RelativeLayout) itemView.findViewById(R.id.rl_time_difference);
            this.tvTimeDifference = (TextView) itemView.findViewById(R.id.tv_time_difference);
            this.tvTimeDifferenceText = (TextView) itemView.findViewById(R.id.tv_time_difference_text);

            this.btnRefresh = (ImageButton) itemView.findViewById(R.id.btn_refresh_journey);

            ///

            this.tvLastingTime = (TextView) itemView.findViewById(R.id.tv_lasting_time);

            this.rlPlatform = (RelativeLayout) itemView.findViewById(R.id.rl_platform);
            this.tvPlatform = (TextView) itemView.findViewById(R.id.tv_platform);

            this.btnPin = (ImageButton) itemView.findViewById(R.id.btn_pin);
            this.btnExpandCard = (ImageButton) itemView.findViewById(R.id.btn_expand_card);
        }
    }


    /**
     * Wraps a ChangeHolder and additional views
     * (it's the main item holder of the journey results recyclerView)
     */
    class JourneyHolder extends RecyclerView.ViewHolder {

        ChangeHolder holder;

        JourneyHolder(View itemView) {
            super(itemView);

            holder = new ChangeHolder(itemView);

            holder.llChanges = (LinearLayout) itemView.findViewById(R.id.ll_changes);

            holder.btnRefresh.setOnClickListener(view -> {
                Log.d("Requested refresh for item ", getAdapterPosition() - 1);
                presenter.onJourneyRefreshRequested(getAdapterPosition() - 1);
            });

            holder.btnPin.setOnClickListener(view -> {
                Log.d("Requested notification for item number ", getAdapterPosition() - 1);
                presenter.onNotificationRequested(getAdapterPosition() - 1);
            });

            holder.btnExpandCard.setOnClickListener(view -> {
                if (holder.llChanges.getVisibility() == View.VISIBLE) {
                    holder.llChanges.setVisibility(View.GONE);
                } else {
                    holder.llChanges.setVisibility(View.VISIBLE);
                }
            });
//            holder.llChanges.setVisibility(View.GONE);
//            holder.btnExpandCard.setFocusableInTouchMode(true);
        }
    }


    private class LoadMoreBeforeHolder extends RecyclerView.ViewHolder {
        ImageButton btn;

        LoadMoreBeforeHolder(View itemView) {
            super(itemView);
            btn = (ImageButton) itemView.findViewById(R.id.search_before_btn);
            btn.setOnClickListener(view -> presenter.onLoadMoreItemsBefore());
        }
    }

    private class LoadMoreAfterHolder extends RecyclerView.ViewHolder {
        ImageButton btn;

        LoadMoreAfterHolder(View itemView) {
            super(itemView);
            btn = (ImageButton) itemView.findViewById(R.id.search_after_btn);
            btn.setOnClickListener(view -> presenter.onLoadMoreItemsAfter());
        }
    }
}