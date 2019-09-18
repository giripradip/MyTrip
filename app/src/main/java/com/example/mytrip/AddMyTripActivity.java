package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.custominterface.OnUpdateMyTripInfoListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.tripasync.GetAllMyTripInfo;
import com.example.mytrip.database.async.tripasync.InsertMyTripInfo;
import com.example.mytrip.database.async.tripasync.UpdateMyTripInfo;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.helper.PrefManager;
import com.example.mytrip.model.MyTripInfo;
import com.example.mytrip.model.Place;
import com.example.mytrip.sync.SyncTripInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.OnButtonClickListener;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import static com.example.mytrip.constant.HelperConstant.GOOGLE_API;
import static com.example.mytrip.constant.HelperConstant.HERE_API;
import static com.example.mytrip.constant.HelperConstant.IS_FROM;
import static com.example.mytrip.constant.HelperConstant.SELECTED_SERVER;
import static com.example.mytrip.constant.HelperConstant.TOM_TOM_API;
import static com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.newInstance;

public class AddMyTripActivity extends AppCompatActivity implements OnUpdateMyTripInfoListener, OnPlaceSelectedListener {

    private static final String TAG = AddMyTripActivity.class.getSimpleName();
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final String TOKEN = "token";

    private NiceSpinner niceSpinner;
    private ImageButton ibFromDate;
    private ImageButton ibToDate;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private EditText etFrom;
    private EditText etTo;
    private TextView tvFromDate;
    private TextView tvToDate;
    private TextView tvFromAddress;
    private TextView tvToAddress;
    private TextView tvClear;
    private FloatingActionButton fabMyTripList;
    private Button btnSubmit;

    private AppDatabase db;
    private int dateType = 0;
    private PrefManager prefManager;
    private SyncTripInfo syncTripInfo;
    private MyTripInfo myTripInfo;
    private boolean isFromUpdate = false;
    private boolean isFrom;
    private String selectedServer;

    private final View.OnClickListener mFromPlaceOnClickListener = (View v) -> {
        isFrom = true;
        goToSearchPlace(isFrom);
    };

    private final View.OnClickListener mFromDateOnClickListener = (View v) -> {
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        ibFromDate.setEnabled(false);
        dateType = 1;
    };

    private final View.OnClickListener mToPlaceOnClickListener = (View v) -> {
        isFrom = false;
        goToSearchPlace(isFrom);
    };

    private final View.OnClickListener mToDateOnClickListener = (View v) -> {
        dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        ibToDate.setEnabled(false);
        dateType = 2;
    };


    private final View.OnClickListener mClearOnClickListener = (View v) -> resetData();

    private final View.OnClickListener mGoToMyTripListOnClickListener = (View v) -> goToMyTripList();

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
        setContentView(R.layout.activity_add_my_trip);

        initView();
        init();
    }

    /**
     * --------Function to initialize necessary view for this activity ----
     **/
    private void initView() {

        niceSpinner = findViewById(R.id.nice_spinner);
        ibFromDate = findViewById(R.id.ib_from);
        tvFromDate = findViewById(R.id.tv_from_date);
        etFrom = findViewById(R.id.et_from);
        etTo = findViewById(R.id.et_to);
        ibToDate = findViewById(R.id.ib_to);
        tvToDate = findViewById(R.id.tv_to_date);
        tvFromAddress = findViewById(R.id.tv_from_address);
        tvToAddress = findViewById(R.id.tv_to_address);
        tvClear = findViewById(R.id.tv_clear_all);
        fabMyTripList = findViewById(R.id.fab_my_trip_list);
        btnSubmit = findViewById(R.id.btn_submit);

        etFrom.setOnClickListener(mFromPlaceOnClickListener);
        ibFromDate.setOnClickListener(mFromDateOnClickListener);
        etTo.setOnClickListener(mToPlaceOnClickListener);
        ibToDate.setOnClickListener(mToDateOnClickListener);
        tvClear.setOnClickListener(mClearOnClickListener);
        fabMyTripList.setOnClickListener(mGoToMyTripListOnClickListener);
        btnSubmit.setOnClickListener(mSubmitOnClickListener);
    }

    /**
     * --------Function to initialize necessary variables for this activity ----
     **/
    private void init() {

        initializeDateTimePicker();

        db = AppDatabase.getInstance(this);
        prefManager = new PrefManager(this);
        myTripInfo = new MyTripInfo();
        syncTripInfo = new SyncTripInfo(this);

        if (prefManager.isSyncRequired())
            getALLMyTripInfoAndSync(TOKEN);

        List<String> source = new LinkedList<>(Arrays.asList(GOOGLE_API, HERE_API, TOM_TOM_API));
        niceSpinner.attachDataSource(source);
        niceSpinner.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {

            String item = (String) parent.getItemAtPosition(position);
            selectedServer = item;
            Log.i(TAG, item);
        });
    }

    /**
     * --------Function to initialize and setup datetime picker ----
     **/
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

    /**
     * --------Function to set selected date time in the TextView ----
     **/
    private void setDateTime(String dateTime) {

        if (dateType == 1) {
            tvFromDate.setText(dateTime);
            int startTimeStamp = Helper.getTimeStampFromDateTime(dateTime);
            myTripInfo.setStartDateTime(startTimeStamp);
        } else if (dateType == 2) {
            tvToDate.setText(dateTime);
            int endTimeStamp = Helper.getTimeStampFromDateTime(dateTime);
            myTripInfo.setEndDateTime(endTimeStamp);
        }
        showReset();
    }

    /**
     * --------Function to set user selected info for start location ----
     **/
    private void setFromData(Place place) {

        etFrom.setText(place.getName());
        tvFromAddress.setVisibility(View.VISIBLE);
        tvFromAddress.setText(getString(R.string.selected).concat(Objects.requireNonNull(place.getFullAddress())));

        // myTripInfo.setStartAddressId(place.getId());
        myTripInfo.setStartAddressName(place.getName());
        myTripInfo.setStartAddress(place.getFullAddress());
    }

    /**
     * --------Function to set user selected info for destination location ----
     **/
    private void setToData(Place place) {

        etTo.setText(place.getName());
        tvToAddress.setVisibility(View.VISIBLE);
        tvToAddress.setText(getString(R.string.selected).concat(Objects.requireNonNull(place.getFullAddress())));

        //myTripInfo.setDestinationAddressId(place.getId());
        myTripInfo.setDestinationAddressName(place.getName());
        myTripInfo.setDestinationAddress(place.getFullAddress());
    }

    /**
     * --------Function to reset all pre-filled data ----
     **/
    private void resetData() {

        etFrom.setText("");
        tvFromAddress.setText("");
        tvFromDate.setText("");
        etTo.setText("");
        tvToAddress.setText("");
        tvToDate.setText("");
        tvFromAddress.setVisibility(View.GONE);
        tvToAddress.setVisibility(View.GONE);
        tvClear.setVisibility(View.GONE);
        myTripInfo = null;
        myTripInfo = new MyTripInfo();
        isFromUpdate = false;
    }

    /**
     * --------Function to sho reset button to clear the data ----
     **/
    private void showReset() {

        if (tvClear.getVisibility() != View.VISIBLE)
            tvClear.setVisibility(View.VISIBLE);
    }

    /**
     * --------Function to validate user input ----
     **/
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

    /**
     * --------Function to set user selected trip info ----
     **/
    private MyTripInfo getMyTripInfoData() {

        return myTripInfo;
    }

    /**
     * --------Function to set user selected info in the form for edit purpose ----
     **/
    private void setMyTripInfoData(MyTripInfo myTripInfo) {

        if (myTripInfo != null) {

            this.myTripInfo = myTripInfo;
            etFrom.setText(myTripInfo.getStartAddressName());
            tvFromAddress.setText(myTripInfo.getStartAddress());
            if (myTripInfo.getStartDateTime() != 0) {
                String dateTime = Helper.getDateTimeFromTimeStamp(myTripInfo.getStartDateTime());
                tvFromDate.setText(dateTime);
            }

            etTo.setText(myTripInfo.getDestinationAddressName());
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

    /**
     * --------Function to call database operation for insertion of TripInfo ----
     **/
    private void insert(AppDatabase appDb, MyTripInfo tripInfo) {

        new InsertMyTripInfo(appDb, result -> {

            if (result) {
                Toasty.success(getApplicationContext(), getString(R.string.insert_success)).show();
                syncTripInfo.syncCreateTripInfo(TOKEN, tripInfo);
                goToMyTripList();
                resetData();
                return;
            }
            Toasty.error(getApplicationContext(), getString(R.string.insert_failed)).show();

        }).execute(tripInfo);
    }

    /**
     * --------Function to call database operation for updating of TripInfo ----
     **/
    private void update(AppDatabase appDb, MyTripInfo tripInfo) {

        Toasty.success(this, "Called").show();
        new UpdateMyTripInfo(appDb, result -> {
            if (result) {
                Toasty.success(getApplicationContext(), getString(R.string.update_success)).show();
                syncTripInfo.syncUpdateTripInfo(TOKEN, tripInfo);
                goToMyTripList();
                resetData();
                return;
            }
            Toasty.error(getApplicationContext(), getString(R.string.update_failed)).show();
        }).execute(tripInfo);
    }

    /**
     * --------Function to call database operation for getting all the TripInfo andy sync if required ----
     **/
    private void getALLMyTripInfoAndSync(String token) {

        new GetAllMyTripInfo(db, myTripInfos -> {

            if (!myTripInfos.isEmpty()) {

                syncTripInfo.syncAllTripInfo(token, myTripInfos);
            }
        }).execute();
    }

    /**
     * --------Function to go to see trip list ----
     **/
    private void goToMyTripList() {

        isFromUpdate = false;
        Intent i = new Intent(this, MyTripListActivity.class);
        MyTripListActivity.updateMyTripInfoListener = this;
        startActivity(i);
    }

    /**
     * --------Function to go to another activity for searching places ----
     **/
    private void goToSearchPlace(boolean isFrom) {

        Intent i = new Intent(this, MainActivity.class);
        MainActivity.mListener = this;
        i.putExtra(SELECTED_SERVER, selectedServer);
        i.putExtra(IS_FROM, isFrom);
        startActivity(i);
    }

    /**
     * --------OnUpdateMyTripInfoListener >> implementation----
     **/
    @Override
    public void onUpdateMyTripInfo(MyTripInfo myTripInfo) {

        setMyTripInfoData(myTripInfo); // populates data in the form
    }

    /**
     * --------OnPlaceSelectedListener >> implementation----
     **/
    @Override
    public void onPlaceSelected(Place place) {

        if (isFrom) {
            setFromData(place);
            return;
        }
        setToData(place);
    }
}
