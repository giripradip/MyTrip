package com.example.mytrip.database.async.placeasync;

import android.os.AsyncTask;

import com.example.mytrip.database.AppDatabase;
import com.example.mytrip.database.Dao.SelectedPlaceDao;
import com.example.mytrip.database.entity.SelectedPlace;
import com.example.mytrip.helper.DataTypeConversionHelper;
import com.example.mytrip.model.Place;

import java.util.ArrayList;
import java.util.List;

public class GetAllSelectedPlace extends AsyncTask<Void, Void, List<Place>> {

    private final SelectedPlaceDao selectedPlaceDao;
    private GetAllResponse delegate;

    public GetAllSelectedPlace(AppDatabase appDb, GetAllResponse response) {
        selectedPlaceDao = appDb.selectedPlaceDao();
        delegate = response;
    }

    @Override
    protected List<Place> doInBackground(final Void... params) {

        List<Place> placeList;
        try {
            List<SelectedPlace> selectedPlaces = selectedPlaceDao.getAllSelectedPlace();
            placeList = DataTypeConversionHelper.convertToPlaceList(selectedPlaces);
            return placeList;
        } catch (Exception e) {
            e.printStackTrace();
            placeList = new ArrayList<>();
            return placeList;
        }

    }

    @Override
    protected void onPostExecute(List<Place> result) {
        delegate.processFinish(result);
    }

    public interface GetAllResponse {
        void processFinish(List<Place> placeList);
    }


}
