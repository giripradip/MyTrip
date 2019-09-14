package com.example.mytrip.sync;

import android.util.Log;

import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.apiinterface.TripInfoSyncService;
import com.example.mytrip.fragment.HomeFragment;
import com.example.mytrip.model.MyTripInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncTripInfo {

    private static final String TAG = SyncTripInfo.class.getSimpleName();

    public static void syncAllTripInfo(String authToken, List<MyTripInfo> myTripInfoList) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<List<MyTripInfo>> call = tripInfoSyncService.syncAllTripInfo(myTripInfoList);
        call.enqueue(new Callback<List<MyTripInfo>>() {
            @Override
            public void onResponse(Call<List<MyTripInfo>> call, Response<List<MyTripInfo>> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Sync All Success");
                    return;
                }
                Log.e(TAG, "Sync All Failed");
            }

            @Override
            public void onFailure(Call<List<MyTripInfo>> call, Throwable t) {
                Log.e(TAG, "Sync All Failed");
                System.out.println(t.getMessage());
            }
        });
    }

    public static void syncCreateTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncCreateTripInfo(myTripInfo);
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Sync Create Trip Info Success");
                    return;
                }
                Log.e(TAG, "Sync Create Trip Info Failed");
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, "Sync Create Trip Info Failed");
            }
        });
    }

    public static void syncUpdateTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncUpdateTripInfo(myTripInfo, myTripInfo.getId());
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Sync Update Trip Info Success");
                    return;
                }
                Log.e(TAG, "Sync Update Trip Info Failed");
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, "Sync Update Trip Info Failed");
            }
        });
    }

    public static void syncDeleteTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncDeleteTripInfo(myTripInfo.getId());
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "Sync Delete Trip Info Success");
                    return;
                }
                Log.e(TAG, "Sync Delete Trip Info Failed");
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, "Sync Delete Trip Info Failed");
            }
        });
    }
}
