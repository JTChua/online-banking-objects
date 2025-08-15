package com.tesdaciicc.service;

import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.model.UserAuthentication;

import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAuthenticationService {

  private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);
  private final UserAuthenticationDAO dao;

  // Validation patterns
  private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
  private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4}$");

  public UserAuthenticationService(UserAuthenticationDAO dao) {
    this.dao = dao;
  }

  /**
   * Registration method that adds a new user with validation
   * 
   * @param userAuthentication User data to register
   * @return true if registration successful, false otherwise
   */
  public boolean registerUser(UserAuthentication userAuthentication) {
    logger.info("Attempting to register user: {}", userAuthentication.getEmail());

    // Validate user data
    if (!validateUser(userAuthentication)) {
      logger.warn("User validation failed for: {}", userAuthentication.getEmail());
      return false;
    }

    // Check if email already exists
    if (dao.findByEmail(userAuthentication.getEmail()).isPresent()) {
      logger.warn("Email already exists: {}", userAuthentication.getEmail());
      return false;
    }

    // Check if phone number already exists
    if (dao.findByNumber(userAuthentication.getNumber()).isPresent()) {
      logger.warn("Phone number already exists: {}", userAuthentication.getNumber());
      return false;
    }

    try {
      Optional<UserAuthentication> savedUser = dao.save(userAuthentication);
      if (savedUser.isPresent()) {
        logger.info("User registered successfully with ID: {}", savedUser.get().getId());
        return true;
      } else {
        logger.error("Failed to save user: {}", userAuthentication.getEmail());
        return false;
      }
    } catch (Exception e) {
      logger.error("Registration error for user: {} - {}", userAuthentication.getEmail(), e.getMessage());
      return false;
    }
  }

  /**
   * Login method to check registered user and return authentication with token
   * 
   * @param emailOrNumber Email or phone number
   * @param pin           User PIN
   * @return UserAuthentication with token if successful, null if failed
   */
  public UserAuthentication loginUser(String emailOrNumber, String pin) {
    logger.info("Attempting login for: {}", emailOrNumber);

    // Validate input
    if (emailOrNumber == null || emailOrNumber.trim().isEmpty()) {
      logger.warn("Empty email/number provided for login");
      return null;
    }

    if (pin == null || !PIN_PATTERN.matcher(pin).matches()) {
      logger.warn("Invalid PIN format for login: {}", emailOrNumber);
      return null;
    }

    try {
      Optional<UserAuthentication> authenticatedUser = dao.authenticate(emailOrNumber.trim(), pin);

      // if (authenticatedUser.isPresent()) {
      // UserAuthentication user = authenticatedUser.get();
      // logger.info("User logged in successfully: {} with token", user.getId());
      // return user; // Contains the generated token

      if (authenticatedUser.isPresent()) {
        UserAuthentication user = authenticatedUser.get();

        // Verify token exists in database
        Optional<UserAuthentication> dbUser = dao.validateToken(user.getToken());
        if (!dbUser.isPresent()) {
          logger.error("CRITICAL: Token was generated but not saved to DB for user {}", user.getId());
          return null;
        }

        logger.info("User logged in successfully: {} with token", user.getId());
        return user;
      }

      if (authenticatedUser.isPresent()) {
        UserAuthentication user = authenticatedUser.get();
        logger.debug("Login successful - User ID: {}, Token: {}",
            user.getId(), user.getToken());
        return user;
      }

      if (authenticatedUser.isPresent()) {
        UserAuthentication user = authenticatedUser.get();
        if (user.getToken() == null || user.getToken().isEmpty()) {
          logger.error("Login succeeded but token was null for user: {}", user.getId());
          return null;
        }
        logger.info("User logged in successfully: {} with token", user.getId());
        return user;
      } else {
        logger.warn("Authentication failed for: {}", emailOrNumber);
        return null;
      }
    } catch (Exception e) {
      logger.error("Login failed for: {} - {}", emailOrNumber, e.getMessage());
      return null;
    }

  }

  /**
   * Validates authentication token
   * 
   * @param token Authentication token
   * @return UserAuthentication if token is valid, null otherwise
   */
  public UserAuthentication validateToken(String token) {
    logger.debug("Validating token: {}", token);
    if (token == null || token.trim().isEmpty()) {
      logger.warn("Empty token provided for validation");
      return null;
    }

    try {
      Optional<UserAuthentication> validUser = dao.validateToken(token.trim());

      if (validUser.isPresent()) {
        logger.debug("Token validated successfully for user: {}", validUser.get().getId());
        return validUser.get();
      } else {
        logger.warn("Invalid or expired token");
        return null;
      }
    } catch (Exception e) {
      logger.error("Token validation error: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Change PIN method with old PIN verification
   * 
   * @param emailOrNumber Email or phone number
   * @param oldPin        Current PIN
   * @param newPin        New PIN
   * @return true if PIN change successful, false otherwise
   */
  public boolean changePin(String emailOrNumber, String oldPin, String newPin) {
    logger.info("Attempting PIN change for: {}", emailOrNumber);

    // Validate inputs
    if (emailOrNumber == null || emailOrNumber.trim().isEmpty()) {
      logger.warn("Empty email/number provided for PIN change");
      return false;
    }

    if (oldPin == null || !PIN_PATTERN.matcher(oldPin).matches()) {
      logger.warn("Invalid old PIN format for: {}", emailOrNumber);
      return false;
    }

    if (newPin == null || !PIN_PATTERN.matcher(newPin).matches()) {
      logger.warn("Invalid new PIN format for: {}", emailOrNumber);
      return false;
    }

    if (oldPin.equals(newPin)) {
      logger.warn("New PIN same as old PIN for: {}", emailOrNumber);
      return false;
    }

    if (newPin.equals(oldPin) || newPin.matches("(.)\\1{3}")) {
      logger.warn("Avoid using easily guessable sequences like 1111, 1234, or your birthdate: {}", emailOrNumber);
      return false;
    }

    try {
      // First verify old PIN by attempting authentication
      Optional<UserAuthentication> user = dao.authenticate(emailOrNumber.trim(), oldPin);

      if (user.isPresent()) {
        // Old PIN is correct, update to new PIN
        boolean updated = dao.updatePin(user.get().getId(), newPin);

        if (updated) {
          // Logout all sessions for security after PIN change
          dao.logoutAll(user.get().getId());
          logger.info("PIN changed successfully for user: {}", user.get().getId());
          return true;
        } else {
          logger.error("Failed to update PIN in database for user: {}", user.get().getId());
          return false;
        }
      } else {
        logger.warn("Old PIN verification failed for: {}", emailOrNumber);
        return false;
      }
    } catch (Exception e) {
      logger.error("PIN change failed for: {} - {}", emailOrNumber, e.getMessage());
      return false;
    }
  }

  /**
   * Logout method to invalidate token
   * 
   * @param token Authentication token to invalidate
   * @return true if logout successful, false otherwise
   */
  public boolean logout(String token) {
    if (token == null || token.trim().isEmpty()) {
      logger.warn("Empty token provided for logout");
      return false;
    }

    try {
      boolean loggedOut = dao.logout(token.trim());

      if (loggedOut) {
        logger.info("User logged out successfully");
        return true;
      } else {
        logger.warn("Logout failed - token may not exist");
        return false;
      }
    } catch (Exception e) {
      logger.error("Logout error: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Logout all sessions for a user (security feature)
   * 
   * @param userId User ID
   * @return true if logout successful
   */
  public boolean logoutAll(Integer userId) {
    if (userId == null || userId <= 0) {
      logger.warn("Invalid user ID provided for logout all");
      return false;
    }

    try {
      boolean loggedOut = dao.logoutAll(userId);
      logger.info("All sessions logged out for user: {}", userId);
      return loggedOut;
    } catch (Exception e) {
      logger.error("Logout all error for user: {} - {}", userId, e.getMessage());
      return false;
    }
  }

  /**
   * Get user profile by ID
   * 
   * @param userId User ID
   * @return UserAuthentication without sensitive data, null if not found
   */
  public UserAuthentication getUserProfile(int userId) {
    if (userId <= 0) {
      logger.warn("Invalid user ID provided for profile retrieval");
      return null;
    }

    try {
      Optional<UserAuthentication> user = dao.findById(userId);

      if (user.isPresent()) {
        UserAuthentication profile = user.get();
        // Remove sensitive data for profile view
        profile.setPin(null);
        profile.setToken(null);
        return profile;
      } else {
        logger.warn("User not found with ID: {}", userId);
        return null;
      }
    } catch (Exception e) {
      logger.error("Error retrieving user profile: {} - {}", userId, e.getMessage());
      return null;
    }
  }

  /**
   * Update user profile information
   * 
   * @param userAuthentication Updated user data
   * @return true if update successful, false otherwise
   */
  public boolean updateProfile(UserAuthentication userAuthentication) {
    if (userAuthentication == null) {
      logger.warn("Invalid user data provided for profile update");
      return false;
    }

    // Validate updated data
    if (!validateUserForUpdate(userAuthentication)) {
      logger.warn("Profile update validation failed for user: {}", userAuthentication.getId());
      return false;
    }

    try {
      boolean updated = dao.update(userAuthentication);

      if (updated) {
        logger.info("User profile updated successfully: {}", userAuthentication.getId());
        return true;
      } else {
        logger.error("Failed to update user profile: {}", userAuthentication.getId());
        return false;
      }
    } catch (Exception e) {
      logger.error("Profile update error for user: {} - {}", userAuthentication.getId(), e.getMessage());
      return false;
    }
  }

  /**
   * Validates user data for registration
   * 
   * @param userAuthentication User data to validate
   * @return true if valid, false otherwise
   */
  private boolean validateUser(UserAuthentication userAuthentication) {
    if (userAuthentication == null) {
      logger.warn("Null user data provided for validation");
      return false;
    }

    // Validate name
    if (userAuthentication.getName() == null || userAuthentication.getName().trim().isEmpty()) {
      logger.warn("Invalid name provided");
      return false;
    }

    // Validate email
    if (userAuthentication.getEmail() == null ||
        !EMAIL_PATTERN.matcher(userAuthentication.getEmail().trim()).matches()) {
      logger.warn("Invalid email format: {}", userAuthentication.getEmail());
      return false;
    }

    // Validate phone number (11 digits)
    if (userAuthentication.getNumber() == null ||
        !PHONE_PATTERN.matcher(userAuthentication.getNumber().trim()).matches()) {
      logger.warn("Invalid phone number format: {}", userAuthentication.getNumber());
      return false;
    }

    // Validate PIN (4 digits)
    if (userAuthentication.getPin() == null ||
        !PIN_PATTERN.matcher(userAuthentication.getPin()).matches()) {
      logger.warn("Invalid PIN format");
      return false;
    }

    return true;
  }

  /**
   * Validates user data for profile updates (excludes PIN)
   * 
   * @param userAuthentication User data to validate
   * @return true if valid, false otherwise
   */
  private boolean validateUserForUpdate(UserAuthentication userAuthentication) {
    if (userAuthentication == null) {
      return false;
    }

    // Validate name
    if (userAuthentication.getName() == null || userAuthentication.getName().trim().isEmpty()) {
      return false;
    }

    // Validate email
    if (userAuthentication.getEmail() == null ||
        !EMAIL_PATTERN.matcher(userAuthentication.getEmail().trim()).matches()) {
      return false;
    }

    // Validate phone number
    if (userAuthentication.getNumber() == null ||
        !PHONE_PATTERN.matcher(userAuthentication.getNumber().trim()).matches()) {
      return false;
    }

    return true;
  }
}