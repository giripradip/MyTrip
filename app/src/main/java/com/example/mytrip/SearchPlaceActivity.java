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
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.adapter.PlaceRecyclerViewAdapter;
import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.async.placeasync.GetAllSelectedPlace;
import com.example.mytrip.database.async.placeasync.InsertSelectedPlace;
import com.example.mytrip.database.async.placeasync.SetFavPlace;
import com.example.mytrip.helper.LocationHelper;
import com.example.mytrip.model.Place;
import com.example.mytrip.place.GooglePlaceAPI;
import com.example.mytrip.place.HerePlaceAPI;
import com.example.mytrip.place.MapBoxAPI;
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
import static com.example.mytrip.constant.HelperConstant.MAP_BOX_API;
import static com.example.mytrip.constant.HelperConstant.SELECTED_SERVER;
import static com.example.mytrip.constant.HelperConstant.TOM_TOM_API;

public class SearchPlaceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        OnPlaceSelectedListener, OnPlaceListFoundListener {

    private static final String TAG = SearchPlaceActivity.class.getSimpleName();

    public static OnPlaceSelectedListener mListener;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private PlaceAPI placeAPI;
    private PlaceRecyclerViewAdapter placeRecyclerViewAdapter;
    private String selectedServer;
    private boolean isFrom;
    private AppDatabase db;
    private int filteredItemCount = 100;
    private boolean isLocalSearchRequired = true;
    private List<Place> placeList;

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

    private void initView() {

        recyclerView = findViewById(R.id.rv_place);
        FloatingActionButton fab = findViewById(R.id.fab_near_me);

        fab.setOnClickListener(view -> getLastLocation());
    }

    private void init() {

        db = AppDatabase.getInstance(this);
        if (TextUtils.isEmpty(selectedServer))
            selectedServer = GOOGLE_API;

        switch (selectedServer) {
            case GOOGLE_API:
                placeAPI = new GooglePlaceAPI(this, this);
                break;

            case HERE_API:
                placeAPI = new HerePlaceAPI(this);
                break;

            case TOM_TOM_API:
                placeAPI = new TomTomPlaceAPI(this);
                break;

            case MAP_BOX_API:
                placeAPI = new MapBoxAPI(this);
                break;

            default:
                break;
        }

        placeRecyclerViewAdapter = new PlaceRecyclerViewAdapter(Collections.emptyList(), this);
        recyclerView.setAdapter(placeRecyclerViewAdapter);
        getAllPlaceFromLocalDb();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLastLocation() {

        if (!LocationHelper.isLocationReady(this)) {
            return;
        }

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        // Write you code here if permission already given.
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Logic to handle location object
                isLocalSearchRequired = false;
                placeAPI.nearBySearch(location.getLatitude(), location.getLongitude());
                return;
            }
            locationUpdate();
        });
        fusedLocationClient.getLastLocation().addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toasty.error(this, getString(R.string.permission_denied)).show();
                }
                break;
            }
        }
    }

    private void locationUpdate() {

        placeAPI.nearBySearch(50.3734597, 7.5207957);
    }

    private void setAdapter(List<Place> placeList, boolean isLocalData) {

        placeRecyclerViewAdapter.setData(placeList);
        placeRecyclerViewAdapter.onFavouritePlaceListener = this::onPlaceFavourite;
        if (!isLocalData) {
            placeRecyclerViewAdapter.onFavouritePlaceListener = null;
        }
        placeRecyclerViewAdapter.notifyDataSetChanged();

        if (CollectionUtils.isEmpty(placeList)) {
            Toasty.error(this, "No Place found").show();
        }
    }

    private void getAllPlaceFromLocalDb() {

        new GetAllSelectedPlace(db, placeList -> {

            if (!placeList.isEmpty()) {
                try {
                    Collections.sort(placeList, (place1, place2) -> Boolean.compare(place2.isFavourite(), place1.isFavourite()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setAdapter(placeList, true);
                this.placeList = placeList;
            }

        }).execute();
    }

    private void insertSelectedPlace(Place place) {

        new InsertSelectedPlace(db, result ->
                Log.i(TAG, "Insert Success")).execute(place);
    }

    private void filterLocalData(String query) {

        if (isLocalSearchRequired) {
            placeRecyclerViewAdapter.getFilter().filter(query); // filter recycler view when query submitted
            filteredItemCount = placeRecyclerViewAdapter.getItemCount();
            Log.i(TAG, "Size=" + placeRecyclerViewAdapter.getItemCount());
        }
    }

    private void search(String query) {

        placeAPI.search(query);
        filterLocalData(query);
    }

    public void onPlaceFavourite(Place place) {

        new SetFavPlace(db, result -> {
        }).execute(place);
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

        if (TextUtils.isEmpty(query)) {
            setAdapter(this.placeList, true);
            return false;
        }
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

        if (filteredItemCount < 5 || !isLocalSearchRequired) {
            setAdapter(placeList, false);
            isLocalSearchRequired = false;
        }
    }
}
