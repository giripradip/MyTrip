package com.example.mytrip.model;

public class MyTripInfo {

    private int id;
    private String startAddressId;
    private String startAddressName;
    private String startAddress;
    private String destinationAddressId;
    private String destinationAddressName;
    private String destinationAddress;
    private int startDateTime;
    private int endDateTime;

    private Place startPlace;
    private Place destinationPlace;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartAddressId() {
        return startAddressId;
    }

    public void setStartAddressId(String startAddressId) {
        this.startAddressId = startAddressId;
    }

    public String getStartAddressName() {
        return startAddressName;
    }

    public void setStartAddressName(String startAddressName) {
        this.startAddressName = startAddressName;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getDestinationAddressId() {
        return destinationAddressId;
    }

    public void setDestinationAddressId(String destinationAddressId) {
        this.destinationAddressId = destinationAddressId;
    }

    public String getDestinationAddressName() {
        return destinationAddressName;
    }

    public void setDestinationAddressName(String destinationAddressName) {
        this.destinationAddressName = destinationAddressName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(int startDateTime) {
        this.startDateTime = startDateTime;
    }

    public int getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(int endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Place getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(Place startPlace) {
        this.startPlace = startPlace;
    }

    public Place getDestinationPlace() {
        return destinationPlace;
    }

    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }
}
