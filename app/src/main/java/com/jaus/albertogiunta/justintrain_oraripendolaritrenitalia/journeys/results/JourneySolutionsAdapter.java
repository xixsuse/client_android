package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

/**
 * Created by albertogiunta on 17/06/16.
 */
public class JourneySolutionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    OnItemClickListener mOnItemClickListener;
    List<SolutionList.Solution> list;

    public JourneySolutionsAdapter(List<SolutionList.Solution> list) {
        this.list = list;
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
            ((LoadMoreAfterHolder) holder).btn.setOnClickListener(v -> mOnItemClickListener.onItemClick(list.get(list.size() - 1)));
        }
        if (holder instanceof ItemHolder && list.size() > 0) {
            ItemHolder itemHolder = (ItemHolder) holder;
            SolutionList.Solution solution = list.get(position - 1);
            Integer timeDifference = solution.solution.timeDifference;
            String c = timeDifference != null ? String.format("%d'", timeDifference) : "";
            itemHolder.trainCategory.setText(solution.solution.trainCategory);
            itemHolder.departureTime.setText(solution.solution.departureTimeReadable);
            itemHolder.departureTimeTimed.setText(timeDifference == null ? "" : DateTimeFormat.forPattern("HH:mm").print(DateTime.parse(solution.solution.departureTimeReadable, DateTimeFormat.forPattern("HH:mm")).plusMinutes(solution.solution.timeDifference)));
            itemHolder.arrivalTime.setText(solution.solution.arrivalTimeReadable);
            itemHolder.arrivalTimeTimed.setText(timeDifference == null ? "" : DateTimeFormat.forPattern("HH:mm").print(DateTime.parse(solution.solution.arrivalTimeReadable, DateTimeFormat.forPattern("HH:mm")).plusMinutes(solution.solution.timeDifference)));
            itemHolder.lastingTime.setText(solution.solution.duration);
            itemHolder.platform.setText(solution.solution.departurePlatform);
            itemHolder.timeDifference.setText(c);
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
        protected TextView trainCategory;
        protected TextView departureTime;
        protected TextView departureTimeTimed;
        protected TextView arrivalTime;
        protected TextView arrivalTimeTimed;
        protected TextView timeDifference;
        protected TextView lastingTime;
        protected TextView platform;

        public ItemHolder(View itemView) {
            super(itemView);
            this.trainCategory = (TextView) itemView.findViewById(R.id.text_train_category);
            this.departureTime = (TextView) itemView.findViewById(R.id.text_departure_time);
            this.departureTimeTimed = (TextView) itemView.findViewById(R.id.text_departure_time_timed);
            this.arrivalTime = (TextView) itemView.findViewById(R.id.text_arrival_time);
            this.arrivalTimeTimed = (TextView) itemView.findViewById(R.id.text_arrival_time_timed);
            this.timeDifference = (TextView) itemView.findViewById(R.id.text_time_difference);
            this.lastingTime = (TextView) itemView.findViewById(R.id.text_lasting_time);
            this.platform = (TextView) itemView.findViewById(R.id.text_platform_number);
        }
    }


    protected static class LoadMoreBeforeHolder extends RecyclerView.ViewHolder {
        protected Button btn;

        public LoadMoreBeforeHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.search_before_btn);
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
        protected Button btn;
        public LoadMoreAfterHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.search_after_btn);
        }
    }


}
