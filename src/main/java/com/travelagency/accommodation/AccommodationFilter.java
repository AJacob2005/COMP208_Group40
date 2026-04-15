package com.travelagency.accommodation;

import java.time.LocalDate;

public class AccommodationFilter {

    private String location;
    private double minRating;
    private LocalDate checkIn;
    private LocalDate checkOut;

    public AccommodationFilter() {}

    public String getLocation() {
        return location;
    }

    public double getMinRating() {
        return minRating;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMinRating(double minRating) {
        this.minRating = minRating;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}