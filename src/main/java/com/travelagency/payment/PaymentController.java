package com.travelagency.payment;

import com.travelagency.booking.BookingController;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentValid paymentService = new PaymentValid();

    @PostMapping("/process")
    public PaymentValid.PaymentResult process(@RequestBody Map<String, String> data) throws Exception {
        double amount = data.get("amount") != null ? Double.parseDouble(data.get("amount")) : 0;
        PaymentValid.PaymentResult result = paymentService.processPayment(
                data.get("paymentMethod"),
                data.getOrDefault("cardName", ""),
                data.getOrDefault("cardNumber", ""),
                data.getOrDefault("expiryDate", ""),
                data.getOrDefault("cvv", ""),
                amount,
                data.getOrDefault("userId", "")
        );
        if (result.success && data.get("bookingId") != null) {
            try {
                long bookingId = Long.parseLong(data.get("bookingId"));
                confirmBookingInDb(bookingId);
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }

    private void confirmBookingInDb(long bookingId) {
        try (Connection conn = DriverManager.getConnection(BookingController.DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE bookings SET status='CONFIRMED', payment_status='PAID' WHERE booking_id=?")) {
            ps.setLong(1, bookingId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/receipt")
    public Map<String, String> receipt(@RequestParam String id) throws Exception {
        return paymentService.getReceipt(id);
    }
}