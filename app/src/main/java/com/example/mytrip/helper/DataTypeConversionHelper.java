package com.example.mytrip.helper;

import com.example.mytrip.database.entity.SelectedPlace;
import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.model.MyTripInfo;
import com.example.mytrip.model.Place;

import java.util.ArrayList;
import java.util.List;

public class DataTypeConversionHelper {

    public static final int IS_FAV = 1;
    public static final int FAV_NONE = 0;

    public static List<MyTripInfo> convertList(List<TripInfo> tripInfos) {

        List<MyTripInfo> myTripInfoList = new ArrayList<>();
        if (tripInfos != null) {
            for (TripInfo tripInfo : tripInfos) {

                MyTripInfo myTripInfo = convertToMyTripInfo(tripInfo);
                myTripInfoList.add(myTripInfo);
            }
        }

        return myTripInfoList;
    }

    public static TripInfo convertToTripInfo(MyTripInfo myTripInfo) {

        TripInfo tripInfo = new TripInfo();

        if (myTripInfo != null) {
            //tripInfo.setId(myTripInfo.getId());
            tripInfo.setStartAddressName(myTripInfo.getStartPlace().getName());
            tripInfo.setStartAddress(myTripInfo.getStartPlace().getFullAddress());
            tripInfo.setDestinationAddressName(myTripInfo.getDestinationPlace().getName());
            tripInfo.setDestinationAddress(myTripInfo.getDestinationPlace().getFullAddress());
            tripInfo.setStartDateTime(myTripInfo.getStartDateTime());
            tripInfo.setEndDateTime(myTripInfo.getEndDateTime());
        }

        return tripInfo;
    }

    private static MyTripInfo convertToMyTripInfo(TripInfo tripInfo) {

        MyTripInfo myTripInfo = new MyTripInfo();

        if (tripInfo != null) {
            myTripInfo.setId(tripInfo.getId());

            Place startPlace = new Place();
            startPlace.setName(tripInfo.getStartAddressName());
            startPlace.setFullAddress(tripInfo.getStartAddress());
            myTripInfo.setStartPlace(startPlace);

            Place destPlace = new Place();
            destPlace.setName(tripInfo.getDestinationAddressName());
            destPlace.setFullAddress(tripInfo.getDestinationAddress());
            myTripInfo.setDestinationPlace(destPlace);
            
            myTripInfo.setStartDateTime(tripInfo.getStartDateTime());
            myTripInfo.setEndDateTime(tripInfo.getEndDateTime());
        }
        return myTripInfo;
    }

    public static SelectedPlace convertToSelectedPlace(Place place) {

        SelectedPlace selectedPlace = new SelectedPlace();

        selectedPlace.setName(place.getName());
        selectedPlace.setFullAddress(place.getFullAddress());
        selectedPlace.setIsFavourite(FAV_NONE);

        return selectedPlace;
    }

    private static Place convertToPlace(SelectedPlace selectedPlace) {

        Place place = new Place();
        place.setId(selectedPlace.getId());
        place.setName(selectedPlace.getName());
        place.setFullAddress(selectedPlace.getFullAddress());
        place.setFavourite(false);
        if (selectedPlace.getIsFavourite() != FAV_NONE) {
            place.setFavourite(true);
        }
        return place;
    }

    public static List<Place> convertToPlaceList(List<SelectedPlace> selectedPlaces) {

        List<Place> placeList = new ArrayList<>();
        if (selectedPlaces != null) {
            for (SelectedPlace selectedPlace : selectedPlaces) {

                Place place = convertToPlace(selectedPlace);
                placeList.add(place);
            }
        }

        return placeList;
    }
}
