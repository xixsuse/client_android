package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by albertogiunta on 29/07/16.
 */
public class FavouriteJourneysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PreferredJourney> list = new ArrayList<>();

    public FavouriteJourneysAdapter(List<PreferredJourney> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new PreferredJourneyViewHolder(layoutInflater.inflate(R.layout.item_favourite_journey, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PreferredJourney pj = list.get(position);
        PreferredJourneyViewHolder vh = (PreferredJourneyViewHolder) holder;
        vh.tvPreferredStation1.setText(pj.getStation1().getName());
        vh.tvPreferredStation2.setText(pj.getStation2().getName());
        vh.data = pj;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    protected class PreferredJourneyViewHolder extends SwipeToAction.ViewHolder {

        public TextView tvPreferredStation1;
        public TextView tvPreferredStation2;

        public PreferredJourneyViewHolder(View itemView) {
            super(itemView);
            tvPreferredStation1 = (TextView) itemView.findViewById(R.id.tv_favourite_station_left);
            tvPreferredStation2 = (TextView) itemView.findViewById(R.id.tv_favourite_station_right);
        }


    }

}
