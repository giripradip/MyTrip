package com.example.mytrip.place;

import android.util.Log;

import com.example.mytrip.BuildConfig;
import com.example.mytrip.apiinterface.PlaceService;
import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.model.Place;
import com.example.mytrip.model.tomtom.TomTomResponse;
import com.example.mytrip.model.tomtom.TomTomResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TomTomPlaceAPI implements PlaceAPI {

    private static final String TAG = TomTomPlaceAPI.class.getSimpleName();

    private OnPlaceListFoundListener mListener;
    private String BASE_URL = "https://api.tomtom.com/search/2/search/";
    private String END_URL = ".json?key=".concat(BuildConfig.TOM_TOM_API_KEY).concat("&typeahead=true");

    public TomTomPlaceAPI(OnPlaceListFoundListener listener) {

        mListener = listener;
    }


    /**
     * --------PlaceAPI >> implementation----
     **/
    @Override
    public void search(String query) {

        tomTomSearch(query);
    }

    private void tomTomSearch(String query) {

        List<Place> placeList = new ArrayList<>();
        String url = generateUrl(query);

        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<TomTomResponse> call = placeService.getAddress(url);
        call.enqueue(new Callback<TomTomResponse>() {
            @Override
            public void onResponse(Call<TomTomResponse> call, Response<TomTomResponse> response) {

                if (response.isSuccessful()) {
                    TomTomResponse tomTomResponse = response.body();
                    List<TomTomResult> results = tomTomResponse.getResults();
                    if (results == null)
                        return;
                    for (TomTomResult result : results) {
                        if (result.getAddress() != null) {
                            Place place = new Place();
                            place.setName(result.getAddress().getMunicipality());
                            place.setFullAddress(result.getAddress().getFreeformAddress());
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
            public void onFailure(Call<TomTomResponse> call, Throwable t) {
                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
            }
        });
    }

    private String generateUrl(String query) {

        return BASE_URL.concat(query).concat(END_URL);
    }
}
