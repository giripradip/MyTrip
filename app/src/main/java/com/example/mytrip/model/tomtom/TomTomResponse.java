package com.example.mytrip.model.tomtom;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TomTomResponse {

    @SerializedName("results")
    private List<TomTomResult> results;

    public List<TomTomResult> getResults() {
        return results;
    }

    public void setResults(List<TomTomResult> results) {
        this.results = results;
    }
}
