package com.tesdaciicc.ui;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.CashInService;
import java.math.BigDecimal;
import java.util.Scanner;

public class CashInTransactions {

    private static Scanner scanner = new Scanner(System.in);
    private static CashInService cashInService = new CashInService();

    private CashInTransactions() {
        // Private constructor to prevent instantiation
    }

    /**
     * Handle cash-in operation UI - deposits money to current user's account
     * @param user The authenticated user
     */
    public static void cashIn(UserAuthentication user) {
        System.out.println("\n>>>     Cash In     <<<");
        System.out.println("Your Account: " + user.getNumber());
        System.out.println("Account Holder: " + user.getName());
        
        boolean continueTransaction = true;
        
        while (continueTransaction) {
            try {
                // Get deposit source/description
                System.out.print("Enter deposit source/description (e.g., 'Bank Transfer', 'Cash Deposit', etc.): ");
                String depositSource = scanner.nextLine().trim();
                
                if (depositSource.isEmpty()) {
                    System.out.println("Deposit source/description cannot be empty. Please try again.");
                    continue;
                }
                
                // Get amount to deposit
                System.out.print("Enter amount to deposit: ₱");
                String amountStr = scanner.nextLine().trim();
                
                if (amountStr.isEmpty()) {
                    System.out.println("Amount cannot be empty. Please try again.");
                    continue;
                }
                
                BigDecimal amount;
                try {
                    amount = new BigDecimal(amountStr);
                    
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        System.out.println("Amount must be greater than zero. Please try again.");
                        continue;
                    }
                    
                    // Check for reasonable amount limit (optional)
                    if (amount.compareTo(new BigDecimal("100000")) > 0) {
                        System.out.println("Amount exceeds maximum deposit limit of ₱100,000. Please try again.");
                        continue;
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount format. Please enter a valid number.");
                    continue;
                }
                
                // Display deposit summary
                System.out.println("\n--- Deposit Summary ---");
                System.out.println("Your Account: " + user.getNumber());
                System.out.println("Account Holder: " + user.getName());
                System.out.println("Deposit Source: " + depositSource);
                System.out.println("Deposit Amount: ₱" + amount);
                
                // Confirm deposit
                System.out.print("Confirm deposit to your account? (y/n): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();
                
                if (confirmation.equals("y") || confirmation.equals("yes")) {
                    // Process the cash-in (deposit) to user's own account
                    // Your existing CashInService already works correctly for deposits
                    boolean success = cashInService.processCashIn(user.getNumber(), amount, depositSource);
                    
                    if (success) {
                        System.out.println("\n✓ Cash-in (Deposit) successful!");
                        System.out.println("Amount ₱" + amount + " has been deposited to your account (" + user.getNumber() + ")");
                        System.out.println("Deposit source: " + depositSource);
                        System.out.println("Transaction completed successfully.");
                        
                        // Show updated balance (if possible)
                        try {
                            var balanceOpt = cashInService.getCurrentBalance(user.getNumber());
                            if (balanceOpt.isPresent()) {
                                System.out.println("Updated Balance: ₱" + String.format("%,.2f", balanceOpt.get().getAmount()));
                            }
                        } catch (Exception e) {
                            // Don't fail the whole operation if balance check fails
                            System.out.println("Note: Unable to display updated balance at this time.");
                        }
                        
                        continueTransaction = false;
                    } else {
                        System.out.println("\n✗ Cash-in (Deposit) failed!");
                        System.out.println("Please try again or contact support if the problem persists.");
                        
                        // Ask if they want to retry
                        System.out.print("Would you like to try another deposit? (y/n): ");
                        String retry = scanner.nextLine().trim().toLowerCase();
                        if (!retry.equals("y") && !retry.equals("yes")) {
                            continueTransaction = false;
                        }
                    }
                } else {
                    System.out.println("Deposit cancelled.");
                    
                    // Ask if they want to start over
                    System.out.print("Would you like to make a different deposit? (y/n): ");
                    String newTransaction = scanner.nextLine().trim().toLowerCase();
                    if (!newTransaction.equals("y") && !newTransaction.equals("yes")) {
                        continueTransaction = false;
                    }
                }
                
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                System.out.println("Please try again.");
                
                // Ask if they want to retry
                System.out.print("Would you like to try again? (y/n): ");
                String retry = scanner.nextLine().trim().toLowerCase();
                if (!retry.equals("y") && !retry.equals("yes")) {
                    continueTransaction = false;
                }
            }
        }
        
        System.out.println("Returning to home page...");
    }

}
