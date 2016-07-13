package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import android.content.Context;
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

import java.util.List;

/**
 * Created by albertogiunta on 17/06/16.
 */
public class JourneySolutionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    OnItemClickListener mOnItemClickListener;
    List<SolutionList.Solution> list;
    Context context;

    public JourneySolutionsAdapter(Context context, List<SolutionList.Solution> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPES.Normal:
                return new ItemHolder(layoutInflater.inflate(R.layout.item_journey, parent, false));
            case VIEW_TYPES.Header:
                return new LoadMoreBeforeHolder(layoutInflater.inflate(R.layout.item_load_before, parent, false));
            case VIEW_TYPES.Footer:
                return new LoadMoreAfterHolder(layoutInflater.inflate(R.layout.item_load_after, parent, false));
            default:
                return new ItemHolder(layoutInflater.inflate(R.layout.item_journey, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LoadMoreAfterHolder) {
//            ((LoadMoreAfterHolder) holder).btn.setOnClickListener(v -> mOnItemClickListener.onItemClick(list.get(list.size() - 1)));
        }
        if (holder instanceof ItemHolder && list.size() > 0) {
            ItemHolder itemHolder = (ItemHolder) holder;
            SolutionList.Solution solution = list.get(position - 1);
            JourneyItemFactory.toggleHolder(context, itemHolder, solution);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPES.Header;
        if (position - 1 == list.size())
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;
    }


    @Override
    public int getItemCount() {
        if (list.isEmpty())
            return list.size();
        else
            return list.size() + 2;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(SolutionList.Solution item);
    }

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }

    protected static class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView tvTrainCategory;

        protected LinearLayout llChangesNumber;
        protected TextView tvChangesNumber;
        protected TextView tvChangesText;

        protected LinearLayout llDepartureSchedule;
        protected TextView tvDepartureTime;
        protected TextView tvDepartureTimeWithDelay;
        protected TextView tvDepartureStationName;

        protected LinearLayout llArrivalSchedule;
        protected TextView tvArrivalTime;
        protected TextView tvArrivalTimeWithDelay;
        protected TextView tvArrivalStationName;

        protected RelativeLayout rlTimeDifference;
        protected TextView tvTimeDifference;
        protected TextView tvTimeDifferenceText;

        protected ImageButton btnRefresh;

        protected TextView tvLastingTime;

        protected RelativeLayout rlPlatform;
        protected TextView tvPlatform;

        protected ImageButton btnPin;
        protected ImageButton btnExpandCard;


        public ItemHolder(View itemView) {
            super(itemView);

            ///

            this.tvTrainCategory = (TextView) itemView.findViewById(R.id.tv_train_category);
            this.tvChangesNumber = (TextView) itemView.findViewById(R.id.tv_changes_number);
            this.tvChangesText = (TextView) itemView.findViewById(R.id.tv_changes_text);

            this.llChangesNumber = (LinearLayout) itemView.findViewById(R.id.ll_changes_number);

            ///

            this.llDepartureSchedule = (LinearLayout) itemView.findViewById(R.id.ll_departure_schedule);
            this.tvDepartureTime = (TextView) itemView.findViewById(R.id.tv_departure_time);
            this.tvDepartureTimeWithDelay = (TextView) itemView.findViewById(R.id.tv_departure_time_with_delay);
            this.tvDepartureStationName = (TextView) itemView.findViewById(R.id.tv_departure_station_name);

            this.llArrivalSchedule= (LinearLayout) itemView.findViewById(R.id.ll_arrival_schedule);
            this.tvArrivalTime = (TextView) itemView.findViewById(R.id.tv_arrival_time);
            this.tvArrivalTimeWithDelay = (TextView) itemView.findViewById(R.id.tv_arrival_time_with_delay);
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


    protected static class LoadMoreBeforeHolder extends RecyclerView.ViewHolder {
        protected ImageButton btn;

        public LoadMoreBeforeHolder(View itemView) {
            super(itemView);
            btn = (ImageButton) itemView.findViewById(R.id.search_before_btn);
        }

        public void bind(final SolutionList.Solution item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    protected static class LoadMoreAfterHolder extends RecyclerView.ViewHolder {
        protected ImageButton btn;
        public LoadMoreAfterHolder(View itemView) {
            super(itemView);
            btn = (ImageButton) itemView.findViewById(R.id.search_after_btn);
        }
    }
}