package com.travelagency.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class AccountValid {
    private Map<String, UserAccount> userDatabase = new HashMap<>();
    
    private Map<String, LoginAttempt> loginAttempts = new HashMap<>();
    
    private Map<String, UserSession> activeSessions = new HashMap<>();
    
    
    private static final String EMAIL_CHECK = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PASSWORD_CHECK = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
    private static final String NAME_CHECK = "^[A-Za-z ]{2,50}$";
    
    private Pattern emailPattern = Pattern.compile(EMAIL_CHECK);
    
    private Pattern passwordPattern = Pattern.compile(PASSWORD_CHECK);
    private Pattern namePattern = Pattern.compile(NAME_CHECK);
    
    public class UserAccount {

        private String userId;
        private String email;
        private String passwordHash;
        private String fullName;
        private String phoneNumber;

        private boolean emailVerified;
        
        private LocalDateTime registeredDate;
        
        private List<SavedTrip> savedTrips;
        
        public UserAccount(String email, String passwordHash, String fullName) {


            this.userId = UUID.randomUUID().toString();
            this.email = email;
            this.passwordHash = passwordHash;
            this.fullName = fullName;
            this.emailVerified = false;
            this.registeredDate = LocalDateTime.now();
            this.savedTrips = new ArrayList<>();

        }
        
        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        
        public String getFullName() { return fullName; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public boolean isEmailVerified() { return emailVerified; }
        public void setEmailVerified(boolean verified) { this.emailVerified = verified; }
        
        public List<SavedTrip> getSavedTrips() { return savedTrips; }
        
        public void addSavedTrip(SavedTrip trip) { savedTrips.add(trip); }
        
        @Override
        public String toString() {
            
            return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailVerified=" + emailVerified +
                ", registeredDate=" + registeredDate +
                ", savedTrips=" + savedTrips + 
                    '}';
        }
    }
    public class LoginAttempt {

        private int attemptCount;
        private LocalDateTime firstAttempt;
        private boolean locked;
        
        public LoginAttempt() {
            this.attemptCount = 1;
            this.firstAttempt = LocalDateTime.now();
            this.locked = false;
        }
        
        public void increment() { attemptCount++; }

        public int getCount() { return attemptCount; }
        public boolean isLocked() { return locked; }
        
        public void setLocked(boolean locked) { this.locked = locked; }
        public LocalDateTime getFirstAttempt() { return firstAttempt; }
    }
    
    public class UserSession {
        private String sessionToken;
        private String userId;
        private LocalDateTime expiryTime;
public UserSession(String userId) {
            this.sessionToken = UUID.randomUUID().toString();
            this.userId = userId;
            this.expiryTime = LocalDateTime.now().plusHours(2);
        }
        
        public String getSessionToken() { return sessionToken; }
        public String getUserId() { return userId; }
        public boolean isValid() { return LocalDateTime.now().isBefore(expiryTime); }
    }
    
    public class SavedTrip {
        private String tripId;

        private String tripName;
        private String destination;
        
        private double totalPrice;
        
        private LocalDateTime savedDate;
        public SavedTrip(String tripName, String destination, double totalPrice) {
            this.tripId = UUID.randomUUID().toString();
            this.tripName = tripName;
            this.destination = destination;

            this.totalPrice = totalPrice;
            this.savedDate = LocalDateTime.now();
        }
        
        public String getTripId() { return tripId; }
        public String getTripName() { return tripName; }
        public String getDestination() { return destination; }
        public double getTotalPrice() { return totalPrice; }
        public String getFormattedDate() 
        { 

            
            return savedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        
        @Override
        public String toString() {
            return "SavedTrip{" +
                "tripName='" + tripName + '\'' +
                ", destination='" + destination + '\'' +
                ", totalPrice=" + totalPrice +
                ", savedDate=" + savedDate +
                '}';

        }
    }
    
    public class RegistrationResult {

        private boolean success;
        private String message;
        private String userId;

        private String verificationToken;
        
        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserId() { return userId; }

        public void setUserId(String userId) { this.userId = userId; }
        public String getVerificationToken() { return verificationToken; }

        public void setVerificationToken(String token) { this.verificationToken = token; }
    }
    
    public class LoginResult {
        private boolean success;
        private String message;

        private String sessionToken;

        private String fullName;
        private String userId;
        private List<SavedTrip> savedTrips;
        
        public LoginResult(boolean success, String message) {
            this.success = success;

            this.message = message;
            this.savedTrips = new ArrayList<>();
        }
        
        public boolean isSuccess() { return success; }

        public String getMessage() { return message; }

        public String getSessionToken() { return sessionToken; }
        public void setSessionToken(String token) { this.sessionToken = token; }
        public String getFullName() { return fullName; }
        public void setFullName(String name) { this.fullName = name; }
        public String getUserId() { return userId; }

        public void setUserId(String id) { this.userId = id; }
        
        public List<SavedTrip> getSavedTrips() { return savedTrips; }
        public void setSavedTrips(List<SavedTrip> trips) { this.savedTrips = trips; }
    }
    
    public RegistrationResult registerUser(String email, String password, String fullName, String phoneNumber) {
        if (email == null || email.trim().isEmpty()) {
            return new RegistrationResult(false, "Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            return new RegistrationResult(false, "Password is required");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return new RegistrationResult(false, "Full name is required");
        }
        
        email = email.trim().toLowerCase();
        fullName = fullName.trim();
        
        if (!emailPattern.matcher(email).matches()) {
            return new RegistrationResult(false, "Invalid email format");
        }
        
        if (userDatabase.containsKey(email)) {
            return new RegistrationResult(false, "Email already registered");
        }
        
        if (!passwordPattern.matcher(password).matches()) {
            return new RegistrationResult(false, "Password must be 8-20 characters with at least one number, one lowercase, and one uppercase letter");
        }
        
        if (!namePattern.matcher(fullName).matches()) {
            return new RegistrationResult(false, "Name should only contain letters and spaces");
        }
        
        String passwordHash = hashPassword(password);
        UserAccount newUser = new UserAccount(email, passwordHash, fullName);
        
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            newUser.setPhoneNumber(phoneNumber.trim());
        }
        
        userDatabase.put(email, newUser);
        
        String verificationToken = UUID.randomUUID().toString();
                newUser.setEmailVerified(true); // For testing, auto-verify
                RegistrationResult result = new RegistrationResult(true, "Registration successful");
        result.setUserId(newUser.userId);
        result.setVerificationToken(verificationToken);
        
        return result;
    }
    
    public LoginResult loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return new LoginResult(false, "Email is required");
        }
        if (password == null || password.trim().isEmpty()) {

            return new LoginResult(false, "Password is required");

        }
        
        email = email.trim().toLowerCase();
        if (loginAttempts.containsKey(email)) {

            LoginAttempt attempt = loginAttempts.get(email);
            if (attempt.isLocked()) {
                return new LoginResult(false, "Account locked. Try again later");

            }
            if (attempt.getCount() >= 5) {
                if (attempt.getFirstAttempt().plusMinutes(15).isAfter(LocalDateTime.now())) {
                    attempt.setLocked(true);

                    return new LoginResult(false, "Too many failed attempts. Account locked for 15 minutes");
                } else {
                    loginAttempts.remove(email);
                }
            }

        }
        UserAccount user = userDatabase.get(email);
        if (user == null) {
            recordFailedAttempt(email);

            return new LoginResult(false, "Invalid email or password");
        }
        
        String passwordHash = hashPassword(password);        
        if (!passwordHash.equals(user.passwordHash)) {
            recordFailedAttempt(email);
            return new LoginResult(false, "Invalid email or password");
        }        
        if (!user.isEmailVerified()) {
            return new LoginResult(false, "Please verify your email before logging in");
        }
        
        loginAttempts.remove(email);
        
        UserSession session = new UserSession(user.userId);
        activeSessions.put(session.getSessionToken(), session);
        
        LoginResult result = new LoginResult(true, "Login successful");
        result.setSessionToken(session.getSessionToken());

        result.setFullName(user.getFullName());

        
        result.setUserId(user.getUserId());
        
        result.setSavedTrips(user.getSavedTrips());
        
        return result;
    }
    
    private void recordFailedAttempt(String email) {
        if (loginAttempts.containsKey(email)) {

            LoginAttempt attempt = loginAttempts.get(email);
            attempt.increment();

        } else {
            loginAttempts.put(email, new LoginAttempt());
        }
    }
    
    private String hashPassword(String password) {
        int hash = 7;
        for (int i = 0; i < password.length(); i++) {
            hash = hash * 31 + password.charAt(i);
        }
        return Integer.toHexString(hash);
    }
    
    public boolean saveTrip(String userId, String tripName, String destination, double totalPrice) {
        for (UserAccount user : userDatabase.values()) {
            if (user.getUserId().equals(userId)) {
                SavedTrip trip = new SavedTrip(tripName, destination, totalPrice);
                user.addSavedTrip(trip);
                return true;
            }
        }
        return false;
    }
    
    public boolean verifySession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }
        UserSession session = activeSessions.get(sessionToken);
        return session != null && session.isValid();
    }
    
    public void logout(String sessionToken) {
        if (sessionToken != null) {
            activeSessions.remove(sessionToken);
        }
    }
    
    public UserAccount getUserById(String userId) {
        for (UserAccount user : userDatabase.values()) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    
    //test code main
    public static void main(String[] args) {
        AccountValid app = new AccountValid();
        
        RegistrationResult reg = app.registerUser("test@email.com", "Password123", "John Doe", "555-1234");
        System.out.println("Registration: " + reg.getMessage());
        
        if (reg.isSuccess()) {
            LoginResult login = app.loginUser("test@email.com", "Password123");
            System.out.println("Login: " + login.getMessage());
            
            if (login.isSuccess()) {
                System.out.println("User: " + login.getFullName());
                System.out.println("Session token: " + login.getSessionToken());
            }
        }
    }
}