package com.example.mytrip.apiinterface;

import com.example.mytrip.model.MyTripInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TripInfoSyncService {

    @POST("trip/all")
    Call<List<MyTripInfo>> syncAllTripInfo(@Body List<MyTripInfo> myTripInfoList);

    @POST("trip")
    Call<MyTripInfo> syncCreateTripInfo(@Body MyTripInfo myTripInfo);

    @PUT("trip/{id}")
    Call<MyTripInfo> syncUpdateTripInfo(@Body MyTripInfo myTripInfo, @Path("id") int tripId);


    @DELETE("trip/{id}")
    Call<MyTripInfo> syncDeleteTripInfo(@Path("id") int tripId);
}
