package com.example.mytrip.database.async.tripasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.MyTripInfo;

public class InsertMyTripInfo extends AsyncTask<MyTripInfo, Void, Boolean> {

    private final TripInfoDao tripInfoDao;
    private InsertResponse delegate;

    public InsertMyTripInfo(AppDatabase appDb, InsertResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final MyTripInfo... params) {

        try {
            TripInfo tripInfo = DataTypeConversionHelper.convertToTripInfo(params[0]);
            tripInfoDao.insert(tripInfo);
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

    public interface InsertResponse {
        void processFinish(Boolean result);
    }
}
