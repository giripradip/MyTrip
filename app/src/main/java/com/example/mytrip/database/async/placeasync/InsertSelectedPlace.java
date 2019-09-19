package com.example.mytrip.database.async.placeasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.SelectedPlaceDao;
import com.example.mytrip.database.entity.SelectedPlace;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.Place;

public class InsertSelectedPlace extends AsyncTask<Place, Void, Boolean> {

    private final SelectedPlaceDao selectedPlaceDao;
    private Response delegate;

    public InsertSelectedPlace(AppDatabase appDb, Response response) {
        selectedPlaceDao = appDb.selectedPlaceDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final Place... params) {

        try {
            SelectedPlace selectedPlace = DataTypeConversionHelper.convertToSelectedPlace(params[0]);
            selectedPlaceDao.insert(selectedPlace);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        delegate.processFinish(result);
    }

    public interface Response {
        void processFinish(Boolean result);
    }
}