package com.example.mytrip.helper;

import com.example.mytrip.database.entity.TripInfo;
import com.example.mytrip.model.MyTripInfo;

import java.util.ArrayList;
import java.util.List;

public class DataTypeConversionHelper {

    public static List<MyTripInfo> convertList(List<TripInfo> tripInfos) {

        List<MyTripInfo> myTripInfoList = new ArrayList<MyTripInfo>();
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
            tripInfo.setStartAddressId(myTripInfo.getStartAddressId());
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

    public static MyTripInfo convertToMyTripInfo(TripInfo tripInfo) {

        MyTripInfo myTripInfo = new MyTripInfo();

        if (tripInfo != null) {
            myTripInfo.setId(tripInfo.getId());
            myTripInfo.setStartAddressId(tripInfo.getStartAddressId());
            myTripInfo.setStartAddressName(tripInfo.getStartAddressName());
            myTripInfo.setStartAddress(tripInfo.getStartAddress());
            myTripInfo.setDestinationAddressId(tripInfo.getDestinationAddressId());
            myTripInfo.setDestinationAddressName(tripInfo.getDestinationAddressName());
            myTripInfo.setDestinationAddress(tripInfo.getDestinationAddress());
            myTripInfo.setStartDateTime(tripInfo.getStartDateTime());
            myTripInfo.setEndDateTime(tripInfo.getEndDateTime());
        }
        return myTripInfo;
    }
}
