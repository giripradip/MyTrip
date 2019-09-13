package com.example.mytrip;

import android.graphics.Color;
import android.os.Bundle;

import com.example.mytrip.adapter.MyTripRecyclerViewAdapter;
import com.example.mytrip.custominterface.AlertDialogListener;
import com.example.mytrip.custominterface.OnLongClickItemListener;
import com.example.mytrip.custominterface.OnMyTripInfoItemClickListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.DeleteMyTripInfo;
import com.example.mytrip.database.async.GetAllMyTripInfo;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.helper.RecyclerTouchListener;
import com.example.mytrip.helper.RecyclerTouchListener.OnSwipeOptionsClickListener;
import com.example.mytrip.model.MyTripInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MyTripListActivity extends AppCompatActivity implements OnMyTripInfoItemClickListener,
        OnRefreshListener, OnSwipeOptionsClickListener, AlertDialogListener, OnLongClickItemListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyListTxt;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshContainer;

    private List<MyTripInfo> myTripInfoList;
    private AppDatabase db;
    private MyTripRecyclerViewAdapter myTripRecyclerViewAdapter;
    private RecyclerTouchListener touchListener;
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

        if (touchListener != null) {

            recyclerView.addOnItemTouchListener(touchListener);
            touchListener.closeVisibleBG(null);
        }
    }

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

    private void init() {

        db = AppDatabase.getInstance(this);
        myTripInfoList = new ArrayList<>();
        getMyTripInfoList();
    }

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

    private void setupAdapter() {

        myTripRecyclerViewAdapter = new MyTripRecyclerViewAdapter(myTripInfoList, this, this);
        recyclerView.setAdapter(myTripRecyclerViewAdapter);
    }

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

    private void deleteMyTripData(MyTripInfo myTripInfo) {

        new DeleteMyTripInfo(db, result -> {

            if (result) {
                Toasty.success(MyTripListActivity.this, getString(R.string.delete_trip_success_msg)).show();
                myTripRecyclerViewAdapter.removeAt(itemPosition);
                if (myTripRecyclerViewAdapter.getItemCount() < 1)
                    showHideView(true);
            }
        }).execute(myTripInfo);
    }


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
                Helper.showConfirmAlertDialog(this, getString(R.string.delete_confirm_msg), getString(R.string.delete));
                break;
            case R.id.edit_task:
                //goToCreateOffering();
                break;
        }
    }

    /**
     * --------OnLongClickItemListener >> Long Pressed----
     **/
    @Override
    public void onLongClickItem(int position) {

        touchListener.openSwipeOptions(position);
    }


    /**
     * ------- AlertDialogListener implementation method -------
     **/
    @Override
    public void onConfirmBtnDialogClick() {

        if (itemPosition != -1)
            deleteMyTripData(myTripRecyclerViewAdapter.getAt(itemPosition));
    }

    @Override
    public void onCancelBtnDialogClick() {

    }

}
