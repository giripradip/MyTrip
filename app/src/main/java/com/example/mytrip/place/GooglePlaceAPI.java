package com.example.mytrip.place;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.mytrip.BuildConfig;
import com.example.mytrip.helper.Helper;
import com.example.mytrip.helper.LocationHelper;
import com.example.mytrip.model.Place;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GooglePlaceAPI implements PlaceAPI {

    private static final String TAG = GooglePlaceAPI.class.getSimpleName();

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    private Context context;
    // Create a new Places client instance
    private PlacesClient placesClient;
    private FindAutocompletePredictionsRequest request;
    private static OnPlaceListFoundListener mListener;
    private AutocompleteSessionToken token;
    private String apiKey = BuildConfig.GOOGLE_API_KEY;

    public GooglePlaceAPI(Context context, OnPlaceListFoundListener listener) {

        if (!Places.isInitialized()) {
            // Initialize the SDK
            Places.initialize(context.getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(context);
        this.context = context;
        mListener = listener;
    }

    /**
     * --------PlaceAPI >> implementation----
     **/
    @Override
    public void search(String query) {
        googleSearch(query);
    }

    @Override
    public void nearBySearch(double lat, double lon) {

        googleSearch(lat, lon);
    }

    private void googleSearch(String query) {

        List<Place> placeList = new ArrayList<>();
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        if (token == null) {
            token = AutocompleteSessionToken.newInstance();
        }
        // Use the builder to create a FindAutocompletePredictionsRequest.
        request = FindAutocompletePredictionsRequest.builder()
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

    private void googleSearch(double lat, double lon) {

        List<Place> placeList = new ArrayList<>();
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.ADDRESS);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                        Place place = new Place();
                        place.setName(placeLikelihood.getPlace().getName());
                        place.setFullAddress(placeLikelihood.getPlace().getAddress());
                        placeList.add(place);
                    }
                    if (mListener != null) {
                        mListener.onPlaceListFound(placeList);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        if (mListener != null) {
                            mListener.onPlaceListFound(placeList);
                        }
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            LocationHelper.requestLocationPermission(context);
        }
    }
}
