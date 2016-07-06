package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.InstantAutoCompleteTextView;

import org.joda.time.LocalDateTime;

import java.util.List;

import trikita.log.Log;

public class JourneySearchFragment extends Fragment implements JourneyContract.Search.View {

    private JourneyContract.Search.Presenter mPresenter;

    private OnFragmentInteractionListener mListener;

    private TextInputLayout mDepartureStationInputLayout;
    private TextInputLayout mArrivalStationInputLayout;
    private InstantAutoCompleteTextView mDepartureStation;
    private InstantAutoCompleteTextView mArrivalStation;
    private Button mClearDeparture;
    private Button mClearArrival;
    private Button mSwapStations;
    private FloatingActionButton mSearchButton;
    private TextView mMinusHour;
    private TextView mMinusMinusHour;
    private TextView mPlusHour;
    private TextView mPlusPlusHour;
    private TextView mDepartureTime;
    // TODO implement invert stations button
    private int departureHourOfDay;



    public JourneySearchFragment() {}

    public static JourneySearchFragment newInstance() {
        JourneySearchFragment fragment = new JourneySearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing presenter
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mPresenter = new JourneySearchPresenter(this); //TODO inject this from activity
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // VIEW BINDING
        View root = inflater.inflate(R.layout.fragment_journey_search, container, false);

        this.mDepartureStationInputLayout = (TextInputLayout) root.findViewById(R.id.input_layout_departure_station);
        this.mArrivalStationInputLayout = (TextInputLayout) root.findViewById(R.id.input_layout_arrival_station);
        this.mDepartureStation = (InstantAutoCompleteTextView) root.findViewById(R.id.departure_station);
        this.mArrivalStation = (InstantAutoCompleteTextView) root.findViewById(R.id.arrival_station);
        this.mClearDeparture = (Button) root.findViewById(R.id.calc_clear_departure);
        this.mClearArrival = (Button) root.findViewById(R.id.calc_clear_arrival);
        this.mSwapStations = (Button) root.findViewById(R.id.swap_stations);
        this.mMinusHour = (TextView) root.findViewById(R.id.arrow_left);
        this.mMinusMinusHour = (TextView) root.findViewById(R.id.arrow_left_left);
        this.mDepartureTime = (TextView) root.findViewById(R.id.time);
        this.mPlusHour = (TextView) root.findViewById(R.id.arrow_right);
        this.mPlusPlusHour = (TextView) root.findViewById(R.id.arrow_right_right);
        this.mSearchButton = (FloatingActionButton) root.findViewById(R.id.search_journey);


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

        this.mClearDeparture.setOnClickListener(v -> {
            this.mDepartureStation.setText("");
            mDepartureStation.dismissDropDown();
        });
        this.mClearArrival.setOnClickListener(v -> {
            this.mArrivalStation.setText("");
            mArrivalStation.dismissDropDown();
        });

        this.mDepartureStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mClearDeparture.setVisibility(View.GONE);
                } else {
                    mClearDeparture.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.mArrivalStation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    mClearArrival.setVisibility(View.GONE);
                } else {
                    mClearArrival.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDepartureStation.setOnItemClickListener((adapterView, view, i, l) -> {
            if (mArrivalStation.getText().toString().equals("")) {
                mArrivalStation.requestFocus();
            } else {
                if (view != null) {
                    hideSoftKeyboard(getActivity(), mDepartureStation);
                }
            }
        });

        mArrivalStation.setOnItemClickListener((adapterView, view, i, l) -> {
            if (mDepartureStation.getText().toString().equals("")) {
                mDepartureStation.requestFocus();
            } else {
                if (view != null) {
                    hideSoftKeyboard(getActivity(), mArrivalStation);
                }
            }
        });

//        mDepartureStation.onKeyPreIme(KeyEvent.KEYCODE_BACK);


        this.mSwapStations.setOnClickListener(v -> {
            String temp = mDepartureStation.getText().toString();
            mDepartureStation.setText(mArrivalStation.getText().toString());
            mArrivalStation.setText(temp);
            mDepartureStation.dismissDropDown();
            mArrivalStation.dismissDropDown();
        });

        int time = LocalDateTime.now().getHourOfDay();
        String times = time < 10 ? "0" + time : Integer.toString(time);
        mDepartureTime.setText(times + ":00");

        // TIME
        mMinusHour.setOnClickListener(v -> {
            mDepartureTime.setText(changeTime(-1));
        });

        mMinusMinusHour.setOnClickListener(v -> {
            mDepartureTime.setText(changeTime(-4));
        });

        mPlusHour.setOnClickListener(v -> {
            mDepartureTime.setText(changeTime(1));
        });

        mPlusPlusHour.setOnClickListener(v -> {
            mDepartureTime.setText(changeTime(4));
        });


        // BUTTON
        // TODO should button be activated only when both autocompletetextview are !empty?
        this.mSearchButton.setOnClickListener(v -> {
            departureHourOfDay = Integer.parseInt(this.mDepartureTime.getText().toString().substring(0, 2));
            if (mPresenter.search(mDepartureStation.getText().toString(), mArrivalStation.getText().toString(), departureHourOfDay)) {
                mListener.onFragmentInteraction(mPresenter.getSearchedStations(), mPresenter.getHourOfDay());
            } else {
                Log.d("search not fired due to some errors");
            }
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });

//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return root;
    }

    public static void hideSoftKeyboard(Context mContext,EditText username){
        if(((Activity) mContext).getCurrentFocus()!=null && ((Activity) mContext).getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
        }
    }


    //TODO fallo fare al presenter
    private String changeTime(int delta) {
        int time = Integer.parseInt(this.mDepartureTime.getText().toString().substring(0, 2)) + delta;
        if (time < 0) {
            time = 23;
        } else if (time > 24) {
            time = 1;
        }
        String times = time < 10 ? "0" + time : Integer.toString(time);
        return times + ":00";
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
//        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mPresenter.unsubscribe();
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




    private class AutocompleteAdapter extends ArrayAdapter<String> implements Filterable {

        List<String> stationNames;
        public AutocompleteAdapter(Context context, InstantAutoCompleteTextView resource) {
            super(context, R.layout.view_autocomplete_item, 0);
            resource.setAdapter(this);
        }

        @Override
        public int getCount() {
            return stationNames == null ? 0 : stationNames.size();
        }

        @Override
        public String getItem(int position) {
            return stationNames == null ? null : stationNames.get(position);
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
//                    if (results.count == 1) {\
//                        stationNames = mPresenter.getLastSearchedStations();
//                    } else {
                        stationNames = mPresenter.searchDbForMatchingStation(String.valueOf(constraint));
//                    }

                    notifyDataSetChanged();

//                    mArrivalStation.setDropDownHeight((results.count > 3 ? 3 : results.count) * 25 + 20);
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(List<Station4Database> stationList, int hourOfDay);
    }
}
