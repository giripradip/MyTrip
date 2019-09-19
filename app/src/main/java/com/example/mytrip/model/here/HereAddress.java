package com.example.mytrip.model.here;

import com.google.gson.annotations.SerializedName;

public class HereAddress {

    @SerializedName("text")
    private String fullAddress;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

   /* public String getShortAddress() {

        return getCity().concat(", ").concat(getCountry());
    }*/

    @Override
    public String toString() {
        return getFullAddress();
    }
}
