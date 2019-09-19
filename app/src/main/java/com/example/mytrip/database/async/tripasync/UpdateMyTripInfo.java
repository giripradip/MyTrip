package com.example.mytrip.database.async.tripasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.MyTripInfo;

public class UpdateMyTripInfo extends AsyncTask<MyTripInfo, Void, Boolean> {

    private final TripInfoDao tripInfoDao;
    private UpdateResponse delegate;

    public UpdateMyTripInfo(AppDatabase appDb, UpdateResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final MyTripInfo... params) {

        try {
            MyTripInfo tripInfo = params[0];
            String sName = tripInfo.getStartPlace().getName();
            String sAdd = tripInfo.getStartPlace().getFullAddress();
            String dName = tripInfo.getDestinationPlace().getName();
            String dAdd = tripInfo.getDestinationPlace().getFullAddress();
            tripInfoDao.update(tripInfo.getId(), sName, sAdd, tripInfo.getStartDateTime(), dName, dAdd, tripInfo.getEndDateTime());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }

    public interface UpdateResponse {
        void processFinish(Boolean result);
    }
}


