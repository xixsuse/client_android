package com.jaus.albertogiunta.justintrain_oraripendolaritrenitalia.journeys.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

    private JourneyContract.Search.Presenter presenter;

    private OnFragmentInteractionListener listener;

    private CoordinatorLayout clHeader;

    private FloatingActionButton btnSearchJourney;
    private TextInputLayout tilDepartureStationInputLayout;
    private TextInputLayout tilArrivalStationInputLayout;
    private InstantAutoCompleteTextView iactDepartureStation;
    private InstantAutoCompleteTextView iactArrivalStation;
    private Button btnClearDeparture;
    private Button btnClearArrival;
    private ImageButton btnSwapStations;
    private TextView tvMinusHour;
    private TextView tvMinusMinusHour;
    private TextView tvPlusHour;
    private TextView tvPlusPlusHour;
    private TextView tvDepartureTime;

    private CardView cvHeader;
    private TextView tvHeaderDepartureStation;
    private TextView tvHeaderArrivalStation;
    private ImageButton btnHeaderSwapStationNames;
    private ImageButton btnHeaderToggleFavorite;


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
        presenter = new JourneySearchPresenter(this); //TODO inject this from activity
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // VIEW BINDING
        View root = inflater.inflate(R.layout.fragment_journey_search, container, false);

        this.clHeader = (CoordinatorLayout) root.findViewById(R.id.cl_header_0);
        this.btnSearchJourney = (FloatingActionButton) root.findViewById(R.id.fab_search_journeys);

        this.tilDepartureStationInputLayout = (TextInputLayout) root.findViewById(R.id.til_departure_station);
        this.tilArrivalStationInputLayout = (TextInputLayout) root.findViewById(R.id.til_arrival_station);
        this.iactDepartureStation = (InstantAutoCompleteTextView) root.findViewById(R.id.iatv_departure_station);
        this.iactArrivalStation = (InstantAutoCompleteTextView) root.findViewById(R.id.iatv_arrival_station);
        this.btnClearDeparture = (Button) root.findViewById(R.id.btn_clear_departure_station);
        this.btnClearArrival = (Button) root.findViewById(R.id.btn_clear_arrival_station);
        this.btnSwapStations = (ImageButton) root.findViewById(R.id.btn_swap_station_names);
        this.tvMinusHour = (TextView) root.findViewById(R.id.tv_minus_hour);
        this.tvMinusMinusHour = (TextView) root.findViewById(R.id.tv_minus_minus_hour);
        this.tvDepartureTime = (TextView) root.findViewById(R.id.tv_selected_time);
        this.tvPlusHour = (TextView) root.findViewById(R.id.plus_hour);
        this.tvPlusPlusHour = (TextView) root.findViewById(R.id.plus_plus_hour);

        this.cvHeader = (CardView) root.findViewById(R.id.cv_header_1);
        this.tvHeaderDepartureStation = (TextView) root.findViewById(R.id.tv_departure_station_name);
        this.tvHeaderArrivalStation = (TextView) root.findViewById(R.id.tv_arrival_station_name);
        this.btnHeaderSwapStationNames = (ImageButton) root.findViewById(R.id.btn_header_swap_station_names);
        this.btnHeaderToggleFavorite = (ImageButton) root.findViewById(R.id.btn_toggle_favourite);


        // TEXT INPUT LAYOUT
        tilDepartureStationInputLayout.setErrorEnabled(true);
        tilArrivalStationInputLayout.setErrorEnabled(true);
        takeOffError(tilDepartureStationInputLayout);
        takeOffError(tilArrivalStationInputLayout);

        // AUTOCOMPLETE TEXT VIEW
        final AutocompleteAdapter departureAdapter = new AutocompleteAdapter(getActivity(), iactDepartureStation);
        final AutocompleteAdapter arrivalAdapter = new AutocompleteAdapter(getActivity(), iactArrivalStation);

        setOnTouchListener(this.iactDepartureStation);
        setOnTouchListener(this.iactArrivalStation);

        setOnEditorAction(this.iactDepartureStation);
        setOnEditorAction(this.iactArrivalStation);

        this.btnClearDeparture.setOnClickListener(v -> clearFields(iactDepartureStation));
        this.btnClearArrival.setOnClickListener(v -> clearFields(iactArrivalStation));

        this.iactDepartureStation.addTextChangedListener(setTextWatcher(btnClearDeparture));
        this.iactArrivalStation.addTextChangedListener(setTextWatcher(btnClearArrival));

        this.iactDepartureStation.setOnItemClickListener((adapterView, view, i, l) -> onAutocompleteItemClick(iactDepartureStation, iactArrivalStation));
        this.iactArrivalStation.setOnItemClickListener((adapterView, view, i, l) -> onAutocompleteItemClick(iactArrivalStation, iactDepartureStation));


        // SWAP BUTTON
        this.btnSwapStations.setOnClickListener(v -> {
            swapStations(iactDepartureStation, iactArrivalStation);
            swapStations(this.tvHeaderDepartureStation, this.tvHeaderArrivalStation);
            iactDepartureStation.dismissDropDown();
            iactArrivalStation.dismissDropDown();
        });

        // TIME PANEL
        presenter.changeTime(0);
        tvMinusHour.setOnClickListener(v -> presenter.changeTime(-1));
        tvMinusMinusHour.setOnClickListener(v -> presenter.changeTime(-4));
        tvPlusHour.setOnClickListener(v -> presenter.changeTime(1));
        tvPlusPlusHour.setOnClickListener(v -> presenter.changeTime(4));


        // SEARCH BUTTON
        this.btnSearchJourney.setOnClickListener(this::performSearch);


        // HEADER

        // PANEL
        this.cvHeader.setOnClickListener(view -> {
            this.cvHeader.setVisibility(View.GONE);
            this.clHeader.setVisibility(View.VISIBLE);
        });

        // BUTTONS
        this.btnHeaderSwapStationNames.setOnClickListener(view -> {
            swapStations(iactDepartureStation, iactArrivalStation);
            swapStations(this.tvHeaderDepartureStation, this.tvHeaderArrivalStation);
            performSearch(this.btnSearchJourney);
        });

        presenter.toggleFavouriteJourneyButton();
        this.btnHeaderToggleFavorite.setOnClickListener(view -> {
            presenter.toggleFavouriteJourneyOnClick();
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
        this.tilArrivalStationInputLayout.setError(error);
        Log.d(error);
    }

    @Override
    public void showDepartureStationNameError(String error) {
        this.tilDepartureStationInputLayout.setError(error);
        Log.d(error);
    }

    @Override
    public void setTime(String time) {
        tvDepartureTime.setText(time);
    }

    @Override
    public Context getViewContext() {
        return this.getActivity();
    }

    @Override
    public void togglePreferredJourneyButton(boolean isPreferred) {
        Log.d("ispreferred", isPreferred);
        if (isPreferred) {
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_black);
        } else {
            this.btnHeaderToggleFavorite.setImageResource(R.drawable.ic_star_border);
        }
    }


    /**
     * Swap station names between text views
     * @param v1
     * @param v2
     */
    private void swapStations(TextView v1, TextView v2) {
        String temp = v1.getText().toString();
        v1.setText(v2.getText().toString());
        v2.setText(temp);
    }


    /**
     * Dismisses dropdown, switches from big search panel to small card, sets station names,
     * hides keyboard and interacts with activity
     * @param v
     */
    private void performSearch(View v) {
        int departureHourOfDay = Integer.parseInt(this.tvDepartureTime.getText().toString().substring(0, 2));

        iactDepartureStation.dismissDropDown();
        iactArrivalStation.dismissDropDown();

        this.cvHeader.setVisibility(View.VISIBLE);
        this.clHeader.setVisibility(View.GONE);

        this.tvHeaderDepartureStation.setText(iactDepartureStation.getText().toString());
        this.tvHeaderArrivalStation.setText(iactArrivalStation.getText().toString());

        if (presenter.search(iactDepartureStation.getText().toString(), iactArrivalStation.getText().toString())) {
            presenter.toggleFavouriteJourneyButton();
            listener.onFragmentInteraction(presenter.getSearchedStations(), presenter.getHourOfDay(), presenter.userHasModifiedTime());
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

    /**
     * Detect if should substitute ENTER key in soft keyboard with SEARCH key
     * @param editText
     */
    private void setOnEditorAction(EditText editText) {
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(textView);
                handled = true;
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
//                        stationNames = presenter.getRecentStations();
//                    } else {
                        stationNames = presenter.searchDbForMatchingStation(String.valueOf(constraint));
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
        void onFragmentInteraction(List<Station4Database> stationList, int hourOfDay, boolean userHasModifiedTime);
    }
}
