package com.tesdaciicc.ui;

import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.UserAuthenticationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Registration class that handles user registration operations.
 * Acts as a controller for registration-related functionality.
 */
public class Registration {

    private static final Logger logger = LoggerFactory.getLogger(Registration.class);
    private final UserAuthenticationService authService;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");
    
    /**
     * Constructor with dependency injection
     * 
     * @param authService UserAuthenticationService instance
     */
    public Registration(UserAuthenticationService authService) {
        this.authService = authService;
    }
    
    /**
     * Constructor that creates its own service instance
     */
    public Registration() {
        this.authService = new UserAuthenticationService(new UserAuthenticationDAO());
    }
    
    /**
     * Registers a new user with validation
     * 
     * @param name User's full name
     * @param email User's email address
     * @param number User's phone number (11 digits)
     * @param pin User's 4-digit PIN
     * @return RegistrationResult containing registration status and details
     */
    public RegistrationResult registerUser(String name, String email, String number, String pin) {
        logger.info("Attempting to register user with email: {}", email);
        
        try {
            // Validate input data
            ValidationResult validation = validateUserInput(name, email, number, pin);
            if (!validation.isValid()) {
                logger.warn("Registration validation failed: {}", validation.getMessage());
                return new RegistrationResult(false, validation.getMessage(), null);
            }
            
            // Create user object
            UserAuthentication newUser = new UserAuthentication(
                name.trim(),
                email.trim().toLowerCase(),
                number.trim(),
                pin
            );
            
            // Attempt registration through service
            boolean registered = authService.registerUserWithBalance(newUser);

            if (registered) {
                logger.info("User registered successfully: {}", email);
                return new RegistrationResult(true, "Registration successful! You can now login.", newUser);
            } else {
                // Check specific reasons for failure
                String failureReason = determineRegistrationFailureReason(email, number);
                logger.warn("Registration failed for {}: {}", email, failureReason);
                return new RegistrationResult(false, failureReason, null);
            }
            
        } catch (Exception e) {
            logger.error("Registration error for email: {} - {}", email, e.getMessage());
            return new RegistrationResult(false, "Registration failed due to system error. Please try again.", null);
        }
    }
    
    /**
     * Validates all user input data
     * 
     * @param name User's name
     * @param email User's email
     * @param number User's phone number
     * @param pin User's PIN
     * @return ValidationResult with validation status
     */
    private ValidationResult validateUserInput(String name, String email, String number, String pin) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Name is required");
        }
        
        if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            return new ValidationResult(false, "Name must be 2-50 characters and contain only letters and spaces");
        }
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new ValidationResult(false, "Please enter a valid email address");
        }
        
        // Validate phone number
        if (number == null || number.trim().isEmpty()) {
            return new ValidationResult(false, "Phone number is required");
        }
        
        if (!PHONE_PATTERN.matcher(number.trim()).matches()) {
            return new ValidationResult(false, "Phone number must be exactly 11 digits");
        }
        
        // Validate PIN
        if (pin == null || pin.trim().isEmpty()) {
            return new ValidationResult(false, "PIN is required");
        }
        
        if (!PIN_PATTERN.matcher(pin).matches()) {
            return new ValidationResult(false, "PIN must be exactly 4 digits");
        }
        
        // Check for weak PINs
        if (isWeakPin(pin)) {
            return new ValidationResult(false, "PIN is too weak. Avoid sequences like 1234 or repeated digits like 1111");
        }
        
        return new ValidationResult(true, "Validation successful");
    }
    
    /**
     * Checks if PIN is weak (sequential or repeated digits)
     * 
     * @param pin PIN to check
     * @return true if PIN is weak
     */
    private boolean isWeakPin(String pin) {
        // Check for repeated digits (1111, 2222, etc.)
        if (pin.matches("(.)\\1{3}")) {
            return true;
        }
        
        // Check for sequential ascending (1234, 5678, etc.)
        for (int i = 0; i < pin.length() - 1; i++) {
            int current = Character.getNumericValue(pin.charAt(i));
            int next = Character.getNumericValue(pin.charAt(i + 1));
            if (next != current + 1) {
                break;
            }
            if (i == pin.length() - 2) { // Reached end, all sequential
                return true;
            }
        }
        
        // Check for sequential descending (4321, 8765, etc.)
        for (int i = 0; i < pin.length() - 1; i++) {
            int current = Character.getNumericValue(pin.charAt(i));
            int next = Character.getNumericValue(pin.charAt(i + 1));
            if (next != current - 1) {
                break;
            }
            if (i == pin.length() - 2) { // Reached end, all sequential
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Determines specific reason for registration failure
     * 
     * @param email User's email
     * @param number User's phone number
     * @return Specific failure message
     */
    private String determineRegistrationFailureReason(String email, String number) {
        try {
            // Check if email exists
            if (authService.validateToken("dummy") != null) { // Just to access DAO through service
                UserAuthenticationDAO dao = new UserAuthenticationDAO();
                
                if (dao.findByEmail(email).isPresent()) {
                    return "Email address is already registered. Please use a different email or try logging in.";
                }
                
                if (dao.findByNumber(number).isPresent()) {
                    return "Phone number is already registered. Please use a different phone number or try logging in.";
                }
            }
            
            return "Registration failed. Please check your information and try again.";
            
        } catch (Exception e) {
            logger.error("Error determining registration failure reason: {}", e.getMessage());
            return "Registration failed due to system error. Please try again later.";
        }
    }
    
    /**
     * Checks if email is already registered
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        try {
            UserAuthenticationDAO dao = new UserAuthenticationDAO();
            return dao.findByEmail(email.trim().toLowerCase()).isPresent();
        } catch (Exception e) {
            logger.error("Error checking email registration status: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if phone number is already registered
     * 
     * @param number Phone number to check
     * @return true if number exists, false otherwise
     */
    public boolean isNumberRegistered(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        
        try {
            UserAuthenticationDAO dao = new UserAuthenticationDAO();
            return dao.findByNumber(number.trim()).isPresent();
        } catch (Exception e) {
            logger.error("Error checking phone number registration status: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets total number of registered users
     * 
     * @return User count
     */
    public int getUserCount() {
        try {
            UserAuthenticationDAO dao = new UserAuthenticationDAO();
            return dao.count();
        } catch (Exception e) {
            logger.error("Error getting user count: {}", e.getMessage());
            return -1;
        }
    }
    
    /**
     * Inner class to represent validation results
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Inner class to represent registration operation results
     */
    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        private final UserAuthentication user;
        
        public RegistrationResult(boolean success, String message, UserAuthentication user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public UserAuthentication getUser() {
            return user;
        }
        
        @Override
        public String toString() {
            return "RegistrationResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", hasUser=" + (user != null) +
                    '}';
        }
    }
}
