package com.travelagency.flights;

import java.util.Map;

public class FlightUpdater {

    private static final String API_KEY = "69df93455e1a26f2fe76fedc";

    public static boolean updateFlights(
        String departure, String arrival, String departDate, String returnDate,
        String adults, String children, String infants, String cabin, String currency
    ) {
        try {
            FlightAPIClient api = new FlightAPIClient(API_KEY);
            FlightRepository db = new FlightRepository();

            Map<String, Object> data = api.fetchFlights(
                departure, arrival, departDate, returnDate,
                adults, children, infants, cabin, currency
            );

            if (data == null) {
                System.out.println("No data received from API");
                return false;
            }

            db.saveFlights(data, departure, arrival, cabin);

            System.out.println("Flights saved successfully");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        boolean result = updateFlights(
            "LHR", "JFK",
            "2026-04-16", "2026-04-30",
            "1", "0", "0",
            "Economy", "GBP"
        );

        System.out.println("Finished: " + result);
    }
}