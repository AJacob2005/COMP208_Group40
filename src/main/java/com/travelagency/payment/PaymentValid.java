package com.travelagency.payment;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentValid {
    
    private Map<String, TransactionRecord> transactionDatabase = new HashMap<>();
    private static final String CARD_CLEANER = "[\\s-]";
    
    public enum PaymentType {
        CARD("card"), PAYPAL("paypal"), BANK("bank");
        
        private final String value;
        

        PaymentType(String value) {
            this.value = value;
        }
        
        public String getValue() { return value; }
        
        public static PaymentType fromValue(String value) {
            for (PaymentType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return CARD;
        }
    }
    
    public class TransactionRecord {
        private String transactionId;
        private String userId;
        private double amount;
        private String currency;
        private PaymentType paymentType;
        private String maskedCardNumber;
        private String cardType;
        private LocalDateTime transactionTime;
        private String status;
            private String authCode;
        
        public TransactionRecord(String transactionId, double amount, PaymentType paymentType) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.paymentType = paymentType;
            
            this.currency = "USD";
            this.transactionTime = LocalDateTime.now();
            this.status = "PENDING";
            this.userId = "";
        }
        
        public String getTransactionId() { return transactionId; }
        public double getAmount() { return amount; }
        public String getFormattedAmount() { 
                return String.format("%.2f %s", amount, currency);
        }

        public String getPaymentType() { return paymentType.value; }
        public String getMaskedCardNumber() { return maskedCardNumber; }
        public void setMaskedCardNumber(String masked) { this.maskedCardNumber = masked; }
        public String getCardType() { return cardType; }
        
        public void setCardType(String type) { this.cardType = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAuthCode() { return authCode; }
        public void setAuthCode(String code) { this.authCode = code; }
        public String getFormattedTime() {
                return transactionTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }


        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public class PaymentResult {
        private boolean success;
        private String message;
        private String transactionId;

        private String authCode;
        private double amount;
        private String paymentMethod;
        private String maskedCard;
        
        private String cardBrand;
        private String timestamp;
        

        public PaymentResult(boolean success, String message) {
            this.success = success;
            this.message = message;

            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }


        public void setMessage(String message) { this.message = message; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String id) { this.transactionId = id; }
        public String getAuthCode() { return authCode; }
        public void setAuthCode(String code) { this.authCode = code; }
        public double getAmount() { return amount; }
        public void setAmount(double amt) { this.amount = amt; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String method) { this.paymentMethod = method; }
        public String getMaskedCard() { return maskedCard; }
        public void setMaskedCard(String card) { this.maskedCard = card; }

        public String getCardBrand() { return cardBrand; }
        public void setCardBrand(String brand) { this.cardBrand = brand; }
        public String getTimestamp() { return timestamp; }
    }
    
    public class CardValidation {

        private boolean valid;
        
        private String message;
        private String cardType;
        private String maskedNumber;
        
        public CardValidation(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }

        public String getMessage() { return message; }
        public String getCardType() { return cardType; }
        public void setCardType(String type) { this.cardType = type; }

        public String getMaskedNumber() { return maskedNumber; }
        public void setMaskedNumber(String masked) { this.maskedNumber = masked; }
    }
    
    public PaymentResult processPayment(String paymentMethod, String cardName, String cardNumber, String expiryDate, String cvv, double amount, String userId) {
        
        PaymentType type = PaymentType.fromValue(paymentMethod);
        
        if (type == PaymentType.CARD) {

            return processCardPayment(cardName, cardNumber, expiryDate, cvv, amount, userId);
        } else if (type == PaymentType.PAYPAL) {
            return processPaypalPayment(amount, userId);

        } else if (type == PaymentType.BANK) {
            return processBankPayment(amount, userId);
            
        }
        
        return new PaymentResult(false, "Invalid payment method");
    }
    
    private PaymentResult processCardPayment(String cardName, String cardNumber, String expiryDate, String cvv, double amount, String userId) {

        CardValidation validation = validateCard(cardName, cardNumber, expiryDate, cvv);
        if (!validation.isValid()) {
            return new PaymentResult(false, validation.getMessage());
            }
        if (amount <= 0) {
            return new PaymentResult(false, "Invalid amount");
            }
        

        if (amount > 10000) {
            return new PaymentResult(false, "Amount exceeds $10,000 limit");
            }
        String transactionId = "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4);
        
        String authCode = "AUTH" + String.format("%06d", (int)(Math.random() * 1000000));
        
        TransactionRecord record = new TransactionRecord(transactionId, amount, PaymentType.CARD);
        
        record.setMaskedCardNumber(validation.getMaskedNumber());
        record.setCardType(validation.getCardType());
        record.setAuthCode(authCode);
        record.setStatus("COMPLETED");
        record.setUserId(userId);
        
        transactionDatabase.put(transactionId, record);
        
        PaymentResult result = new PaymentResult(true, "Payment successful");
        result.setTransactionId(transactionId);
        
        result.setAuthCode(authCode);
        result.setAmount(amount);
        result.setPaymentMethod("Credit/Debit Card");
        
        result.setMaskedCard(validation.getMaskedNumber());
        result.setCardBrand(validation.getCardType());
        
        return result;
    }
    private CardValidation validateCard(String cardName, String cardNumber, 
                                         String expiryDate, String cvv) {
        if (cardName == null || cardName.trim().length() < 2) {
            return new CardValidation(false, "Enter cardholder name");
           }
        
        String cleanNumber = "";
        if (cardNumber != null) {
            cleanNumber = cardNumber.replaceAll(CARD_CLEANER, "");
        }
        

        if (cleanNumber.isEmpty()) {
            return new CardValidation(false, "Enter card number");
        }
        if (cleanNumber.length() < 15 || cleanNumber.length() > 16) {
            return new CardValidation(false, "Card number must be 15 or 16 digits");
        }
        if (!cleanNumber.matches("\\d+")) {
            return new CardValidation(false, "Card number must contain only digits");
        }
        

        String cardType = detectCardType(cleanNumber);
        
        if (cvv == null || cvv.trim().isEmpty()) {
            return new CardValidation(false, "Enter CVV");
        }
        
        String cleanCvv = cvv.trim();
        int expectedLength = cardType.equals("American Express") ? 4 : 3;
        
        if (cleanCvv.length() != expectedLength || !cleanCvv.matches("\\d+")) {
            return new CardValidation(false, "CVV must be " + expectedLength + " digits");
            }
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return new CardValidation(false, "Enter expiry date");
            }
        

        try {
            String[] parts = expiryDate.split("-");
            if (parts.length != 2) {
                return new CardValidation(false, "Invalid expiry date format");
            }
            
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            
            YearMonth expiry = YearMonth.of(year, month);
            YearMonth now = YearMonth.now();
            
            if (expiry.isBefore(now)) {
                return new CardValidation(false, "Card has expired");
            }
            
            if (expiry.isAfter(now.plusYears(10))) {
                return new CardValidation(false, "Expiry date too far in future");
            }
            
        } catch (NumberFormatException e) {
            return new CardValidation(false, "Invalid expiry date");
            }
        
        if (!luhnCheck(cleanNumber)) {
            return new CardValidation(false, "Invalid card number");
            }
        
        CardValidation result = new CardValidation(true, "Card valid");
        
        result.setCardType(cardType);
        result.setMaskedNumber(maskCard(cleanNumber));
        
        return result;
    }
    
    
    private String detectCardType(String cardNumber) {
    
        if (cardNumber.startsWith("4")) {
            return "Visa";
        } else if (cardNumber.startsWith("5")) {
            return "Mastercard";
        } else if (cardNumber.startsWith("3")) {

            return "American Express";
        } else if (cardNumber.startsWith("6")) {
            return "Discover";
        }
    
        return "Unknown";
    
    }
    private String maskCard(String cardNumber) {
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        
        return "•••• •••• •••• " + lastFour;
    }
    
    private boolean luhnCheck(String cardNumber) {
        
        int sum = 0;
        boolean alternate = false;


        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = digit - 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
    
    private PaymentResult processPaypalPayment(double amount, String userId) {
        
        String transactionId = "PP" + System.currentTimeMillis();
        String authCode = "PYPL" + String.format("%08d", (int)(Math.random() * 100000000));
        
        TransactionRecord record = new TransactionRecord(transactionId, amount, PaymentType.PAYPAL);
        
        record.setAuthCode(authCode);
        record.setStatus("COMPLETED");
        record.setUserId(userId);
        
        transactionDatabase.put(transactionId, record);
        
        PaymentResult result = new PaymentResult(true, "PayPal payment processed");
        result.setTransactionId(transactionId);
        result.setAuthCode(authCode);
        result.setAmount(amount);
        
        result.setPaymentMethod("PayPal");
        
        return result;
    }
    
    private PaymentResult processBankPayment(double amount, String userId) {
        
        String transactionId = "BNK" + System.currentTimeMillis();
        
        String reference = "REF" + String.format("%09d", (int)(Math.random() * 1000000000));
        
        
        TransactionRecord record = new TransactionRecord(transactionId, amount, PaymentType.BANK);
        record.setAuthCode(reference);
        record.setStatus("PENDING");
        record.setUserId(userId);
        
        transactionDatabase.put(transactionId, record);
        PaymentResult result = new PaymentResult(true, "Bank transfer initiated");
        
        result.setTransactionId(transactionId);
        result.setAuthCode(reference);
        result.setAmount(amount);
        
        result.setPaymentMethod("Bank Transfer");
        result.setMessage("Use reference: " + reference);
        
        return result;
    }
    


    public TransactionRecord getTransaction(String transactionId) {
        return transactionDatabase.get(transactionId);
    }
    
    public Map<String, String> getReceipt(String transactionId) {
        TransactionRecord record = transactionDatabase.get(transactionId);
        
        Map<String, String> receipt = new HashMap<>();
        if (record != null) {

            receipt.put("id", record.getTransactionId());
            receipt.put("amount", record.getFormattedAmount());
            receipt.put("date", record.getFormattedTime());
            
            receipt.put("status", record.getStatus());
            receipt.put("authCode", record.getAuthCode());

            receipt.put("method", record.getPaymentType());
            
            if (record.getMaskedCardNumber() != null) {
                receipt.put("card", record.getMaskedCardNumber());
                receipt.put("cardType", record.getCardType());
            }
        }
        
        return receipt;
    }


    //test code main
    public static void main(String[] args) {
    System.out.println("=== PAYMENT TEST ===\n");
    
    PaymentValid payment = new PaymentValid();
    String userId = "test-user-123";
    
    System.out.println("Testing card payment...");
    PaymentResult result = payment.processPayment(
        "card",         
        "John Smith",        
        "4111111111111111",
        "2028-12",      
        "123",                 
        299.99,             
        userId                      
    );
    
    System.out.println("Success: " + result.isSuccess());
    System.out.println("Message: " + result.getMessage());
    
    if (result.isSuccess()) {
        System.out.println("Transaction ID: " + result.getTransactionId());
        System.out.println("Amount: $" + result.getAmount());

        System.out.println("Card: " + result.getMaskedCard());
        System.out.println("Card Brand: " + result.getCardBrand());
        
        Map<String, String> receipt = payment.getReceipt(result.getTransactionId());
        System.out.println("\nReceipt: " + receipt);
    } else {
        System.out.println("Error: " + result.getMessage());
    }
    
    System.out.println("\n=== TEST COMPLETE ===");
    }
}