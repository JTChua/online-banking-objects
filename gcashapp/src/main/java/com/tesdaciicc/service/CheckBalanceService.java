package com.tesdaciicc.service;

import com.tesdaciicc.data.repository.BalanceDAO;
import com.tesdaciicc.model.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;

public class CheckBalanceService {

  private static final Logger logger = LoggerFactory.getLogger(CheckBalanceService.class);
  private final BalanceDAO balanceDAO;

  public CheckBalanceService() {
    this.balanceDAO = new BalanceDAO();
  }

  public CheckBalanceService(BalanceDAO balanceDAO) {
    this.balanceDAO = balanceDAO;
  }

  /**
   * Check balance for a specific user
   * 
   * @param userId The user ID to check balance for
   * @return Optional containing BigDecimal balance if found, empty otherwise
   */
  public Optional<BigDecimal> checkBalance(int userId) {
    logger.debug("Checking balance for userId: {}", userId);

    if (userId <= 0) {
      logger.warn("Invalid userId provided: {}", userId);
      return Optional.empty();
    }

    Optional<Balance> balance = balanceDAO.findByUserId(userId);

    if (balance.isPresent()) {
      BigDecimal amount = balance.get().getAmount();
      logger.info("Balance found for userId {}: {}", userId, amount);
      return Optional.of(amount);
    } else {
      logger.info("No balance found for userId: {}", userId);
      return Optional.empty();
    }
  }

  /**
   * Get formatted balance string for display
   * 
   * @param userId The user ID to check balance for
   * @return Formatted balance string (e.g., "₱1,234.56") or error message
   */
  public String getFormattedBalance(int userId) {
    logger.debug("Getting formatted balance for userId: {}", userId);

    Optional<BigDecimal> balance = checkBalance(userId);

    if (balance.isPresent()) {
      return String.format("₱%,.2f", balance.get());
    } else {
      return "Balance not available";
    }
  }

  /**
   * Check if user has sufficient balance for a transaction
   * 
   * @param userId         The user ID
   * @param requiredAmount The amount needed for transaction
   * @return true if sufficient balance exists, false otherwise
   */
  public boolean hasSufficientBalance(int userId, BigDecimal requiredAmount) {
    logger.debug("Checking if userId {} has sufficient balance for amount: {}", userId, requiredAmount);

    if (requiredAmount == null || requiredAmount.compareTo(BigDecimal.ZERO) < 0) {
      logger.warn("Invalid required amount: {}", requiredAmount);
      return false;
    }

    Optional<BigDecimal> currentBalance = checkBalance(userId);

    if (currentBalance.isPresent()) {
      boolean sufficient = currentBalance.get().compareTo(requiredAmount) >= 0;
      logger.info("User {} has {} balance for required amount {}",
          userId, sufficient ? "sufficient" : "insufficient", requiredAmount);
      return sufficient;
    }

    logger.info("No balance found for userId {}, insufficient for amount: {}", userId, requiredAmount);
    return false;
  }

  /**
   * Check if user has sufficient balance for a transaction (double overload)
   * 
   * @param userId         The user ID
   * @param requiredAmount The amount needed for transaction
   * @return true if sufficient balance exists, false otherwise
   */
  public boolean hasSufficientBalance(int userId, double requiredAmount) {
    return hasSufficientBalance(userId, BigDecimal.valueOf(requiredAmount));
  }

  /**
   * Get balance information for a user
   * 
   * @param userId The user ID
   * @return Optional containing Balance object if found, empty otherwise
   */
  public Optional<Balance> getBalanceInfo(int userId) {
    logger.debug("Getting balance info for userId: {}", userId);

    if (userId <= 0) {
      logger.warn("Invalid userId provided: {}", userId);
      return Optional.empty();
    }

    return balanceDAO.findByUserId(userId);
  }

  /**
   * Initialize balance for a new user (works with your existing schema)
   * 
   * @param userId        The user ID
   * @param initialAmount The initial balance amount (default 0.00 if null)
   * @return true if successful, false otherwise
   */
  public boolean initializeBalance(int userId, BigDecimal initialAmount) {
    logger.debug("Initializing balance for userId {} with amount: {}", userId, initialAmount);

    if (userId <= 0) {
      logger.warn("Invalid userId provided: {}", userId);
      return false;
    }

    // Check if balance already exists
    if (balanceDAO.findByUserId(userId).isPresent()) {
      logger.warn("Balance already exists for userId: {}", userId);
      return false;
    }

    BigDecimal amount = initialAmount != null ? initialAmount : BigDecimal.ZERO;
    Balance newBalance = new Balance(amount, userId);

    boolean success = balanceDAO.create(newBalance);
    if (success) {
      logger.info("Balance initialized successfully for userId: {}", userId);
    } else {
      logger.error("Failed to initialize balance for userId: {}", userId);
    }

    return success;
  }

  /**
   * Initialize balance with default amount (0.00)
   * 
   * @param userId The user ID
   * @return true if successful, false otherwise
   */
  public boolean initializeBalance(int userId) {
    return initializeBalance(userId, BigDecimal.ZERO);
  }

  /**
   * Initialize balance with double value (convenience method)
   * 
   * @param userId        The user ID
   * @param initialAmount The initial balance amount
   * @return true if successful, false otherwise
   */
  public boolean initializeBalance(int userId, double initialAmount) {
    return initializeBalance(userId, BigDecimal.valueOf(initialAmount));
  }

  /**
   * Update user balance
   * 
   * @param userId    The user ID
   * @param newAmount The new balance amount
   * @return true if successful, false otherwise
   */
  public boolean updateBalance(int userId, BigDecimal newAmount) {
    logger.debug("Updating balance for userId {} to amount: {}", userId, newAmount);

    if (userId <= 0) {
      logger.warn("Invalid userId provided: {}", userId);
      return false;
    }

    if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
      logger.warn("Invalid amount provided: {}", newAmount);
      return false;
    }

    return balanceDAO.updateBalance(userId, newAmount);
  }

  /**
   * Update user balance (double overload)
   * 
   * @param userId    The user ID
   * @param newAmount The new balance amount
   * @return true if successful, false otherwise
   */
  public boolean updateBalance(int userId, double newAmount) {
    return updateBalance(userId, BigDecimal.valueOf(newAmount));
  }
}