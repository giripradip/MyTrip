package com.example.mytrip.place;

import android.content.Context;
import android.util.Log;

import com.example.mytrip.BuildConfig;
import com.example.mytrip.model.Place;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class GooglePlaceAPI implements PlaceAPI {

    private static final String TAG = GooglePlaceAPI.class.getSimpleName();

    // Create a new Places client instance
    private PlacesClient placesClient;
    private static OnPlaceListFoundListener mListener;
    private AutocompleteSessionToken token;
    private String apiKey = BuildConfig.GOOGLE_API_KEY;

    public GooglePlaceAPI(Context context, OnPlaceListFoundListener listener) {

        if (!Places.isInitialized()) {
            // Initialize the SDK
            Places.initialize(context.getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(context);
        mListener = listener;
    }

    /**
     * --------PlaceAPI >> implementation----
     **/
    @Override
    public void search(String query) {
        googleSearch(query);
    }

    private void googleSearch(String query) {

        List<Place> placeList = new ArrayList<>();
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        if (token == null) {
            token = AutocompleteSessionToken.newInstance();
        }
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                // .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                //.setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Place place = new Place();
                place.setName(prediction.getPrimaryText(null).toString());
                place.setFullAddress(prediction.getFullText(null).toString());
                placeList.add(place);
            }
            if (mListener != null) {
                mListener.onPlaceListFound(placeList);
            }
            //setAdapter(placeList);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                if (mListener != null) {
                    mListener.onPlaceListFound(placeList);
                }
            }
        });
    }
}
