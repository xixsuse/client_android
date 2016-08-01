package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;

import java.util.List;

import trikita.log.Log;

public class JourneySolutionsFragment extends Fragment implements JourneyContract.Results.View {

    private JourneyContract.Results.Presenter mPresenter;
    private JourneySolutionsAdapter journeySolutionsAdapter;
    private RecyclerView rvJourneySolutions;
    private ImageButton btnRefresh;

    public JourneySolutionsFragment() {}

    public static JourneySolutionsFragment newInstance(List<Station4Database> stationList, int time, boolean hasModifiedTime) {
        JourneySolutionsFragment fragment = new JourneySolutionsFragment();
        // TODO usa gson
        Bundle bundle = new Bundle();
        bundle.putString("departureStationName", stationList.get(0).getName());
        bundle.putString("departureStationId", stationList.get(0).getStationShortId());
        bundle.putString("arrivalStationName", stationList.get(1).getName());
        bundle.putString("arrivalStationId", stationList.get(1).getStationShortId());
        bundle.putInt("hourOfDay", time);
        bundle.putBoolean("hasModifiedTime", hasModifiedTime);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new JourneySolutionsPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_journey_solutions, container, false);
        rvJourneySolutions = (RecyclerView) root.findViewById(R.id.rv_journey_solutions);
        journeySolutionsAdapter = new JourneySolutionsAdapter(getActivity(), mPresenter);
        rvJourneySolutions.setAdapter(journeySolutionsAdapter);
        rvJourneySolutions.setHasFixedSize(true);
        rvJourneySolutions.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnRefresh = (ImageButton) root.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(view -> {
            mPresenter.searchJourney(new JourneySolutionsPresenter.SearchInstantlyStrategy(),
                    getArguments().getString("departureStationId"),
                    getArguments().getString("arrivalStationId"),
                    getArguments().getInt("hourOfDay"),
                    true,
                    true);
        });

        if (getArguments().getBoolean("hasModifiedTime")) {
            mPresenter.searchJourney(new JourneySolutionsPresenter.SearchAfterTimeStrategy(),
                    getArguments().getString("departureStationId"),
                    getArguments().getString("arrivalStationId"),
                    getArguments().getInt("hourOfDay"),
                    true,
                    true);
        } else {
            mPresenter.searchJourney(new JourneySolutionsPresenter.SearchInstantlyStrategy(),
                    getArguments().getString("departureStationId"),
                    getArguments().getString("arrivalStationId"),
                    getArguments().getInt("hourOfDay"),
                    true,
                    true);
        }
        return root;
    }

    @Override
    public void showProgress() {
        Log.d("Starting ...");
    }

    @Override
    public void hideProgress() {
        Log.d("Finishing ...");
    }

    public void updateSolutionsList(List<SolutionList.Solution> solutionList) {
        Log.d("Size of solutionList: ", journeySolutionsAdapter.solutionList.size());
        journeySolutionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Log.d(message);
    }
}
