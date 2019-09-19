package com.example.mytrip.model;

public class PlaceWrapper {

    public Place p;

    public PlaceWrapper(Place p1) {
        p = p1;
    }

    public static void swap(PlaceWrapper startP, PlaceWrapper endP) {

        Place temp = startP.p;
        startP.p = endP.p;
        endP.p = temp;
    }
}
