package com.example.mytrip.model.here;

import com.google.gson.annotations.SerializedName;

public class HereSearchResult {

    @SerializedName("address")
    private HereAddress address;

    public HereAddress getAddress() {
        return address;
    }

    public void setAddress(HereAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address.toString();
    }
}
