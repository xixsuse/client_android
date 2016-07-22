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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.R;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.data.Station4Database;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.JourneyContract;
import com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.utils.InstantAutoCompleteTextView;

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
    private ImageButton mSwapStations;
    private FloatingActionButton mSearchButton;
    private TextView mMinusHour;
    private TextView mMinusMinusHour;
    private TextView mPlusHour;
    private TextView mPlusPlusHour;
    private TextView mDepartureTime;
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
        this.mDepartureStationInputLayout = (TextInputLayout) root.findViewById(R.id.til_departure_station);
        this.mArrivalStationInputLayout = (TextInputLayout) root.findViewById(R.id.til_arrival_station);
        this.mDepartureStation = (InstantAutoCompleteTextView) root.findViewById(R.id.iatv_departure_station);
        this.mArrivalStation = (InstantAutoCompleteTextView) root.findViewById(R.id.iatv_arrival_station);
        this.mClearDeparture = (Button) root.findViewById(R.id.btn_clear_departure_station);
        this.mClearArrival = (Button) root.findViewById(R.id.btn_clear_arrival_station);
        this.mSwapStations = (ImageButton) root.findViewById(R.id.btn_swap_station_names);
        this.mMinusHour = (TextView) root.findViewById(R.id.tv_minus_hour);
        this.mMinusMinusHour = (TextView) root.findViewById(R.id.tv_minus_minus_hour);
        this.mDepartureTime = (TextView) root.findViewById(R.id.tv_selected_time);
        this.mPlusHour = (TextView) root.findViewById(R.id.plus_hour);
        this.mPlusPlusHour = (TextView) root.findViewById(R.id.plus_plus_hour);
        this.mSearchButton = (FloatingActionButton) root.findViewById(R.id.fab_search_journeys);

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

        setOnEditorAction(this.mDepartureStation);
        setOnEditorAction(this.mArrivalStation);

        this.mClearDeparture.setOnClickListener(v -> clearFields(mDepartureStation));
        this.mClearArrival.setOnClickListener(v -> clearFields(mArrivalStation));

        this.mDepartureStation.addTextChangedListener(setTextWatcher(mClearDeparture));
        this.mArrivalStation.addTextChangedListener(setTextWatcher(mClearArrival));

        this.mDepartureStation.setOnItemClickListener((adapterView, view, i, l) -> onAutocompleteItemClick(mDepartureStation, mArrivalStation));
        this.mArrivalStation.setOnItemClickListener((adapterView, view, i, l) -> onAutocompleteItemClick(mArrivalStation, mDepartureStation));



        this.mSwapStations.setOnClickListener(v -> {
            String temp = mDepartureStation.getText().toString();
            mDepartureStation.setText(mArrivalStation.getText().toString());
            mArrivalStation.setText(temp);
            mDepartureStation.dismissDropDown();
            mArrivalStation.dismissDropDown();
        });

        // TIME PANEL
        mPresenter.changeTime(0);
        mMinusHour.setOnClickListener(v -> mPresenter.changeTime(-1));
        mMinusMinusHour.setOnClickListener(v -> mPresenter.changeTime(-4));
        mPlusHour.setOnClickListener(v -> mPresenter.changeTime(1));
        mPlusPlusHour.setOnClickListener(v -> mPresenter.changeTime(4));


        // SEARCH BUTTON
        this.mSearchButton.setOnClickListener(this::performSearch);

        return root;
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
    }

    @Override
    public void onPause() {
        super.onPause();
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

    @Override
    public void setTime(String time) {
        mDepartureTime.setText(time);
    }

    private void performSearch(View v) {
        departureHourOfDay = Integer.parseInt(this.mDepartureTime.getText().toString().substring(0, 2));

        mDepartureStation.dismissDropDown();
        mArrivalStation.dismissDropDown();

        if (mPresenter.search(mDepartureStation.getText().toString(), mArrivalStation.getText().toString(), departureHourOfDay)) {
            mListener.onFragmentInteraction(mPresenter.getSearchedStations(), mPresenter.getHourOfDay());
        } else {
            Log.d("Search not fired due to some errors");
        }
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Hide the keyboard when touching out of the bounds of an editText
     * @param mContext
     * @param editText
     */
    private static void hideSoftKeyboard(Context mContext,EditText editText){
        if(((Activity) mContext).getCurrentFocus()!=null && ((Activity) mContext).getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private void setOnEditorAction(EditText editText) {
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(textView);
                handled = true;
//            } else if (i == EditorInfo.IME_ACTION_NEXT) {
//
//                handled = true;
            }

            return handled;
        });
    }

    /**
     * Text watcher for any autocomplete textview (hide and shows clear button)
     * @param v
     * @return
     */
    private TextWatcher setTextWatcher(Button v) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    v.setVisibility(View.GONE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    /**
     * Clear the passed field and dismissed dropdown
     * @param v
     */
    private void clearFields(InstantAutoCompleteTextView v) {
        v.setText("");
        v.dismissDropDown();
    }

    /**
     * Switches AutocompleteTextView when an item of the autocomplete text view is pressed
     * @param focusedView
     * @param unfocusedView
     */
    private void onAutocompleteItemClick(@NonNull InstantAutoCompleteTextView focusedView, @NonNull InstantAutoCompleteTextView unfocusedView) {
        if (unfocusedView.getText().toString().equals("")) {
            unfocusedView.requestFocus();
            unfocusedView.showDropDown();
        } else {
            hideSoftKeyboard(getActivity(), focusedView);
        }
    }

    /**
     * Toggles the error off whenever the user begins writing again in a mispelled field
     * @param textInputLayout
     */
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

    /**
     * Shows the popup even when nothing is written in it
     * @param textView
     */
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
            super(context, R.layout.item_autucomplete_station, 0);
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
//                    if (results.count == 1) {
//                        stationNames = mPresenter.getLastSearchedStations();
//                    } else {
                        stationNames = mPresenter.searchDbForMatchingStation(String.valueOf(constraint));
//                    }

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
        void onFragmentInteraction(List<Station4Database> stationList, int hourOfDay);
    }
}
