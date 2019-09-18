package com.example.mytrip.database.async.placeasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.SelectedPlaceDao;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.Place;


public class SetFavPlace extends AsyncTask<Place, Void, Boolean> {

    private final SelectedPlaceDao selectedPlaceDao;
    private SetFavResponse delegate;

    public SetFavPlace(AppDatabase appDb, SetFavResponse response) {
        selectedPlaceDao = appDb.selectedPlaceDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final Place... params) {

        try {
            Place place = params[0];
            int fav = DataTypeConversionHelper.FAV_NONE;
            if (place.isFavourite()) {
                fav = DataTypeConversionHelper.IS_FAV;
            }
            selectedPlaceDao.update(place.getId(), fav);
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

    public interface SetFavResponse {

        void processFinish(Boolean result);
    }
}
