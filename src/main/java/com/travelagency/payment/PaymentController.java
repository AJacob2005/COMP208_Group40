package com.travelagency.payment;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentValid paymentService = new PaymentValid();

    @PostMapping("/process")
    public PaymentValid.PaymentResult process(@RequestBody Map<String, String> data) throws Exception {
        double amount = data.get("amount") != null ? Double.parseDouble(data.get("amount")) : 0;
        return paymentService.processPayment(
                data.get("paymentMethod"),
                data.getOrDefault("cardName", ""),
                data.getOrDefault("cardNumber", ""),
                data.getOrDefault("expiryDate", ""),
                data.getOrDefault("cvv", ""),
                amount,
                data.getOrDefault("userId", "")
        );
    }

    @GetMapping("/receipt")
    public Map<String, String> receipt(@RequestParam String id) throws Exception {
        return paymentService.getReceipt(id);
    }
}