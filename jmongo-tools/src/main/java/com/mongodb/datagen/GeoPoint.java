package com.mongodb.datagen;

public class GeoPoint {
    
    double latitude;
    double longitude;
    
    public GeoPoint(double lat, double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
