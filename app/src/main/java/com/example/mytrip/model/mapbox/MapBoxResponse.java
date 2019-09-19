package com.example.mytrip.model.mapbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapBoxResponse {

    @SerializedName("features")
    private List<MapBoxResult> mapBoxResultList;

    public List<MapBoxResult> getMapBoxResultList() {
        return mapBoxResultList;
    }

    public void setMapBoxResultList(List<MapBoxResult> mapBoxResultList) {
        this.mapBoxResultList = mapBoxResultList;
    }
}
