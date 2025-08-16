package com.tesdaciicc.ui;

import com.tesdaciicc.service.CheckBalanceService;
import com.tesdaciicc.model.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class CheckBalance {

  private static final Logger logger = LoggerFactory.getLogger(CheckBalance.class);
  private final CheckBalanceService checkBalanceService;
  private final Scanner scanner;

  public CheckBalance() {
    this.checkBalanceService = new CheckBalanceService();
    this.scanner = new Scanner(System.in);
  }

  public CheckBalance(CheckBalanceService checkBalanceService, Scanner scanner) {
    this.checkBalanceService = checkBalanceService;
    this.scanner = scanner;
  }

  /**
   * Main method to display balance checking menu
   */
  public void displayBalanceMenu() {
    logger.info("Displaying balance check menu");

    while (true) {
      try {
        System.out.println("\n=== CHECK BALANCE ===");
        System.out.println("1. Check My Balance");
        System.out.println("2. Check Balance Details");
        System.out.println("3. Back to Main Menu");
        System.out.print("Choose an option (1-3): ");

        int choice = Integer.parseInt(scanner.nextLine().trim());

        switch (choice) {
          case 1:
            checkBalance();
            break;
          case 2:
            checkBalanceDetails();
            break;
          case 3:
            logger.info("Returning to main menu");
            return;
          default:
            System.out.println("❌ Invalid option. Please choose 1-3.");
        }
      } catch (NumberFormatException e) {
        System.out.println("❌ Invalid input. Please enter a number.");
        logger.warn("Invalid input in balance menu: {}", e.getMessage());
      } catch (Exception e) {
        System.out.println("❌ An error occurred. Please try again.");
        logger.error("Error in balance menu: {}", e.getMessage(), e);
      }
    }
  }

  /**
   * Check balance for current user (simplified version)
   */
  public void checkBalance() {
    try {
      System.out.print("Enter User ID: ");
      int userId = Integer.parseInt(scanner.nextLine().trim());

      displayUserBalance(userId);

    } catch (NumberFormatException e) {
      System.out.println("❌ Invalid User ID format. Please enter a valid number.");
      logger.warn("Invalid user ID format: {}", e.getMessage());
    } catch (Exception e) {
      System.out.println("❌ An error occurred while checking balance.");
      logger.error("Error checking balance: {}", e.getMessage(), e);
    }
  }

  /**
   * Check detailed balance information
   */
  public void checkBalanceDetails() {
    try {
      System.out.print("Enter User ID: ");
      int userId = Integer.parseInt(scanner.nextLine().trim());

      displayDetailedBalance(userId);

    } catch (NumberFormatException e) {
      System.out.println("❌ Invalid User ID format. Please enter a valid number.");
      logger.warn("Invalid user ID format: {}", e.getMessage());
    } catch (Exception e) {
      System.out.println("❌ An error occurred while checking balance details.");
      logger.error("Error checking balance details: {}", e.getMessage(), e);
    }
  }

  /**
   * Display user balance (simple format)
   * 
   * @param userId The user ID to check balance for
   */
  public void displayUserBalance(int userId) {
    logger.info("Displaying balance for userId: {}", userId);

    Optional<BigDecimal> balance = checkBalanceService.checkBalance(userId);

    if (balance.isPresent()) {
      String formattedBalance = String.format("₱%,.2f", balance.get());
      System.out.println("\n💰 Current Balance: " + formattedBalance);

      // Check if balance is low
      if (balance.get().compareTo(new BigDecimal("100")) < 0) {
        System.out.println("⚠️  Low Balance Warning: Consider adding funds to your account.");
      }
    } else {
      System.out.println("\n❌ Balance not found for User ID: " + userId);
      System.out.println("   Please check if the User ID is correct or contact support.");
    }
  }

  /**
   * Display detailed balance information
   * 
   * @param userId The user ID to check balance for
   */
  public void displayDetailedBalance(int userId) {
    logger.info("Displaying detailed balance for userId: {}", userId);

    Optional<Balance> balanceInfo = checkBalanceService.getBalanceInfo(userId);

    if (balanceInfo.isPresent()) {
      Balance balance = balanceInfo.get();

      System.out.println("\n=== BALANCE DETAILS ===");
      System.out.printf("User ID: %d%n", balance.getUserId());
      System.out.printf("Balance ID: %d%n", balance.getId());
      System.out.printf("Current Balance: %s%n", balance.getFormattedAmount());
      System.out.printf("Account Created: %s%n", balance.getCreatedDate());
      System.out.printf("Last Updated: %s%n", balance.getUpdatedDate());

      // Balance status
      BigDecimal amount = balance.getAmount();
      if (amount.compareTo(BigDecimal.ZERO) == 0) {
        System.out.println("Status: 🔴 No Balance");
      } else if (amount.compareTo(new BigDecimal("100")) < 0) {
        System.out.println("Status: 🟡 Low Balance");
      } else if (amount.compareTo(new BigDecimal("10000")) >= 0) {
        System.out.println("Status: 🟢 High Balance");
      } else {
        System.out.println("Status: 🟢 Active Balance");
      }

    } else {
      System.out.println("\n❌ Balance information not found for User ID: " + userId);
      System.out.println("   Please check if the User ID is correct or contact support.");
    }
  }

  /**
   * Quick balance check method (for use in other UI classes)
   * 
   * @param userId The user ID to check balance for
   * @return Formatted balance string or error message
   */
  public String quickBalanceCheck(int userId) {
    logger.debug("Quick balance check for userId: {}", userId);
    return checkBalanceService.getFormattedBalance(userId);
  }

  /**
   * Check if user has sufficient balance for a transaction
   * 
   * @param userId The user ID
   * @param amount The transaction amount
   * @return true if sufficient, false otherwise
   */
  public boolean checkSufficientBalance(int userId, double amount) {
    logger.debug("Checking sufficient balance for userId {} amount: {}", userId, amount);
    return checkBalanceService.hasSufficientBalance(userId, amount);
  }

  /**
   * Display insufficient balance message
   * 
   * @param userId         The user ID
   * @param requiredAmount The required amount
   */
  public void displayInsufficientBalanceMessage(int userId, double requiredAmount) {
    String currentBalance = quickBalanceCheck(userId);
    System.out.println("\n❌ INSUFFICIENT BALANCE");
    System.out.printf("Current Balance: %s%n", currentBalance);
    System.out.printf("Required Amount: ₱%.2f%n", requiredAmount);
    System.out.println("Please add funds to your account to proceed with this transaction.");
  }
}