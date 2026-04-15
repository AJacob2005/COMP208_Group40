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
        String depDate     = data.getOrDefault("departureDate", "2026-04-16");
        String retDate     = data.getOrDefault("returnDate", "2026-04-30");
        String adults      = data.getOrDefault("adults", "1");
        String children    = data.getOrDefault("children", "0");
        String infants     = data.getOrDefault("infants", "0");
        String cabin       = data.getOrDefault("cabin", "Economy");
        String currency    = data.getOrDefault("currency", "GBP");

        FlightUpdater.updateFlights(origin, destination, depDate, retDate, adults, children, infants, cabin, currency);

        List<Map<String, Object>> flights = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:flights.db")) {
            String query = "SELECT * FROM flights WHERE origin = ? AND destination = ? " +
                    "AND substr(outbound_departure,1,10) = ? AND substr(return_departure,1,10) = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, origin);
                stmt.setString(2, destination);
                stmt.setString(3, depDate);
                stmt.setString(4, retDate);
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
                    flights.add(f);
                }
            }
        }
        return flights;
    }
}