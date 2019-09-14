package com.example.mytrip;

import android.content.Intent;
import android.content.SyncInfo;
import android.os.Bundle;

import com.example.mytrip.custominterface.OnUpdateMyTripInfoListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.async.GetAllMyTripInfo;
import com.example.mytrip.database.async.InsertMyTripInfo;
import com.example.mytrip.database.async.UpdateMyTripInfo;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.fragment.HomeFragment;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.model.MyTripInfo;
import com.example.mytrip.sync.SyncTripInfo;
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

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import static com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.newInstance;

public class DefaultSearchActivity extends AppCompatActivity implements OnUpdateMyTripInfoListener {

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
    private Button btnSubmit;

    private AppDatabase db;
    private int dateType = 0;
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
    private AutocompleteSupportFragment fromAutocompleteFragment;
    private AutocompleteSupportFragment toAutocompleteFragment;
    private MyTripInfo myTripInfo;
    private boolean isFromUpdate = false;
    private final View.OnClickListener mClearOnClickListener = (View v) -> {
        resetData();
    };

    private final View.OnClickListener mGoToMyTripListOnClickListener = (View v) -> {
        goToMyTripList();
    };

    private final View.OnClickListener mSubmitOnClickListener = (View v) -> {
        if (!isValidInput())
            return;
        if (!isFromUpdate) {
            insert(db, getMyTripInfoData());
            return;
        }
        update(db, getMyTripInfoData());
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_search);

        Toolbar toolbar = findViewById(R.id.toolbar_default_search);
        setSupportActionBar(toolbar);

        initView();
        init();
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
        btnSubmit = findViewById(R.id.btn_submit);

        ibFromDate.setOnClickListener(mFromDateOnClickListener);
        ibToDate.setOnClickListener(mToDateOnClickListener);
        tvClear.setOnClickListener(mClearOnClickListener);
        fabMyTripList.setOnClickListener(mGoToMyTripListOnClickListener);
        btnSubmit.setOnClickListener(mSubmitOnClickListener);
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
        db = AppDatabase.getInstance(this);
        myTripInfo = new MyTripInfo();
        getALLMyTripInfoAndSync("token");
    }

    private void setUpFromSearch() {

        // Initialize the AutocompleteSupportFragment.
        fromAutocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_from);

        fromAutocompleteFragment.setHint(getString(R.string.from_hint));
        // Specify the types of place data to return.
        fromAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        // Set up a PlaceSelectionListener to handle the response.
        fromAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                setFromData(place);
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

        toAutocompleteFragment.setHint(getString(R.string.to_hint));
        // Specify the types of place data to return.
        toAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        toAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getAddress());

                setToData(place);
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
            int startTimeStamp = Helper.getTimeStampFromDateTime(dateTime);
            myTripInfo.setStartDateTime(startTimeStamp);
        } else if (dateType == 2) {
            tvToDate.setText(dateTime);
            int endTimeStamp = Helper.getTimeStampFromDateTime(dateTime);
            myTripInfo.setEndDateTime(endTimeStamp);
        } else {
            // do nothing
        }
        showReset();
    }

    private void setFromData(Place place) {

        tvFromAddress.setVisibility(View.VISIBLE);
        tvFromAddress.setText(getString(R.string.selected).concat(place.getAddress()));

        myTripInfo.setStartAddressId(place.getId());
        myTripInfo.setStartAddressName(place.getName());
        myTripInfo.setStartAddress(place.getAddress());
    }

    private void setToData(Place place) {

        tvToAddress.setVisibility(View.VISIBLE);
        tvToAddress.setText(getString(R.string.selected).concat(place.getAddress()));

        myTripInfo.setDestinationAddressId(place.getId());
        myTripInfo.setDestinationAddressName(place.getName());
        myTripInfo.setDestinationAddress(place.getAddress());
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
        myTripInfo = null;
        myTripInfo = new MyTripInfo();
        isFromUpdate = false;
    }

    private void showReset() {

        if (tvClear.getVisibility() != View.VISIBLE)
            tvClear.setVisibility(View.VISIBLE);
    }

    private boolean isValidInput() {

        if (TextUtils.isEmpty(myTripInfo.getStartAddress())) {

            Toasty.error(this, getString(R.string.start_add_required)).show();
            return false;
        }
        if (myTripInfo.getStartDateTime() == 0) {

            Toasty.error(this, getString(R.string.start_time_required)).show();
            return false;
        }
        if (TextUtils.isEmpty(myTripInfo.getDestinationAddress())) {

            Toasty.error(this, getString(R.string.dest_add_required)).show();
            return false;
        }
        if (myTripInfo.getEndDateTime() == 0) {

            Toasty.error(this, getString(R.string.end_time_required)).show();
            return false;
        }

        return true;
    }

    private MyTripInfo getMyTripInfoData() {

        return myTripInfo;
    }

    private void setMyTripInfoData(MyTripInfo myTripInfo) {

        if (myTripInfo != null) {

            this.myTripInfo = myTripInfo;
            fromAutocompleteFragment.setText(myTripInfo.getStartAddressName());
            tvFromAddress.setText(myTripInfo.getStartAddress());
            if (myTripInfo.getStartDateTime() != 0) {
                String dateTime = Helper.getDateTimeFromTimeStamp(myTripInfo.getStartDateTime());
                tvFromDate.setText(dateTime);
            }

            toAutocompleteFragment.setText(myTripInfo.getDestinationAddressName());
            tvToAddress.setText(myTripInfo.getDestinationAddress());
            if (myTripInfo.getEndDateTime() != 0) {
                String dateTime = Helper.getDateTimeFromTimeStamp(myTripInfo.getEndDateTime());
                tvToDate.setText(dateTime);
            }
            tvFromAddress.setVisibility(View.VISIBLE);
            tvToAddress.setVisibility(View.VISIBLE);
            showReset();
            isFromUpdate = true;
        }
    }

    private void insert(AppDatabase appDb, MyTripInfo tripInfo) {

        new InsertMyTripInfo(appDb, result -> {

            if (result) {
                Toasty.success(getApplicationContext(), getString(R.string.insert_success)).show();
                goToMyTripList();
                resetData();
                return;
            }
            Toasty.error(getApplicationContext(), getString(R.string.insert_failed)).show();

        }).execute(tripInfo);
    }

    private void update(AppDatabase appDb, MyTripInfo tripInfo) {

        Toasty.success(this, "Called").show();
        new UpdateMyTripInfo(appDb, result -> {
            if (result) {
                Toasty.success(getApplicationContext(), getString(R.string.update_success)).show();
                goToMyTripList();
                resetData();
                return;
            }
            Toasty.error(getApplicationContext(), getString(R.string.update_failed)).show();
        }).execute(tripInfo);
    }

    private void getALLMyTripInfoAndSync(String token) {

        new GetAllMyTripInfo(db, myTripInfos -> {

            if (!myTripInfos.isEmpty()) {

                List<MyTripInfo> myTripInfoList = myTripInfos;
                SyncTripInfo.syncAllTripInfo(token, myTripInfoList);
            }
        }).execute();
    }

    private void goToMyTripList() {

        isFromUpdate = false;
        Intent i = new Intent(this, MyTripListActivity.class);
        MyTripListActivity.updateMyTripInfoListener = this;
        startActivity(i);
    }

    /**
     * --------OnUpdateMyTripInfoListener >> implementation----
     **/
    @Override
    public void onUpdateMyTripInfo(MyTripInfo myTripInfo) {

        setMyTripInfoData(myTripInfo);
    }
}
