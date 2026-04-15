
package com.travelagency.flights;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FlightUpdater {
    public static boolean updateFlights(
        String departure, String arrival, String departDate, String returnDate,
        String adults, String children, String infants, String cabin, String currency
    ) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "python3",
                "-u",
                "src/main/java/com/travelagency/flights/flight_search.py",
                departure, arrival, departDate, returnDate, adults, children, infants, cabin, currency 
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean success = false;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line);
                if (line.equalsIgnoreCase("True")) {
                    success = true;
                }
            }

            process.waitFor();
            return success;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        boolean result = updateFlights("LHR", "JFK", "2026-04-16", "2026-04-30", "1", "0", "0", "Economy", "GBP");
        System.out.println("Python main returned: " + result);
    }
}
