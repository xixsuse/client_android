package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeyFavourites;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;
import me.grantland.widget.AutofitTextView;
import trikita.log.Log;

class FavouriteJourneysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PreferredJourney> list = new LinkedList<>();

    FavouriteJourneysAdapter(List<PreferredJourney> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPES.Header:
                View v = layoutInflater.inflate(R.layout.item_header_favourite_journeys, parent, false);
                v.setOnClickListener(view -> Log.d("onCreateViewHolder:", "niente"));
                return new HeaderViewHolder(v);
            default:
                return new JourneyViewHolder(layoutInflater.inflate(R.layout.item_favourite_journey, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof JourneyViewHolder) {
            JourneyViewHolder vh = (JourneyViewHolder) holder;
            vh.bind(list.get(position - 1));
//        } else if (holder instanceof HeaderViewHolder) {
//            HeaderViewHolder vh = (HeaderViewHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPES.Header;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }

        if (list.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return list.size() + 1;
    }

    private class VIEW_TYPES {
        static final int Header = 1;
//        static final int Normal = 2;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lbl_favourite_journey_title)
        TextView lblFavouriteJourneyTitle;
        @BindView(R.id.lbl_favourite_journey_subtitle)
        TextView lblFavouriteJourneySubtitle;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class JourneyViewHolder extends SwipeToAction.ViewHolder {

        @BindView(R.id.tv_favourite_station_left)
        AutofitTextView tvPreferredStation1;
        @BindView(R.id.tv_favourite_station_right)
        AutofitTextView tvPreferredStation2;

        JourneyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(PreferredJourney preferredJourney) {
            tvPreferredStation1.setText(preferredJourney.getStation1().getName());
            tvPreferredStation2.setText(preferredJourney.getStation2().getName());
            data = preferredJourney;
        }
    }
}
