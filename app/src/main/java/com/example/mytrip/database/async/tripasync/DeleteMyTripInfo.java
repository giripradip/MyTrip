package com.example.mytrip.database.async.tripasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.model.MyTripInfo;

public class DeleteMyTripInfo extends AsyncTask<MyTripInfo, Void, Boolean> {

    private final TripInfoDao tripInfoDao;
    private DeleteResponse delegate;

    public DeleteMyTripInfo(AppDatabase appDb, DeleteResponse response) {
        tripInfoDao = appDb.tripInfoDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final MyTripInfo... params) {

        try {
            MyTripInfo myTripInfo = params[0];
            tripInfoDao.deleteById(myTripInfo.getId());
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

    public interface DeleteResponse {
        void processFinish(Boolean result);
    }
}
