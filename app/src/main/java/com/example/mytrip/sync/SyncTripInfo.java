package com.example.mytrip.sync;

import android.content.Context;
import android.util.Log;

import com.example.mytrip.R;
import com.example.mytrip.apiinterface.ServiceGenerator;
import com.example.mytrip.apiinterface.TripInfoSyncService;
import com.example.mytrip.fragment.HomeFragment;
import com.example.mytrip.helper.PrefManager;
import com.example.mytrip.model.MyTripInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncTripInfo {

    private static final String TAG = SyncTripInfo.class.getSimpleName();
    private Context context;
    private PrefManager prefManager;

    public SyncTripInfo(Context context) {

        this.context = context;
        prefManager = new PrefManager(context);
    }

    public void syncAllTripInfo(String authToken, List<MyTripInfo> myTripInfoList) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<List<MyTripInfo>> call = tripInfoSyncService.syncAllTripInfo(myTripInfoList);
        call.enqueue(new Callback<List<MyTripInfo>>() {
            @Override
            public void onResponse(Call<List<MyTripInfo>> call, Response<List<MyTripInfo>> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, context.getString(R.string.sync_all_success));
                    setIsSyncRequired(false);
                    return;
                }
                Log.e(TAG, context.getString(R.string.sync_all_failed));
                setIsSyncRequired(true);
            }

            @Override
            public void onFailure(Call<List<MyTripInfo>> call, Throwable t) {
                Log.e(TAG, context.getString(R.string.sync_all_failed));
                setIsSyncRequired(true);
            }
        });
    }

    public void syncCreateTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncCreateTripInfo(myTripInfo);
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, context.getString(R.string.sync_create_success));
                    setIsSyncRequired(false);
                    return;
                }
                setIsSyncRequired(true);
                Log.e(TAG, context.getString(R.string.sync_create_failed));
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, context.getString(R.string.sync_create_failed));
                setIsSyncRequired(true);
            }
        });
    }

    public void syncUpdateTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncUpdateTripInfo(myTripInfo, myTripInfo.getId());
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, context.getString(R.string.sync_update_success));
                    setIsSyncRequired(false);
                    return;
                }
                setIsSyncRequired(true);
                Log.e(TAG, context.getString(R.string.sync_update_failed));
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, context.getString(R.string.sync_update_failed));
                setIsSyncRequired(true);
            }
        });
    }

    public void syncDeleteTripInfo(String authToken, MyTripInfo myTripInfo) {

        TripInfoSyncService tripInfoSyncService = ServiceGenerator.createService(TripInfoSyncService.class, authToken);
        Call<MyTripInfo> call = tripInfoSyncService.syncDeleteTripInfo(myTripInfo.getId());
        call.enqueue(new Callback<MyTripInfo>() {
            @Override
            public void onResponse(Call<MyTripInfo> call, Response<MyTripInfo> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, context.getString(R.string.sync_delete_success));
                    setIsSyncRequired(false);
                    return;
                }
                setIsSyncRequired(true);
                Log.e(TAG, context.getString(R.string.sync_delete_failed));
            }

            @Override
            public void onFailure(Call<MyTripInfo> call, Throwable t) {
                Log.e(TAG, context.getString(R.string.sync_delete_failed));
                setIsSyncRequired(true);
            }
        });
    }

    private void setIsSyncRequired(boolean val) {

        prefManager.setIsSyncRequired(val);
    }
}
