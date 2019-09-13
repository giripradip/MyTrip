package com.example.mytrip.database.async;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.model.MyTripInfo;

public class InsertMyTripInfo extends AsyncTask<MyTripInfo, Void, Boolean> {

    public interface InsertResponse {
        void processFinish(Boolean result);
    }

    private final TripInfoDao tripInfoDao;
    InsertResponse delegate;

    public InsertMyTripInfo(AppDatabase appDb, InsertResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final MyTripInfo... params) {

        TripInfo tripInfo = prepareTripInfo(params[0]);
        tripInfoDao.insert(tripInfo);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }

    private TripInfo prepareTripInfo(MyTripInfo myTripInfo) {

        TripInfo tripInfo = new TripInfo();

        if (myTripInfo != null) {
            //tripInfo.setId(myTripInfo.getId());
            myTripInfo.setStartAddressId(myTripInfo.getStartAddressId());
            tripInfo.setStartAddressName(myTripInfo.getStartAddressName());
            tripInfo.setStartAddress(myTripInfo.getStartAddress());
            tripInfo.setDestinationAddressId(myTripInfo.getDestinationAddressId());
            tripInfo.setDestinationAddressName(myTripInfo.getDestinationAddressName());
            tripInfo.setDestinationAddress(myTripInfo.getDestinationAddress());
            tripInfo.setStartDateTime(myTripInfo.getStartDateTime());
            tripInfo.setEndDateTime(myTripInfo.getEndDateTime());
        }

        return tripInfo;
    }
}
