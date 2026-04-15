package com.travelagency.flights;

import java.sql.*;
import java.util.List;
import java.util.Map;

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

        List<Map<String, Object>> itineraries =
                (List<Map<String, Object>>) data.get("itineraries");

        if (itineraries == null) return;

        int limit = Math.min(50, itineraries.size());

        String sql = """
            INSERT INTO flights VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < limit; i++) {

                Map<String, Object> flight = itineraries.get(i);

                Map<String, Object> priceObj =
                        (Map<String, Object>) flight.get("cheapest_price");

                Double price = priceObj == null ? null :
                        ((Number) priceObj.get("amount")).doubleValue();

                ps.setObject(1, price);

                // Minimal version (can expand later)
                ps.setString(2, null);
                ps.setString(3, null);
                ps.setString(4, null);
                ps.setInt(5, 0);

                ps.setString(6, null);
                ps.setString(7, null);
                ps.setString(8, null);
                ps.setInt(9, 0);

                ps.setString(10, origin);
                ps.setString(11, destination);
                ps.setString(12, cabin);

                ps.executeUpdate();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}