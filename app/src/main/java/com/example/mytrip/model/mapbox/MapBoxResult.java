package com.example.mytrip.model.mapbox;

import com.google.gson.annotations.SerializedName;

public class MapBoxResult {

    @SerializedName("text")
    private String name;

    @SerializedName("place_name")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
