package com.example.mytrip.place;

import android.text.TextUtils;
import android.util.Log;

import com.example.mytrip.BuildConfig;
import com.example.mytrip.apiinterface.PlaceService;
import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.model.Place;
import com.example.mytrip.model.here.HereAddress;
import com.example.mytrip.model.here.HereNearByResponse;
import com.example.mytrip.model.here.HereResponse;
import com.example.mytrip.model.here.HereResult;
import com.example.mytrip.model.here.HereSearchResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HerePlaceAPI implements PlaceAPI {

    private static final String TAG = HerePlaceAPI.class.getSimpleName();

    private OnPlaceListFoundListener mListener;
    private String BASE_URL = "https://places.cit.api.here.com/places/v1/";
    private String END_URL = "&app_id=" + BuildConfig.HERE_APP_ID + "&app_code=" + BuildConfig.HERE_APP_CODE;

    public HerePlaceAPI(OnPlaceListFoundListener listener) {

        mListener = listener;
    }

    /**
     * --------PlaceAPI >> implementation----
     **/
    @Override
    public void search(String query) {

        String url = generateUrl(query);
        hereSearch(url);
    }

    @Override
    public void nearBySearch(double lat, double lon) {

        String endUrl = "?app_id=" + BuildConfig.HERE_APP_ID + "&app_code=" + BuildConfig.HERE_APP_CODE;
        String url = BASE_URL.concat("discover/here").concat(endUrl).concat("&at="+lat).concat(","+lon).concat("&pretty");
        hereNearBy(url);
    }

    private void hereSearch(String url) {

        List<Place> placeList = new ArrayList<>();

        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<HereResponse> call = placeService.getPlace(url);
        call.enqueue(new Callback<HereResponse>() {
            @Override
            public void onResponse(Call<HereResponse> call, Response<HereResponse> response) {

                if (response.isSuccessful()) {
                    HereResponse hereResponse = response.body();
                    List<HereResult> results = hereResponse.getHereResults();
                    for (HereResult result : results) {
                        Place place = new Place();
                        place.setName(result.getTitle());
                        if (!TextUtils.isEmpty(result.getVicinity())) {
                            place.setFullAddress(formatString(result.getVicinity()));
                        }
                        placeList.add(place);
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
            public void onFailure(Call<HereResponse> call, Throwable t) {
                Log.e(TAG, "Error Occured: " + t.getLocalizedMessage());
                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
            }
        });
    }

    private void hereNearBy(String url) {

        List<Place> placeList = new ArrayList<>();

        PlaceService placeService = ServiceGenerator.createService(PlaceService.class);
        Call<HereNearByResponse> call = placeService.getNearByPlace(url);
        call.enqueue(new Callback<HereNearByResponse>() {
            @Override
            public void onResponse(Call<HereNearByResponse> call, Response<HereNearByResponse> response) {

                if(response.isSuccessful()) {

                    try {
                        HereNearByResponse hereNearByResponse = response.body();
                        HereSearchResult hereSearchResult = hereNearByResponse.getSearchResult();

                        HereAddress address = hereSearchResult.getAddress();
                        System.out.println("Result=" + hereSearchResult.getAddress());


                        Place place = new Place();
                        //place.setName(address.getShortAddress());
                        place.setFullAddress(address.getFullAddress());
                        placeList.add(place);
                    }catch (Exception e) {

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
            public void onFailure(Call<HereNearByResponse> call, Throwable t) {
                Log.e(TAG, "Error Occured: " + t.getLocalizedMessage());
                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
            }
        });


    }


    private String generateUrl(String query) {

        return BASE_URL.concat("autosuggest?at=40.74917,-73.98529&q=").concat(query).concat(END_URL.concat("&pretty"));
    }

    private String formatString(String text) {

        return text.replace("<br/>", ", ");
    }
}
