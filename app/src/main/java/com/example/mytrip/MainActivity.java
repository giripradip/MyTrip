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
import com.example.mytrip.apiinterface.PlaceService;
import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.custominterface.OnPlaceSelectedListener;
import com.example.mytrip.model.Place;
import com.example.mytrip.model.here.HereResponse;
import com.example.mytrip.model.here.HereResult;
import com.example.mytrip.model.tomtom.TomTomResponse;
import com.example.mytrip.model.tomtom.TomTomResult;
import com.example.mytrip.place.GooglePlaceAPI;
import com.example.mytrip.place.OnPlaceListFoundListener;
import com.example.mytrip.place.PlaceAPI;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        OnPlaceSelectedListener, OnPlaceListFoundListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static OnPlaceSelectedListener mListener;

    private SearchView searchView;
    private RecyclerView recyclerView;

    private PlaceAPI placeAPI;
    private PlaceRecyclerViewAdapter placeRecyclerViewAdapter;

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
        searchView.setQueryHint("Start Address");
        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void initView() {

        recyclerView = findViewById(R.id.rv_place);
    }

    private void init() {

        placeAPI = new GooglePlaceAPI(this, this);
    }

    private void search(String query) {

        String url = "https://api.tomtom.com/search/2/search/".concat(query).concat(".json?key=")
                .concat(BuildConfig.TOM_TOM_API_KEY).concat("&typeahead=true");
        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<TomTomResponse> call = placeService.getAddress(url);
        call.enqueue(new Callback<TomTomResponse>() {
            @Override
            public void onResponse(Call<TomTomResponse> call, Response<TomTomResponse> response) {

                if (response.isSuccessful()) {
                    TomTomResponse tomTomResponse = response.body();
                    List<TomTomResult> results = tomTomResponse.getResults();
                    List<Place> placeList = new ArrayList<>();
                    for (TomTomResult result : results) {
                        if (result.getAddress() != null) {
                            Place place = new Place();
                            place.setFullAddress(result.getAddress().getFreeformAddress());
                            placeList.add(place);
                        }
                    }
                    setAdapter(placeList);
                }
            }

            @Override
            public void onFailure(Call<TomTomResponse> call, Throwable t) {

            }
        });
    }

    private void hereSearch(String query) {

        String url = "https://places.cit.api.here.com/places/v1/autosuggest?at=40.74917,-73.98529&q=" + query
                + "&app_id=" + BuildConfig.HERE_APP_ID + "&app_code=" + BuildConfig.HERE_APP_CODE;

        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<HereResponse> call = placeService.getPlace(url);
        call.enqueue(new Callback<HereResponse>() {
            @Override
            public void onResponse(Call<HereResponse> call, Response<HereResponse> response) {

                if (response.isSuccessful()) {
                    HereResponse hereResponse = response.body();
                    List<HereResult> results = hereResponse.getHereResults();
                    List<Place> placeList = new ArrayList<>();
                    for (HereResult result : results) {
                        Place place = new Place();
                        place.setName(result.getTitle());
                        if (!TextUtils.isEmpty(result.getVicinity())) {
                            place.setFullAddress(formatString(result.getVicinity()));
                        }

                        placeList.add(place);
                    }
                    setAdapter(placeList);
                }
            }

            @Override
            public void onFailure(Call<HereResponse> call, Throwable t) {

            }
        });
    }

    private void setAdapter(List<Place> placeList) {

        if (!CollectionUtils.isEmpty(placeList)) {
            placeRecyclerViewAdapter = new PlaceRecyclerViewAdapter(placeList, this);
            recyclerView.setAdapter(placeRecyclerViewAdapter);
            return;
        }
        Toasty.error(this, "No Place found").show();
    }

    private String formatString(String text) {

        return text.replace("<br/>", ", ");
    }


    /**
     * --------SearchView.OnQueryTextListener >> implementation----
     **/

    public boolean onQueryTextSubmit(String query) {

        System.out.println(query);
        placeAPI.search(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        System.out.println(query);
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
