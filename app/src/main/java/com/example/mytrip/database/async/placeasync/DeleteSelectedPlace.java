package com.example.mytrip.database.async.placeasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.SelectedPlaceDao;
import com.example.mytrip.model.Place;

public class DeleteSelectedPlace extends AsyncTask<Place, Void, Boolean> {

    private final SelectedPlaceDao selectedPlaceDao;
    private DeleteResponse delegate;

    public DeleteSelectedPlace(AppDatabase appDb, DeleteResponse response) {
        selectedPlaceDao = appDb.selectedPlaceDao();
        delegate = response;
    }

    @Override
    protected Boolean doInBackground(final Place... params) {

        try {
            Place place = params[0];
            selectedPlaceDao.deleteById(place.getId());
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