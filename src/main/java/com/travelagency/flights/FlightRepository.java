package com.travelagency.flights;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightRepository {

    private final String dbUrl = "jdbc:sqlite:flights.db";

    public FlightRepository() {
        initDb();
    }

    private void initDb() {
        String sql = """
            CREATE TABLE IF NOT EXISTS flights (
                price REAL,
                outbound_airline TEXT,
                outbound_departure TEXT,
                outbound_arrival TEXT,
                outbound_stops INTEGER,
                return_airline TEXT,
                return_departure TEXT,
                return_arrival TEXT,
                return_stops INTEGER,
                origin TEXT,
                destination TEXT,
                cabin_class TEXT
            );
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFlights(Map<String, Object> data, String origin, String destination, String cabin) {

        // Build lookup maps from the top-level lists (same as Python's all_legs / all_carriers)
        Map<Object, Map<String, Object>> allLegs = toMap((List<Map<String, Object>>) data.get("legs"));
        Map<Object, Map<String, Object>> allCarriers = toMap((List<Map<String, Object>>) data.get("carriers"));

        List<Map<String, Object>> itineraries = (List<Map<String, Object>>) data.get("itineraries");
        if (itineraries == null) return;

        int limit = Math.min(50, itineraries.size());

        String sql = "INSERT INTO flights VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < limit; i++) {
                Map<String, Object> flight = itineraries.get(i);

                // Price
                Map<String, Object> priceObj = (Map<String, Object>) flight.get("cheapest_price");
                Double price = priceObj == null ? null : ((Number) priceObj.get("amount")).doubleValue();

                // Leg IDs — [0] outbound, [1] return
                List<Object> legIds = (List<Object>) flight.get("leg_ids");
                Map<String, Object> outboundLeg = legIds != null && legIds.size() > 0 ? allLegs.get(legIds.get(0)) : null;
                Map<String, Object> returnLeg   = legIds != null && legIds.size() > 1 ? allLegs.get(legIds.get(1)) : null;

                String[] out = getLegDetails(outboundLeg, allCarriers);
                String[] ret = getLegDetails(returnLeg,   allCarriers);

                ps.setObject(1, price);
                ps.setString(2, out[0]); // outbound_airline
                ps.setString(3, out[1]); // outbound_departure
                ps.setString(4, out[2]); // outbound_arrival
                ps.setInt(5,    Integer.parseInt(out[3])); // outbound_stops
                ps.setString(6, ret[0]); // return_airline
                ps.setString(7, ret[1]); // return_departure
                ps.setString(8, ret[2]); // return_arrival
                ps.setInt(9,    Integer.parseInt(ret[3])); // return_stops
                ps.setString(10, origin);
                ps.setString(11, destination);
                ps.setString(12, cabin);

                ps.executeUpdate();
            }

            System.out.println("Saved " + limit + " flights to DB");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Returns [airline, departure, arrival, stopCount] for a leg, resolving carrier IDs to names. */
    private String[] getLegDetails(Map<String, Object> leg, Map<Object, Map<String, Object>> allCarriers) {
        if (leg == null) return new String[]{"", "", "", "0"};

        List<Object> carrierIds = (List<Object>) leg.get("marketing_carrier_ids");
        String airline = "";
        if (carrierIds != null) {
            airline = carrierIds.stream()
                .map(id -> {
                    Map<String, Object> c = allCarriers.get(id);
                    return c != null ? (String) c.get("name") : String.valueOf(id);
                })
                .collect(Collectors.joining(", "));
        }

        String departure = leg.getOrDefault("departure", "") != null ? (String) leg.get("departure") : "";
        String arrival   = leg.getOrDefault("arrival",   "") != null ? (String) leg.get("arrival")   : "";
        int stops = leg.get("stop_count") instanceof Number ? ((Number) leg.get("stop_count")).intValue() : 0;

        return new String[]{airline, departure, arrival, String.valueOf(stops)};
    }

    /** Converts a list of maps (each having an "id" key) into a map keyed by id. */
    private Map<Object, Map<String, Object>> toMap(List<Map<String, Object>> list) {
        Map<Object, Map<String, Object>> result = new HashMap<>();
        if (list == null) return result;
        for (Map<String, Object> item : list) {
            Object id = item.get("id");
            if (id != null) result.put(id, item);
        }
        return result;
    }
}