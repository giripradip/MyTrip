package com.example.mytrip.model.here;

import com.google.gson.annotations.SerializedName;

public class HereNearByResponse {

    @SerializedName("search")
    private HereSearchResult searchResult;

    @SerializedName("results")
    private HereResult result;

    public HereSearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(HereSearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public HereResult getResult() {
        return result;
    }

    public void setResult(HereResult result) {
        this.result = result;
    }
}
