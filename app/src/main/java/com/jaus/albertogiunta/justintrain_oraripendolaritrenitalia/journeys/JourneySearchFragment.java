package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SeekBar;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;

import org.joda.time.LocalTime;

import java.util.List;

import trikita.log.Log;

public class JourneySearchFragment extends Fragment implements JourneyContract.Search.View {

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;

    private JourneyContract.Search.Presenter mPresenter;

    private OnFragmentInteractionListener mListener;

    private TextInputLayout mDepartureStationInputLayout;
    private TextInputLayout mArrivalStationInputLayout;
    private InstantAutoCompleteTextView mDepartureStation;
    private InstantAutoCompleteTextView mArrivalStation;
    private Button mSearchButton;
    private SeekBar mDepartureTime;
    // TODO implement invert stations button
    private int departureTime = LocalTime.now().getHourOfDay();


    public JourneySearchFragment() {}

    public static JourneySearchFragment newInstance(String param1, String param2) {
        JourneySearchFragment fragment = new JourneySearchFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new JourneySearchPresenter(this); //TODO inject this from activity

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_journey_search, container, false);

        this.mDepartureStationInputLayout = (TextInputLayout) root.findViewById(R.id.input_layout_departure_station);
        this.mArrivalStationInputLayout = (TextInputLayout) root.findViewById(R.id.input_layout_arrival_station);
        this.mDepartureStation = (InstantAutoCompleteTextView) root.findViewById(R.id.departure_station);
        this.mArrivalStation = (InstantAutoCompleteTextView) root.findViewById(R.id.arrival_station);
        this.mDepartureTime = (SeekBar) root.findViewById(R.id.time_selector);
        this.mSearchButton = (Button) root.findViewById(R.id.search_journey);

        // TEXT INPUT LAYOUT
        mDepartureStationInputLayout.setErrorEnabled(true);
        mArrivalStationInputLayout.setErrorEnabled(true);
        takeOffError(mDepartureStationInputLayout);
        takeOffError(mArrivalStationInputLayout);

        // AUTOCOMPLETE TEXT VIEW
        final AutocompleteAdapter departureAdapter = new AutocompleteAdapter(getActivity(), mDepartureStation);
        final AutocompleteAdapter arrivalAdapter = new AutocompleteAdapter(getActivity(), mArrivalStation);

        setOnTouchListener(this.mDepartureStation);
        setOnTouchListener(this.mArrivalStation);

        // SEEK BAR
        this.mDepartureTime.setProgress(LocalTime.now().getHourOfDay());
        this.mDepartureTime.incrementProgressBy(1);
        this.mDepartureTime.setMax(23);
        this.mDepartureTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                departureTime = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // BUTTON
        // TODO should button be activated only when both autocompletetextview are !empty?
        this.mSearchButton.setOnClickListener(v -> mPresenter.search(mDepartureStation.getText().toString(),
                                                                    mArrivalStation.getText().toString(),
                                                                    departureTime));

        return root;
    }

    private void takeOffError(TextInputLayout textInputLayout) {
        //noinspection ConstantConditions
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setOnTouchListener(InstantAutoCompleteTextView textView) {
        textView.setOnTouchListener((v2, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (textView.getText().toString().equals("")) {
                    if (!textView.isPopupShowing()) {
                        textView.showDropDown();
                    }
                }
            }
            return false;
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(JourneyContract.Search.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showArrivalStationNameError(String error) {
        Log.d(error);
        this.mArrivalStationInputLayout.setError(error);
    }

    @Override
    public void showDepartureStationNameError(String error) {
        Log.d(error);
        this.mDepartureStationInputLayout.setError(error);
    }

    private class AutocompleteAdapter extends ArrayAdapter<String> implements Filterable {

        List<String> stringList;
        public AutocompleteAdapter(Context context, InstantAutoCompleteTextView resource) {
            super(context, android.R.layout.simple_list_item_1, 0);
            resource.setAdapter(this);
        }

        @Override
        public int getCount() {
            return stringList == null ? 0 : stringList.size();
        }

        @Override
        public String getItem(int position) {
            return stringList == null ? null : stringList.get(position);
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                // runs in another thread, hence i cannot perform querying with realm here cause it's not allowed
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    filterResults.count = 1; // instead of 0 in order to avoid the list flickering
                    return filterResults;
                }

                // querying the DB and retrieving the stations names
                @Override
                protected void publishResults(@NonNull CharSequence constraint, FilterResults results) {
                    if (results.count == 1) {
                        stringList = mPresenter.getLastSearchedStations();
                    } else {
                        stringList = mPresenter.searchDbForMatchingStation(String.valueOf(constraint));
                    }
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
