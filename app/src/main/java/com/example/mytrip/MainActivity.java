package com.example.mytrip;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytrip.adapter.PlaceRecyclerViewAdapter;
import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.model.Place;
import com.example.mytrip.place.GooglePlaceAPI;
import com.example.mytrip.place.HerePlaceAPI;
import com.example.mytrip.place.OnPlaceListFoundListener;
import com.example.mytrip.place.PlaceAPI;
import com.example.mytrip.place.TomTomPlaceAPI;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.mytrip.constant.HelperConstant.GOOGLE_API;
import static com.example.mytrip.constant.HelperConstant.HERE_API;
import static com.example.mytrip.constant.HelperConstant.IS_FROM;
import static com.example.mytrip.constant.HelperConstant.SELECTED_SERVER;
import static com.example.mytrip.constant.HelperConstant.TOM_TOM_API;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        OnPlaceSelectedListener, OnPlaceListFoundListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static OnPlaceSelectedListener mListener;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private PlaceAPI placeAPI;
    private PlaceRecyclerViewAdapter placeRecyclerViewAdapter;
    private String selectedServer;
    private boolean isFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        return true;
    }

    private void initView() {

        recyclerView = findViewById(R.id.rv_place);
    }

    private void init() {

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

            default:
                break;
        }
    }


    private void setAdapter(List<Place> placeList) {

        if (!CollectionUtils.isEmpty(placeList)) {
            placeRecyclerViewAdapter = new PlaceRecyclerViewAdapter(placeList, this);
            recyclerView.setAdapter(placeRecyclerViewAdapter);
            return;
        }
        placeRecyclerViewAdapter.setData(Collections.emptyList());
        placeRecyclerViewAdapter.notifyDataSetChanged();
        Toasty.error(this, "No Place found").show();
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

        placeAPI.search(query);
        return false;
    }

    /**
     * --------OnPlaceSelectedListener >> implementation----
     **/
    @Override
    public void onPlaceSelected(Place place) {

        if (mListener != null)
            mListener.onPlaceSelected(place);
        finish();
    }

    /**
     * --------OnPlaceListFoundListener >> implementation----
     **/
    @Override
    public void onPlaceListFound(List<Place> placeList) {

        setAdapter(placeList);
    }
}
