package com.example.mytrip;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.example.mytrip.adapter.MyTripRecyclerViewAdapter;
import com.example.mytrip.custominterface.AlertDialogListener;
import com.example.mytrip.custominterface.OnLongClickItemListener;
import com.example.mytrip.custominterface.OnMyTripInfoItemClickListener;
import com.example.mytrip.custominterface.OnUpdateMyTripInfoListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.tripasync.DeleteMyTripInfo;
import com.example.mytrip.database.async.tripasync.GetAllMyTripInfo;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.helper.RecyclerTouchListener;
import com.example.mytrip.helper.RecyclerTouchListener.OnSwipeOptionsClickListener;
import com.example.mytrip.model.MyTripInfo;
import com.example.mytrip.sync.SyncTripInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MyTripListActivity extends AppCompatActivity implements OnMyTripInfoItemClickListener,
        OnRefreshListener, OnSwipeOptionsClickListener, AlertDialogListener, OnLongClickItemListener {

    public static OnUpdateMyTripInfoListener updateMyTripInfoListener;
    private static final String TOKEN = "token";

    private RecyclerView recyclerView;
    private TextView tvEmptyListTxt;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshContainer; // for app refresh

    private List<MyTripInfo> myTripInfoList;
    private AppDatabase db;
    private MyTripRecyclerViewAdapter myTripRecyclerViewAdapter;
    private RecyclerTouchListener touchListener; // for swiping RecyclerView item
    private int itemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_list);

        Toolbar toolbar = findViewById(R.id.toolbar_my_trip_list);
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

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.addOnItemTouchListener(touchListener); // allows swiping recycler view item
        touchListener.closeVisibleBG(null); // closes if any open item
    }

    /**
     * --------Function to initialize necessary view for this activity ----
     **/
    private void initView() {

        FloatingActionButton fab = findViewById(R.id.fab_add_trip);
        recyclerView = findViewById(R.id.rv_my_trip_list);
        tvEmptyListTxt = findViewById(R.id.tv_empty_text);
        pb = findViewById(R.id.custom_list_progress);
        refreshContainer = findViewById(R.id.swipeContainer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager); // sets team_list_item Manager

        touchListener = new RecyclerTouchListener(this, recyclerView);
        touchListener.setSwipeable(true);
        touchListener.setSwipeOptionViews(R.id.delete_task, R.id.edit_task)
                .setSwipeable(R.id.rowFG, R.id.rowBG, this);

        // sets refresh control
        refreshContainer.setOnRefreshListener(this);
        refreshContainer.setColorSchemeColors(getColor(R.color.colorAccent));

        fab.setOnClickListener(view -> finish());
    }

    /**
     * --------Function to initialize necessary variables for this activity ----
     **/
    private void init() {

        db = AppDatabase.getInstance(this);
        myTripInfoList = new ArrayList<>();
        getMyTripInfoList();
    }

    /**
     * --------Function to get all the trip info from database ----
     **/
    private void getMyTripInfoList() {

        new GetAllMyTripInfo(db, myTripInfos -> {

            if (!myTripInfos.isEmpty()) {

                myTripInfoList = myTripInfos;
                setupAdapter();
                showHideView(false);
                return;
            }
            showHideView(true);
        }).execute();
    }

    /**
     * --------Function to set adapter to populate data in the recycler view ----
     **/
    private void setupAdapter() {

        myTripRecyclerViewAdapter = new MyTripRecyclerViewAdapter(myTripInfoList, this, this);
        recyclerView.setAdapter(myTripRecyclerViewAdapter);
    }

    /**
     * --------Function to show and hide recycler view and empty info text message ----
     **/
    private void showHideView(boolean show) {

        pb.setVisibility(View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyListTxt.setVisibility(View.VISIBLE);
            return;
        }
        tvEmptyListTxt.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * --------Function to call database operation for removing item ----
     **/
    private void deleteMyTripData(MyTripInfo myTripInfo) {

        new DeleteMyTripInfo(db, result -> {

            if (result) {
                Toasty.success(MyTripListActivity.this, getString(R.string.delete_trip_success_msg)).show();
                syncDeleteInfo(myTripInfo);
                myTripRecyclerViewAdapter.removeAt(itemPosition); // removes from recycler view
                if (myTripRecyclerViewAdapter.getItemCount() < 1)
                    showHideView(true);
            }
        }).execute(myTripInfo);
    }

    /**
     * --------Function to finish this activity and goes to previous activity with data ----
     **/
    private void goToUpdateActivity() {

        if (updateMyTripInfoListener != null) {
            if (itemPosition != -1) {
                MyTripInfo myTripInfo = myTripRecyclerViewAdapter.getAt(itemPosition);
                updateMyTripInfoListener.onUpdateMyTripInfo(myTripInfo); // sends data to the calling activity or previous activity
                finish();
            }
        }
    }

    /**
     * --------Function to Sync deleted trip info ----
     **/
    private void syncDeleteInfo(MyTripInfo myTripInfo) {

        SyncTripInfo syncTripInfo = new SyncTripInfo(this);
        syncTripInfo.syncDeleteTripInfo(TOKEN, myTripInfo);
    }


    /**
     * -------------------- OnMyTripInfoItemClickListener implementation method ------------
     **/
    @Override
    public void onMyTripInfoItemClick(MyTripInfo myTripInfo) {

    }

    /**
     * -------------------- SwipeRefreshLayout.OnRefreshListener implementation method ------------
     **/
    @Override
    public void onRefresh() {
        // Your code to refresh the list here
        refreshContainer.setRefreshing(false);
        pb.setVisibility(View.VISIBLE);
        getMyTripInfoList();
    }

    /**
     * --------swipe related implementation-----
     **/
    @Override
    public void onSwipeOptionClicked(int viewID, int position) {
        itemPosition = position;
        switch (viewID) {
            case R.id.delete_task:
                Helper.showConfirmAlertDialog(this, getString(R.string.delete_confirm_msg), getString(R.string.delete)); // ask for delete confirmation
                break;
            case R.id.edit_task:
                goToUpdateActivity();
                break;

            default:
                break;
        }
    }

    /**
     * --------OnLongClickItemListener >> Long Pressed----
     **/
    @Override
    public void onLongClickItem(int position) {

        touchListener.openSwipeOptions(position); // swipe item on long pressed
    }


    /**
     * ------- AlertDialogListener implementation method -------
     **/
    @Override
    public void onConfirmBtnDialogClick() {

        if (itemPosition != -1)
            deleteMyTripData(myTripRecyclerViewAdapter.getAt(itemPosition)); // delete trip info item
    }

    @Override
    public void onCancelBtnDialogClick() {

    }

}
