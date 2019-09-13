package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;

import com.example.mytrip.fragment.HomeFragment;
import com.example.mytrip.helper.Helper;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.OnButtonClickListener;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.newInstance;

public class DefaultSearchActivity extends AppCompatActivity {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private ImageButton ibFromDate;
    private ImageButton ibToDate;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private TextView tvFromDate;
    private TextView tvToDate;
    private TextView tvFromAddress;
    private TextView tvToAddress;
    private TextView tvClear;
    private FloatingActionButton fabMyTripList;

    private int dateType = 0;
    private AutocompleteSupportFragment fromAutocompleteFragment;
    private AutocompleteSupportFragment toAutocompleteFragment;

    private final View.OnClickListener mFromDateOnClickListener = (View v) -> {
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        ibFromDate.setEnabled(false);
        dateType = 1;
    };

    private final View.OnClickListener mToDateOnClickListener = (View v) -> {
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        ibToDate.setEnabled(false);
        dateType = 2;
    };

    private final View.OnClickListener mClearOnClickListener = (View v) -> {
        resetData();
    };

    private final View.OnClickListener mGoToMyTripListOnClickListener = (View v) -> {
        Intent i = new Intent(this, MyTripListActivity.class);
        startActivity(i);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_search);

        Toolbar toolbar = findViewById(R.id.toolbar_default_search);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initView();
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        ibFromDate = findViewById(R.id.ib_from);
        tvFromDate = findViewById(R.id.tv_from_date);
        ibToDate = findViewById(R.id.ib_to);
        tvToDate = findViewById(R.id.tv_to_date);
        tvFromAddress = findViewById(R.id.tv_from_address);
        tvToAddress = findViewById(R.id.tv_to_address);
        tvClear = findViewById(R.id.tv_clear_all);
        fabMyTripList = findViewById(R.id.fab_my_trip_list);

        ibFromDate.setOnClickListener(mFromDateOnClickListener);
        ibToDate.setOnClickListener(mToDateOnClickListener);
        tvClear.setOnClickListener(mClearOnClickListener);
        fabMyTripList.setOnClickListener(mGoToMyTripListOnClickListener);
    }

    private void init() {

        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            // Initialize the SDK
            Places.initialize(getApplicationContext(), apiKey);
        }

        setUpFromSearch();
        setUpToSearch();
        initializeDateTimePicker();
    }

    private void setUpFromSearch() {

        // Initialize the AutocompleteSupportFragment.
        fromAutocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_from);

        fromAutocompleteFragment.setHint("Search start location");
        // Specify the types of place data to return.
        fromAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));


        // Set up a PlaceSelectionListener to handle the response.
        fromAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                tvFromAddress.setVisibility(View.VISIBLE);
                tvFromAddress.setText(getString(R.string.selected).concat(place.getAddress()));
                showReset();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
                tvFromAddress.setVisibility(View.GONE);
            }
        });
    }

    private void setUpToSearch() {

        // Initialize the AutocompleteSupportFragment.
        toAutocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_to);

        toAutocompleteFragment.setHint("Search destination location");
        // Specify the types of place data to return.
        toAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        toAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getAddress());
                tvToAddress.setVisibility(View.VISIBLE);
                tvToAddress.setText(getString(R.string.selected).concat(place.getAddress()));
                showReset();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
                tvToAddress.setVisibility(View.GONE);
            }
        });
    }

    private void initializeDateTimePicker() {

        // Construct SwitchDateTimePicker
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if (dateTimeFragment == null) {
            dateTimeFragment = newInstance(getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok), getString(android.R.string.cancel));
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());
        dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.startAtCalendarView();
        // Define new day and month format
        dateTimeFragment.setOnButtonClickListener(new OnButtonClickListener() {

            @Override
            public void onPositiveButtonClick(Date date) {
                ibFromDate.setEnabled(true);
                ibToDate.setEnabled(true);
                String selectedDateTime = Helper.changeDateFormat(date);
                if (selectedDateTime != null)
                    setDateTime(selectedDateTime);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
                ibFromDate.setEnabled(true);
                ibToDate.setEnabled(true);
            }
        });
    }

    private void setDateTime(String dateTime) {

        if (dateType == 1) {
            tvFromDate.setText(dateTime);
        } else if (dateType == 2) {
            tvToDate.setText(dateTime);
        } else {
            // do nothing
        }
        showReset();
    }

    private void resetData() {

        fromAutocompleteFragment.setText("");
        tvFromAddress.setText("");
        tvFromDate.setText("");
        toAutocompleteFragment.setText("");
        tvToAddress.setText("");
        tvToDate.setText("");
        tvFromAddress.setVisibility(View.GONE);
        tvToAddress.setVisibility(View.GONE);
        tvClear.setVisibility(View.GONE);

    }

    private void showReset() {

        if (tvClear.getVisibility() != View.VISIBLE)
            tvClear.setVisibility(View.VISIBLE);
    }

}
