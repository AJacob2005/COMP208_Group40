package com.travelagency.accommodation;

public class Accommodation {

    private String name;
    private String location;
    private double rating;
    private String hotelKey;

    public Accommodation() {}

    public Accommodation(String name, String location, double rating, String hotelKey) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.hotelKey = hotelKey;
    }

    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }

    public double getRating() {
        return rating;
    }

    public String getHotelKey() {
        return hotelKey;
    }
}