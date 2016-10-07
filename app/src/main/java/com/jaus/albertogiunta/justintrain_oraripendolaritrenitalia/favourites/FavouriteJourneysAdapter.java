package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.favourites;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.PreferredJourney;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.dift.ui.SwipeToAction;

class FavouriteJourneysAdapter extends RecyclerView.Adapter<FavouriteJourneysAdapter.ViewHolder> {

    private List<PreferredJourney> list = new ArrayList<>();
    FavouriteJourneysAdapter(List<PreferredJourney> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_favourite_journey, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends SwipeToAction.ViewHolder {

        @BindView(R.id.tv_favourite_station_left) TextView tvPreferredStation1;
        @BindView(R.id.tv_favourite_station_right) TextView tvPreferredStation2;

        ViewHolder(View itemView) {
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
