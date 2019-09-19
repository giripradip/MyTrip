package com.example.mytrip.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TripInfo {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "startAddressName")
    private String startAddressName;

    @ColumnInfo(name = "startAddress")
    private String startAddress;

    @ColumnInfo(name = "destinationAddressName")
    private String destinationAddressName;

    @ColumnInfo(name = "destinationAddress")
    private String destinationAddress;

    @ColumnInfo(name = "startDateTime")
    private int startDateTime;

    @ColumnInfo(name = "endDateTime")
    private int endDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
