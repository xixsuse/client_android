package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.results;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.SolutionList;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;

import java.util.List;

import trikita.log.Log;

public class JourneySolutionsFragment extends Fragment implements JourneyContract.Results.View {

//    private OnFragmentInteractionListener mListener;

    private JourneyContract.Results.Presenter mPresenter;
    private RecyclerView mJourneySolutions;
    private JourneySolutionsAdapter mJourneySolutionsAdapter;


    public JourneySolutionsFragment() {
        // Required empty public constructor
    }

    public static JourneySolutionsFragment newInstance(List<Station4Database> stationList, int time) {
        JourneySolutionsFragment fragment = new JourneySolutionsFragment();
        Bundle args = new Bundle();
        args.putString("departureStationName", stationList.get(0).getName());
        args.putString("departureStationId", stationList.get(0).getStationShortId());
        args.putString("arrivalStationName", stationList.get(1).getName());
        args.putString("arrivalStationId", stationList.get(1).getStationShortId());
        args.putInt("hourOfDay", time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new JourneySolutionsPresenter(this, getArguments().getString("departureStationId"), getArguments().getString("arrivalStationId"), getArguments().getInt("hourOfDay"));
        Log.d("Instating JourneySolutionsPresenter");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_journey_solutions, container, false);
        mJourneySolutions = (RecyclerView) root.findViewById(R.id.rv_journey_solutions);
        mJourneySolutionsAdapter = new JourneySolutionsAdapter(getActivity(), mPresenter.getSolutionList());
        mJourneySolutions.setAdapter(mJourneySolutionsAdapter);
        mJourneySolutions.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPresenter.searchJourney();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }


    @Override
    public void showProgress() {
        Log.d("Starting ...");
    }

    @Override
    public void hideProgress() {
        Log.d("Finishing ...");
    }

    @Override
    public void setJourneySolutions(List<SolutionList.Solution> solutionList) {
        Log.d("Size of list: ", mJourneySolutionsAdapter.list.size());
        mJourneySolutionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Log.d(message);
    }


//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction();
//    }
}
