package com.example.project;

public class Location {

    private int streetNumber;
    private String streetName;
    private String city;
    private String province;
    private String nameOfLocation;

    public Location(String nameOfLocation, int streetNumber, String streetName, String city, String province){

        setStreetNumber(streetNumber);
        setStreetName(streetName);
        setCity(city);
        setProvince(province);
        setNameOfLocation(nameOfLocation);
    }

    public int getStreetNumber(){ return streetNumber; }
    public String getStreetName(){ return streetName; }
    public String getCity(){ return city; }
    public String getProvince(){ return province; }
    public String getNameOfLocation(){ return nameOfLocation; }

    public void setStreetNumber(int streetNumber) {this.streetNumber = streetNumber; }
    public void setStreetName(String streetName) {this.streetName = streetName; }
    public void setCity(String city) {this.city = city; }
    public void setProvince(String province) {this.province = province; }
    public void setNameOfLocation(String nameOfLocation) {this.nameOfLocation = nameOfLocation;}

}
