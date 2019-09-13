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

    private final TripInfoDao tripInfoDao;
    private GetAllResponse delegate;

    public GetAllMyTripInfo(AppDatabase appDb, GetAllResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected List<MyTripInfo> doInBackground(final Void... params) {

        List<MyTripInfo> myTripInfoList;
        try {
            List<TripInfo> tripInfos = tripInfoDao.getAll();
            myTripInfoList = DataTypeConversionHelper.convertList(tripInfos);
            return myTripInfoList;
        } catch (Exception e) {
            e.printStackTrace();
            myTripInfoList = new ArrayList<MyTripInfo>();
            return myTripInfoList;
        }

    }

    @Override
    protected void onPostExecute(List<MyTripInfo> result) {
        delegate.processFinish(result);
    }

    public interface GetAllResponse {
        void processFinish(List<MyTripInfo> myTripInfoList);
    }


}
