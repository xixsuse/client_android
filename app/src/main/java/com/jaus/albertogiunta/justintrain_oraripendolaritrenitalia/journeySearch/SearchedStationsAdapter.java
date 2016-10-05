package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeySearch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;

import java.util.List;

public class SearchedStationsAdapter extends RecyclerView.Adapter<SearchedStationsAdapter.ViewHolder> {

    protected List<String> stationNames;
    private OnClickListener listener;

    public SearchedStationsAdapter(List<String> stationNames, OnClickListener listener) {
        this.stationNames = stationNames;
        this.listener = listener;
    }

    @Override
    public SearchedStationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_autucomplete_station, parent ,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SearchedStationsAdapter.ViewHolder holder, int position) {
        holder.tvStationName.setText(stationNames.get(position));
        holder.tvStationName.setOnClickListener(view -> {
            listener.onItemSelected(stationNames.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return stationNames.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvStationName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvStationName = (TextView) itemView.findViewById(R.id.tv_station_name);
        }
    }

    interface OnClickListener {
        void onItemSelected(String stationName);
    }
}
