package com.hardcastle.honeysuckervendor.Model;

/**
 * Created by abhijeet on 1/10/2018.
 */

public class LatLongClass {

    private String latitude;
    private String longitude;

    public LatLongClass(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
