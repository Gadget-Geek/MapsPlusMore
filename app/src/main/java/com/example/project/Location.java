package com.example.project;

public class Location {

    private int LocationID;
    private double latitude;
    private double longitude;

    public Location(int LocationID, double lat, double lon){

        setLocationID(LocationID);
        setLatitude(lat);
        setLongitude(lon);
    }

    public int getLocationID(){ return LocationID; }
    public double getLatitude(){ return latitude; }
    public double getLongitude(){ return longitude; }

    public void setLocationID(int LocationID ) { this.LocationID = LocationID; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

}
