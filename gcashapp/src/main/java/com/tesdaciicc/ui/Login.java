package com.tesdaciicc.ui;

import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.UserAuthenticationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Login class that handles user authentication operations.
 * Acts as a controller/facade for login-related functionality.
 */

public class Login {

    private static final Logger logger = LoggerFactory.getLogger(Login.class);
    private final UserAuthenticationService authService;
    private UserAuthentication currentUser;

    /**
     * Constructor with dependency injection
     * 
     * @param authService UserAuthenticationService instance
     */
    public Login(UserAuthenticationService authService) {
        this.authService = authService;
        this.currentUser = null;
    }
    
    /**
     * Constructor that creates its own service instance
     */
    public Login() {
        this.authService = new UserAuthenticationService(new UserAuthenticationDAO());
        this.currentUser = null;
    }
    
    /**
     * Authenticates user with email/phone and PIN
     * 
     * @param emailOrNumber User's email or phone number
     * @param pin User's PIN
     * @return LoginResult containing authentication status and user data
     */
    public LoginResult authenticate(String emailOrNumber, String pin) {
        logger.info("Attempting authentication for: {}", emailOrNumber);
        
        try {
            // Input validation
            if (emailOrNumber == null || emailOrNumber.trim().isEmpty()) {
                logger.warn("Empty email/number provided for authentication");
                return new LoginResult(false, "Email or phone number is required", null);
            }
            
            if (pin == null || pin.trim().isEmpty()) {
                logger.warn("Empty PIN provided for authentication");
                return new LoginResult(false, "PIN is required", null);
            }
            
            // Attempt authentication through service
            UserAuthentication authenticatedUser = authService.loginUser(emailOrNumber.trim(), pin);
            
            if (authenticatedUser != null) {
                this.currentUser = authenticatedUser;
                logger.info("Authentication successful for user: {}", authenticatedUser.getId());
                return new LoginResult(true, "Login successful", authenticatedUser);
            } else {
                logger.warn("Authentication failed for: {}", emailOrNumber);
                return new LoginResult(false, "Invalid email/phone number or PIN", null);
            }
            
        } catch (Exception e) {
            logger.error("Authentication error for: {} - {}", emailOrNumber, e.getMessage());
            return new LoginResult(false, "Authentication failed due to system error", null);
        }
    }
    
    /**
     * Validates an existing authentication token
     * 
     * @param token Authentication token to validate
     * @return LoginResult containing validation status and user data
     */
    public LoginResult validateSession(String token) {
        logger.debug("Validating session token");
        
        try {
            if (token == null || token.trim().isEmpty()) {
                logger.warn("Empty token provided for session validation");
                return new LoginResult(false, "Authentication token is required", null);
            }
            
            UserAuthentication validUser = authService.validateToken(token.trim());
            
            if (validUser != null) {
                this.currentUser = validUser;
                logger.debug("Session validated for user: {}", validUser.getId());
                return new LoginResult(true, "Session valid", validUser);
            } else {
                logger.warn("Invalid or expired session token");
                return new LoginResult(false, "Session expired or invalid", null);
            }
            
        } catch (Exception e) {
            logger.error("Session validation error: {}", e.getMessage());
            return new LoginResult(false, "Session validation failed", null);
        }
    }
    
    /**
     * Logs out the current user by invalidating their token
     * 
     * @return true if logout successful, false otherwise
     */
    public boolean logout() {
        if (currentUser == null || !currentUser.isAuthenticated()) {
            logger.warn("No authenticated user to logout");
            return false;
        }
        
        try {
            String token = currentUser.getToken();
            boolean loggedOut = authService.logout(token);
            
            if (loggedOut) {
                logger.info("User logged out successfully: {}", currentUser.getId());
                this.currentUser = null;
                return true;
            } else {
                logger.warn("Logout failed for user: {}", currentUser.getId());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Logs out user by token (useful for external logout operations)
     * 
     * @param token Authentication token to invalidate
     * @return true if logout successful, false otherwise
     */
    public boolean logout(String token) {
        try {
            boolean loggedOut = authService.logout(token);
            
            if (loggedOut) {
                // Clear current user if it matches the token being logged out
                if (currentUser != null && token.equals(currentUser.getToken())) {
                    this.currentUser = null;
                }
                logger.info("Token invalidated successfully");
                return true;
            } else {
                logger.warn("Failed to invalidate token");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Token logout error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Changes the current user's PIN
     * 
     * @param oldPin Current PIN
     * @param newPin New PIN
     * @return true if PIN change successful, false otherwise
     */
    public boolean changePin(String oldPin, String newPin) {
        if (currentUser == null) {
            logger.warn("No authenticated user for PIN change");
            return false;
        }
        
        try {
            String emailOrNumber = currentUser.getEmail(); // Use email as identifier
            boolean changed = authService.changePin(emailOrNumber, oldPin, newPin);
            
            if (changed) {
                logger.info("PIN changed successfully for user: {}", currentUser.getId());
                // Clear current session for security after PIN change
                this.currentUser = null;
                return true;
            } else {
                logger.warn("PIN change failed for user: {}", currentUser.getId());
                return false;
            }
            
        } catch (Exception e) {
            logger.error("PIN change error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the current authenticated user
     * 
     * @return UserAuthentication object or null if not authenticated
     */
    public UserAuthentication getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently authenticated
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return currentUser != null && currentUser.isAuthenticated();
    }
    
    /**
     * Gets the current user's authentication token
     * 
     * @return Authentication token or null if not authenticated
     */
    public String getAuthenticationToken() {
        return currentUser != null ? currentUser.getToken() : null;
    }
    
    /**
     * Refreshes the current user's session by validating the token
     * 
     * @return true if session refresh successful, false otherwise
     */
    public boolean refreshSession() {
        if (currentUser == null || !currentUser.isAuthenticated()) {
            logger.warn("No authenticated user to refresh session");
            return false;
        }
        
        LoginResult result = validateSession(currentUser.getToken());
        return result.isSuccess();
    }
    
    /**
     * Gets user profile information (without sensitive data)
     * 
     * @return UserAuthentication with profile data or null if not authenticated
     */
    public UserAuthentication getUserProfile() {
        if (currentUser == null) {
            logger.warn("No authenticated user for profile retrieval");
            return null;
        }
        
        try {
            UserAuthentication profile = authService.getUserProfile(currentUser.getId());
            
            if (profile != null) {
                logger.debug("Profile retrieved for user: {}", currentUser.getId());
                return profile;
            } else {
                logger.warn("Failed to retrieve profile for user: {}", currentUser.getId());
                return null;
            }
            
        } catch (Exception e) {
            logger.error("Profile retrieval error: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Inner class to represent login operation results
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final UserAuthentication user;
        
        public LoginResult(boolean success, String message, UserAuthentication user) {
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
        
        public String getAuthenticationToken() {
            return user != null ? user.getToken() : null;
        }
        
        @Override
        public String toString() {
            return "LoginResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", hasUser=" + (user != null) +
                    '}';
        }
    }
}