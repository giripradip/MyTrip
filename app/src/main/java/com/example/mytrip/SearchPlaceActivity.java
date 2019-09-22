package com.example.mytrip;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.adapter.LocalPlaceRecyclerViewAdapter;
import com.example.mytrip.adapter.PlaceRecyclerViewAdapter;
import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.placeasync.DeleteSelectedPlace;
import com.example.mytrip.database.async.placeasync.GetAllSelectedPlace;
import com.example.mytrip.database.async.placeasync.InsertSelectedPlace;
import com.example.mytrip.database.async.placeasync.SetFavPlace;
import com.example.mytrip.helper.DeletionSwipeHelper;
import com.example.mytrip.helper.LocationHelper;
import com.example.mytrip.model.Place;
import com.example.mytrip.place.GooglePlaceAPI;
import com.example.mytrip.place.HerePlaceAPI;
import com.example.mytrip.place.OnPlaceListFoundListener;
import com.example.mytrip.place.PlaceAPI;
import com.example.mytrip.place.TomTomPlaceAPI;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.mytrip.constant.HelperConstant.GOOGLE_API;
import static com.example.mytrip.constant.HelperConstant.HERE_API;
import static com.example.mytrip.constant.HelperConstant.IS_FROM;
import static com.example.mytrip.constant.HelperConstant.LOCATION_REQUEST_CODE;
import static com.example.mytrip.constant.HelperConstant.SELECTED_SERVER;
import static com.example.mytrip.constant.HelperConstant.TOM_TOM_API;

public class SearchPlaceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        OnPlaceSelectedListener, OnPlaceListFoundListener, DeletionSwipeHelper.OnSwipeListener {

    private static final String TAG = SearchPlaceActivity.class.getSimpleName();

    public static OnPlaceSelectedListener mListener;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerView localRecyclerView;
    private TextView tvDivider;

    private PlaceAPI placeAPI;
    private PlaceRecyclerViewAdapter placeRecyclerViewAdapter;
    private LocalPlaceRecyclerViewAdapter localPlaceRecyclerViewAdapter;
    private String selectedServer;
    private boolean isFrom;
    private AppDatabase db;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (getIntent().getExtras() != null) {
            selectedServer = getIntent().getStringExtra(SELECTED_SERVER);
            isFrom = getIntent().getBooleanExtra(IS_FROM, true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getString(R.string.from));
        if (!isFrom)
            searchView.setQueryHint(getString(R.string.to));
        searchView.setOnQueryTextListener(this);
        // Get the search close button image view

        return true;
    }

    /**
     * --------Function to initialize necessary views for this activity ----
     **/
    private void initView() {

        recyclerView = findViewById(R.id.rv_place);
        localRecyclerView = findViewById(R.id.rv_local_place);
        tvDivider = findViewById(R.id.tv_divider);
        FloatingActionButton fab = findViewById(R.id.fab_near_me);

        fab.setOnClickListener(view -> getLastLocation());
        ItemTouchHelper.Callback callback = new DeletionSwipeHelper(0, ItemTouchHelper.START, this, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(localRecyclerView);
    }

    /**
     * --------Function to initialize necessary variables for this activity ----
     **/
    private void init() {

        if (TextUtils.isEmpty(selectedServer))
            selectedServer = GOOGLE_API;

        switch (selectedServer) {
            case GOOGLE_API:
                placeAPI = new GooglePlaceAPI(this);
                break;

            case HERE_API:
                placeAPI = new HerePlaceAPI(this);
                break;

            case TOM_TOM_API:
                placeAPI = new TomTomPlaceAPI(this);
                break;

            default:
                break;
        }

        placeRecyclerViewAdapter = new PlaceRecyclerViewAdapter(Collections.emptyList(), this);
        recyclerView.setAdapter(placeRecyclerViewAdapter);

        db = AppDatabase.getInstance(this);
        getAllPlaceFromLocalDb();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * --------Function to check and get device location ----
     **/
    private void getLastLocation() {

        if (!LocationHelper.isLocationReady(this)) { // checks if user has given permission and location service is on
            return;
        }

        getDeviceLocation();
    }

    /**
     * --------Function to check and get device location ----
     **/
    private void getDeviceLocation() {

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Logic to handle location object
                placeAPI.nearBySearch(location.getLatitude(), location.getLongitude()); // search nearby places
                setEmptyLocalAdapter();
                //return;
            }
            //locationUpdate();
        });
        fusedLocationClient.getLastLocation().addOnFailureListener(e -> e.printStackTrace());
    }

    /**
     * --------Function to check if permission is granted ----
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                Toasty.error(this, getString(R.string.permission_denied)).show();
            }
        }
    }

    private void locationUpdate() {

        //placeAPI.nearBySearch(50.3734597, 7.5207957);
    }

    /**
     * --------Function to set adapter for both local and remote data ----
     **/
    private void setAdapter(List<Place> placeList) {

        placeRecyclerViewAdapter.setData(placeList);
        placeRecyclerViewAdapter.notifyDataSetChanged();
        showDivider();
        if (CollectionUtils.isEmpty(placeList)) {
            Toasty.error(this, "No Place found").show();
            hideDivider();
        }
    }

    private void setEmptyAdapter() {

        placeRecyclerViewAdapter.setData(Collections.emptyList());
        placeRecyclerViewAdapter.notifyDataSetChanged();
        hideDivider();
    }

    private void setLocalPlaceAdapter(List<Place> placeList) {

        localPlaceRecyclerViewAdapter = new LocalPlaceRecyclerViewAdapter(placeList, this);
        LocalPlaceRecyclerViewAdapter.onFavouritePlaceListener = this::onPlaceFavourite;
        localRecyclerView.setAdapter(localPlaceRecyclerViewAdapter);
    }

    private void setEmptyLocalAdapter() {

        localPlaceRecyclerViewAdapter.setData(Collections.emptyList());
        localPlaceRecyclerViewAdapter.notifyDataSetChanged();
        hideDivider();
    }


    /**
     * --------Function to get all place information from database----
     **/
    private void getAllPlaceFromLocalDb() {

        new GetAllSelectedPlace(db, placeList -> {

            if (!placeList.isEmpty()) {
                try {// sort data according to favourite value
                    Collections.sort(placeList, (place1, place2) -> Boolean.compare(place2.isFavourite(), place1.isFavourite()));
                    setLocalPlaceAdapter(placeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).execute();
    }

    /**
     * --------Function to save user selected place into database ----
     **/
    private void insertSelectedPlace(Place place) {

        new InsertSelectedPlace(db, result ->
                Log.i(TAG, "Insert Success")).execute(place);
    }

    /**
     * --------Function to filter local data based on user query ----
     **/
    private void filterLocalData(String query) {

        if (localPlaceRecyclerViewAdapter != null) {
            localPlaceRecyclerViewAdapter.getFilter().filter(query); // filter recycler view when query submitted
            Log.i(TAG, "Size=" + localPlaceRecyclerViewAdapter.getItemCount());
        }
    }

    /**
     * --------Function to search query on different location provider ----
     **/
    private void search(String query) {

        filterLocalData(query);
        if (TextUtils.isEmpty(query)) {
            setEmptyAdapter();
            return;
        }
        placeAPI.search(query);
    }

    /**
     * --------Function to update fav info in database ----
     **/
    public void onPlaceFavourite(Place place) {

        new SetFavPlace(db, result -> {
        }).execute(place);
    }

    /**
     * --------Function to show divider if not showing ----
     **/
    private void showDivider() {

        if (tvDivider.getVisibility() != View.VISIBLE)
            tvDivider.setVisibility(View.VISIBLE);
    }

    /**
     * --------Function to show divider if not showing ----
     **/
    private void hideDivider() {

        if (tvDivider.getVisibility() != View.GONE)
            tvDivider.setVisibility(View.GONE);
    }


    /**
     * --------SearchView.OnQueryTextListener >> implementation----
     **/

    public boolean onQueryTextSubmit(String query) {

        placeAPI.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        search(query);
        return false;
    }

    /**
     * --------OnPlaceSelectedListener >> implementation----
     **/
    @Override
    public void onPlaceSelected(Place place) {

        if (mListener != null)
            mListener.onPlaceSelected(place);
        insertSelectedPlace(place);
        finish();
    }

    /**
     * --------OnPlaceListFoundListener >> implementation----
     **/
    @Override
    public void onPlaceListFound(List<Place> placeList) {

        setAdapter(placeList);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {

        Place place = localPlaceRecyclerViewAdapter.getAt(position);
        new DeleteSelectedPlace(db, result -> {
            if (result) {

                localPlaceRecyclerViewAdapter.removeAt(position); // removes from recycler view
                return;
            }
            Toasty.error(this, "Something went wrong! Failed to remove item").show();
            localPlaceRecyclerViewAdapter.notifyItemChanged(position);
        }).execute(place);
    }
}
