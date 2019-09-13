package com.example.mytrip;

import android.graphics.Color;
import android.os.Bundle;

import com.example.mytrip.adapter.MyTripRecyclerViewAdapter;
import com.example.mytrip.custominterface.OnMyTripInfoItemClickListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.GetAllMyTripInfo;
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

public class MyTripListActivity extends AppCompatActivity implements OnMyTripInfoItemClickListener, OnRefreshListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyListTxt;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshContainer;

    private List<MyTripInfo> myTripInfoList;
    private AppDatabase db;
    private MyTripRecyclerViewAdapter myTripRecyclerViewAdapter;

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

    private void initView() {

        FloatingActionButton fab = findViewById(R.id.fab_add_trip);
        recyclerView = findViewById(R.id.rv_my_trip_list);
        tvEmptyListTxt = findViewById(R.id.tv_empty_text);
        pb = findViewById(R.id.custom_list_progress);
        refreshContainer = findViewById(R.id.swipeContainer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager); // sets team_list_item Manager

        // sets refresh control
        refreshContainer.setOnRefreshListener(this);
        refreshContainer.setColorSchemeColors(getColor(R.color.colorAccent));

        fab.setOnClickListener(view -> finish());
    }

    private void init() {

        db = AppDatabase.getInstance(this);
        myTripInfoList = new ArrayList<MyTripInfo>();
        getMyTripInfoList();
    }

    private void getMyTripInfoList() {

        new GetAllMyTripInfo(db, myTripInfos -> {

            if (!myTripInfos.isEmpty()) {

                myTripInfoList = myTripInfos;
                setupAdapter();
            }
            pb.setVisibility(View.GONE);
        }).execute();
    }

    private void setupAdapter() {

        myTripRecyclerViewAdapter = new MyTripRecyclerViewAdapter(myTripInfoList, MyTripListActivity.this);
        recyclerView.setAdapter(myTripRecyclerViewAdapter);
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
        getMyTripInfoList();
    }

}
