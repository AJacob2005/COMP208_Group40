package com.travelagency.booking;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    static final String DB_URL = "jdbc:sqlite:data/travelBookings.db";
    private final Gson gson = new Gson();

    public BookingController() {
        initDb();
    }

    private void initDb() {
        String createBookings =
            "CREATE TABLE IF NOT EXISTS bookings (" +
            "  booking_id   INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  user_id      TEXT NOT NULL," +
            "  booking_date TEXT DEFAULT (datetime('now'))," +
            "  total_price  REAL NOT NULL," +
            "  status       TEXT DEFAULT 'PENDING'," +
            "  payment_status TEXT DEFAULT 'UNPAID'" +
            ")";

        String createItems =
            "CREATE TABLE IF NOT EXISTS booking_items (" +
            "  item_id            INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  booking_id         INTEGER NOT NULL," +
            "  user_id            TEXT NOT NULL," +
            "  item_type          TEXT NOT NULL," +
            "  airline            TEXT," +
            "  origin             TEXT," +
            "  destination        TEXT," +
            "  departure_date     TEXT," +
            "  return_date        TEXT," +
            "  outbound_departure TEXT," +
            "  outbound_arrival   TEXT," +
            "  outbound_stops     INTEGER," +
            "  return_departure   TEXT," +
            "  return_arrival     TEXT," +
            "  return_stops       INTEGER," +
            "  flight_price       REAL," +
            "  hotel_name         TEXT," +
            "  location           TEXT," +
            "  nightly_rate       REAL," +
            "  accommodation_price REAL," +
            "  selected_at        TEXT DEFAULT (datetime('now'))," +
            "  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE" +
            ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            st.execute(createBookings);
            st.execute(createItems);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping
    public Map<String, Object> createBooking(@RequestBody Map<String, Object> body) {
        String userId = (String) body.getOrDefault("userId", "guest");

        Map<?, ?> flight = body.get("flight") instanceof Map ? (Map<?, ?>) body.get("flight") : null;
        Map<?, ?> hotel  = body.get("hotel")  instanceof Map ? (Map<?, ?>) body.get("hotel")  : null;

        double flightPrice = 0, hotelPrice = 0;
        if (flight != null && flight.get("price") != null)
            flightPrice = Double.parseDouble(flight.get("price").toString());
        if (hotel != null && hotel.get("totalPrice") != null)
            hotelPrice = Double.parseDouble(hotel.get("totalPrice").toString());
        double total = flightPrice + hotelPrice;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            // Insert booking
            long bookingId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO bookings (user_id, total_price, status, payment_status) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, userId);
                ps.setDouble(2, total);
                ps.setString(3, "PENDING");
                ps.setString(4, "UNPAID");
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                bookingId = keys.getLong(1);
            }

            // Insert flight item
            if (flight != null) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO booking_items (booking_id, user_id, item_type, airline, origin, destination, " +
                        "departure_date, return_date, outbound_departure, outbound_arrival, outbound_stops, " +
                        "return_departure, return_arrival, return_stops, flight_price) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    ps.setLong(1, bookingId);
                    ps.setString(2, userId);
                    ps.setString(3, "flight");
                    ps.setString(4, str(flight, "outboundAirline"));
                    ps.setString(5, str(flight, "origin"));
                    ps.setString(6, str(flight, "destination"));
                    ps.setString(7, str(flight, "outboundDeparture"));
                    ps.setString(8, str(flight, "returnArrival"));
                    ps.setString(9, str(flight, "outboundDeparture"));
                    ps.setString(10, str(flight, "outboundArrival"));
                    ps.setObject(11, flight.get("outboundStops"));
                    ps.setString(12, str(flight, "returnDeparture"));
                    ps.setString(13, str(flight, "returnArrival"));
                    ps.setObject(14, flight.get("returnStops"));
                    ps.setDouble(15, flightPrice);
                    ps.executeUpdate();
                }
            }

            // Insert hotel item
            if (hotel != null) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO booking_items (booking_id, user_id, item_type, hotel_name, location, " +
                        "nightly_rate, accommodation_price) VALUES (?,?,?,?,?,?,?)")) {
                    ps.setLong(1, bookingId);
                    ps.setString(2, userId);
                    ps.setString(3, "accommodation");
                    ps.setString(4, str(hotel, "name"));
                    ps.setString(5, str(hotel, "location"));
                    ps.setObject(6, hotel.get("nightlyRate"));
                    ps.setDouble(7, hotelPrice);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return Map.of("success", true, "bookingId", bookingId, "totalPrice", total);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm")
    public Map<String, Object> confirmBooking(@PathVariable long id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE bookings SET status='CONFIRMED', payment_status='PAID' WHERE booking_id=?")) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) return Map.of("success", true);
            return Map.of("success", false, "message", "Booking not found");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", e.getMessage());
        }
    }

    private String str(Map<?, ?> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}
