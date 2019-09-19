package com.example.mytrip.place;

import android.content.Context;

public interface PlaceAPI {

    void search(String query);

    void nearBySearch(double latitude, double longitude);
}
