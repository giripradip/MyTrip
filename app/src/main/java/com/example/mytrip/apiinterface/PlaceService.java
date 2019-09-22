package com.example.mytrip.apiinterface;

import com.example.mytrip.model.here.HereResponse;
import com.example.mytrip.model.tomtom.TomTomResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface PlaceService {

    @GET
    Call<TomTomResponse> getAddress(@Url String url);

    @GET
    Call<HereResponse> getPlace(@Url String url);

    @GET
    Call<ResponseBody> getNearByPlace(@Url String url);

}
