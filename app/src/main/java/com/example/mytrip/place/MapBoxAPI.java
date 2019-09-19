package com.example.mytrip.place;

import android.util.Log;

import com.example.mytrip.BuildConfig;
import com.example.mytrip.apiinterface.PlaceService;
import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.model.Place;
import com.example.mytrip.model.mapbox.MapBoxResponse;
import com.example.mytrip.model.mapbox.MapBoxResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapBoxAPI implements PlaceAPI {

    private static final String TAG = MapBoxAPI.class.getSimpleName();

    private String BASE_URL = "https://api.mapbox.com";
    private String token = BuildConfig.MAP_BOX_TOKEN;

    private OnPlaceListFoundListener mListener;

    public MapBoxAPI(OnPlaceListFoundListener listener) {
        mListener = listener;
    }

    /**
     * --------PlaceAPI >> implementation----
     **/
    @Override
    public void search(String query) {

        String url = BASE_URL.concat("geocoding/v5/mapbox.places/").concat(query + ".json&access_token=").concat(token);
        mapBoxSearch(url);
    }

    @Override
    public void nearBySearch(double lat, double lon) {

    }

    private void mapBoxSearch(String url) {

        List<Place> placeList = new ArrayList<>();

        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<MapBoxResponse> call = placeService.mapBoxSearch(url);
        call.enqueue(new Callback<MapBoxResponse>() {
            @Override
            public void onResponse(Call<MapBoxResponse> call, Response<MapBoxResponse> response) {

                if (response.isSuccessful()) {

                    MapBoxResponse mapBoxResponse = response.body();
                    List<MapBoxResult> mapBoxResultList = mapBoxResponse.getMapBoxResultList();
                    if (mapBoxResultList != null) {
                        for (MapBoxResult result : mapBoxResultList) {
                            Place place = new Place();
                            place.setName(result.getName());
                            place.setFullAddress(result.getAddress());
                            placeList.add(place);
                        }
                    }
                    if (mListener != null) {
                        mListener.onPlaceListFound(placeList);
                    }
                    return;
                }
                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
                Log.e(TAG, "Place not found: " + response.errorBody());
            }

            @Override
            public void onFailure(Call<MapBoxResponse> call, Throwable t) {

                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
            }
        });
    }
}
