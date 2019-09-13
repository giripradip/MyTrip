package com.example.mytrip.database.async;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.MyTripInfo;

import java.util.ArrayList;
import java.util.List;

public class GetAllMyTripInfo extends AsyncTask<Void, Void, List<MyTripInfo>> {

    public interface GetAllResponse {
        void processFinish(List<MyTripInfo> myTripInfoList);
    }

    private final TripInfoDao tripInfoDao;
    private GetAllResponse delegate;

    public GetAllMyTripInfo(AppDatabase appDb, GetAllResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected List<MyTripInfo> doInBackground(final Void... params) {

        List<TripInfo> tripInfos = tripInfoDao.getAll();
        List<MyTripInfo> myTripInfoList = DataTypeConversionHelper.convertList(tripInfos);
        return myTripInfoList;
    }

    @Override
    protected void onPostExecute(List<MyTripInfo> result) {
        delegate.processFinish(result);
    }


}
