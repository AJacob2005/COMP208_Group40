package com.travelagency.flights;

import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FlightsController {

    @PostMapping("/flights")
    public List<Map<String, Object>> searchFlights(@RequestBody Map<String, String> data) throws Exception {
        String origin      = data.getOrDefault("origin", "LHR");
        String destination = data.getOrDefault("destination", "JFK");
        String depDate     = data.getOrDefault("departureDate", "2026-04-20");
        String retDate     = data.getOrDefault("returnDate", "2026-04-30");

        // API calls temporarily disabled to preserve free tier credits.
        // To re-enable: remove the return below and uncomment the block beneath it.
        return getBackupFlights(origin, destination, depDate, retDate);

        /*
        String adults   = data.getOrDefault("adults", "1");
        String children = data.getOrDefault("children", "0");
        String infants  = data.getOrDefault("infants", "0");
        String cabin    = data.getOrDefault("cabin", "Economy").replace(" ", "_");
        String currency = data.getOrDefault("currency", "GBP");

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:flights.db");
             PreparedStatement del = conn.prepareStatement(
                "DELETE FROM flights WHERE origin = ? AND destination = ? " +
                "AND substr(outbound_departure,1,10) = ? AND substr(return_departure,1,10) = ?")) {
            del.setString(1, origin); del.setString(2, destination);
            del.setString(3, depDate); del.setString(4, retDate);
            del.executeUpdate();
        } catch (Exception ignored) {}

        FlightUpdater.updateFlights(origin, destination, depDate, retDate, adults, children, infants, cabin, currency);

        List<Map<String, Object>> flights = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:flights.db");
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM flights WHERE origin=? AND destination=? " +
                "AND substr(outbound_departure,1,10)=? AND substr(return_departure,1,10)=?")) {
            stmt.setString(1, origin); stmt.setString(2, destination);
            stmt.setString(3, depDate); stmt.setString(4, retDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> f = new HashMap<>();
                f.put("price",            rs.getDouble("price"));
                f.put("outboundAirline",  rs.getString("outbound_airline"));
                f.put("outboundDeparture",rs.getString("outbound_departure"));
                f.put("outboundArrival",  rs.getString("outbound_arrival"));
                f.put("outboundStops",    rs.getInt("outbound_stops"));
                f.put("returnAirline",    rs.getString("return_airline"));
                f.put("returnDeparture",  rs.getString("return_departure"));
                f.put("returnArrival",    rs.getString("return_arrival"));
                f.put("returnStops",      rs.getInt("return_stops"));
                f.put("origin",           origin);
                f.put("destination",      destination);
                flights.add(f);
            }
        }
        return flights.isEmpty() ? getBackupFlights(origin, destination, depDate, retDate) : flights;
        */
    }

    private List<Map<String, Object>> getBackupFlights(
            String origin, String destination, String depDate, String retDate) {
        List<Map<String, Object>> fallback = new ArrayList<>();
        Object[][] samples = {
            { 342.00, "British Airways", depDate+"T08:00:00", depDate+"T11:30:00", 0,
                      "British Airways", retDate+"T13:00:00", retDate+"T16:30:00", 0 },
            { 289.00, "Ryanair",         depDate+"T06:15:00", depDate+"T09:45:00", 0,
                      "Ryanair",         retDate+"T18:00:00", retDate+"T21:30:00", 0 },
            { 415.00, "Lufthansa",       depDate+"T10:30:00", depDate+"T15:00:00", 1,
                      "Lufthansa",       retDate+"T09:15:00", retDate+"T13:45:00", 1 },
            { 198.00, "easyJet",         depDate+"T07:00:00", depDate+"T10:20:00", 0,
                      "easyJet",         retDate+"T20:00:00", retDate+"T23:20:00", 0 },
            { 520.00, "Emirates",        depDate+"T14:00:00", depDate+"T19:30:00", 1,
                      "Emirates",        retDate+"T22:00:00", retDate+"T03:30:00", 1 },
        };
        for (Object[] s : samples) {
            Map<String, Object> f = new HashMap<>();
            f.put("price",             s[0]);
            f.put("outboundAirline",   s[1]);
            f.put("outboundDeparture", s[2]);
            f.put("outboundArrival",   s[3]);
            f.put("outboundStops",     s[4]);
            f.put("returnAirline",     s[5]);
            f.put("returnDeparture",   s[6]);
            f.put("returnArrival",     s[7]);
            f.put("returnStops",       s[8]);
            f.put("origin",            origin);
            f.put("destination",       destination);
            fallback.add(f);
        }
        return fallback;
    }
}
