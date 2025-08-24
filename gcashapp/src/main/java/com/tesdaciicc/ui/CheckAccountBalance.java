package com.tesdaciicc.ui;

import java.util.Optional;
import java.util.Scanner;

import com.tesdaciicc.model.Balance;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.CheckBalanceService;

public class CheckAccountBalance {

    private static Scanner scanner = new Scanner(System.in);

    private CheckAccountBalance() {
        // Private constructor to prevent instantiation
    }

    public static void checkBalance(UserAuthentication user) {
        CheckAccountBalance.checkAccountBalance(user);
    }

    private static void checkAccountBalance(UserAuthentication user) {
        System.out.println("\n>>> Check Balance <<<");
        System.out.println("Account Holder: " + user.getName());
        
        try {
            // Initialize the CheckBalanceService
            CheckBalanceService balanceService = new CheckBalanceService();
            
            // Get balance information for the user
            Optional<Balance> balanceInfo = balanceService.getBalanceInfo(user.getId());
            
            if (balanceInfo.isPresent()) {
                Balance balance = balanceInfo.get();
                
                // Display balance information
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("  ACCOUNT BALANCE INFORMATION");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("Account ID     : " + user.getId());
                System.out.println("Account Name   : " + user.getName());
                System.out.println("Email          : " + user.getEmail());
                System.out.println("Phone Number   : " + user.getNumber());
                System.out.println("Current Balance: " + balance.getFormattedAmount());
                System.out.println("Last Updated   : " + balance.getUpdatedDate());
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                // Show balance status
                if (balance.isZero()) {
                    System.out.println("⚠️  Your account balance is zero.");
                    System.out.println("   Consider making a deposit to start using JCash services.");
                } else if (balance.isNegative()) {
                    System.out.println("❌ Your account has a negative balance.");
                    System.out.println("   Please deposit funds immediately.");
                } else {
                    System.out.println("✅ Your account is in good standing.");
                }
                
            } else {
                // Handle case where balance record doesn't exist
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("  ACCOUNT BALANCE INFORMATION");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("Account ID     : " + user.getId());
                System.out.println("Account Name   : " + user.getName());
                System.out.println("Email          : " + user.getEmail());
                System.out.println("Phone Number   : " + user.getNumber());
                System.out.println("Current Balance: Balance not initialized");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                System.out.println("⚠️  No balance record found for your account.");
                System.out.println("   This might be a new account that needs balance initialization.");
                
                // Option to initialize balance
                System.out.print("\nWould you like to initialize your balance with ₱0.00? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                
                if (choice.equals("y") || choice.equals("yes")) {
                    boolean initialized = balanceService.initializeBalance(user.getId());
                    if (initialized) {
                        System.out.println("✅ Balance initialized successfully with ₱0.00");
                        System.out.println("   You can now use Cash In to add funds to your account.");
                    } else {
                        System.out.println("❌ Failed to initialize balance. Please try again later.");
                    }
                } else {
                    System.out.println("Balance initialization cancelled.");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error occurred while checking balance.");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("   Please try again later or contact support.");
        }
    }

}
