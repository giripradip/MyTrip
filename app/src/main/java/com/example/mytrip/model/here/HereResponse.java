package com.example.mytrip.model.here;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HereResponse {

    @SerializedName("results")
    private List<HereResult> hereResults;

    public List<HereResult> getHereResults() {
        return hereResults;
    }

    public void setHereResults(List<HereResult> hereResults) {
        this.hereResults = hereResults;
    }
}
