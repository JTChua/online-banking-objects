package com.tesdaciicc.ui;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.tesdaciicc.model.CashTransfer;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.CashTransferService;

import java.util.Scanner;

public class CashTransferTransactions {

    private static Scanner scanner = new Scanner(System.in);

    private CashTransferTransactions() {
        // Private constructor to prevent instantiation
    }

    public static void cashTransfer(UserAuthentication user) {
        CashTransferTransactions.cashTransferTransactions(user);
    }

    private static void cashTransferTransactions(UserAuthentication user) {
        CashTransferService transferService = new CashTransferService();
    
        System.out.println("\n>>>     Cash Transfer     <<<");
        System.out.println("Send money to another JCash user");
        System.out.println("----------------------------------------");
    
        try {
            // Get recipient's mobile number
            String recipientNumber = getRecipientMobileNumber();
            if (recipientNumber == null) {
                System.out.println("Transfer cancelled.");
                return;
            }
            
            // Validate recipient exists and not self-transfer
            if (!transferService.recipientExists(recipientNumber)) {
                System.out.println("❌ Error: Recipient not found. Please verify the mobile number.");
                return;
            }
            
            if (recipientNumber.equals(user.getNumber())) {
                System.out.println("❌ Error: Cannot transfer to your own account.");
                return;
            }
            
            // Display recipient information
            String recipientName = transferService.getRecipientName(recipientNumber);
            System.out.println("\n📱 Recipient Found:");
            System.out.println("   Name: " + recipientName);
            System.out.println("   Mobile: " + recipientNumber);
            
            // Get transfer amount
            BigDecimal transferAmount = getTransferAmount(transferService);
            if (transferAmount == null) {
                System.out.println("Transfer cancelled.");
                return;
            }
            
            // Calculate and display fees
            BigDecimal serviceFee = transferService.previewServiceFee(transferAmount);
            BigDecimal totalAmount = transferAmount.add(serviceFee);
            
            System.out.println("\n💰 Transfer Summary:");
            System.out.println("   Transfer Amount: ₱" + String.format("%,.2f", transferAmount));
            System.out.println("   Service Fee: ₱" + String.format("%,.2f", serviceFee));
            System.out.println("   Total Deduction: ₱" + String.format("%,.2f", totalAmount));
            
            if (serviceFee.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("   🎉 FREE TRANSFER! (Amount ≥ ₱500.00)");
            }
            
            // Get transfer description (optional)
            System.out.print("\n📝 Transfer Description (optional): ");
            scanner.nextLine(); // Clear buffer
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = "Cash Transfer";
            }
            
            // Display final confirmation
            System.out.println("\n🔍 Final Transfer Details:");
            System.out.println("   From: " + user.getName() + " (" + user.getNumber() + ")");
            System.out.println("   To: " + recipientName + " (" + recipientNumber + ")");
            System.out.println("   Amount: ₱" + String.format("%,.2f", transferAmount));
            System.out.println("   Service Fee: ₱" + String.format("%,.2f", serviceFee));
            System.out.println("   Total Deduction: ₱" + String.format("%,.2f", totalAmount));
            System.out.println("   Description: " + description);
            
            // Final confirmation
            if (!confirmTransfer()) {
                System.out.println("Transfer cancelled by user.");
                return;
            }
            
            // Execute the transfer
            System.out.println("\n⏳ Processing transfer...");
            
            CashTransferService.TransferResult result = transferService.cashTransfer(
                user.getId(),
                recipientNumber,
                transferAmount,
                description
            );
            
            // Display result
            if (result.isSuccess()) {
                System.out.println("\n✅ " + result.getMessage());
                
                if (result.getTransfer() != null) {
                    CashTransfer transfer = result.getTransfer();
                    System.out.println("\n📄 Transaction Details:");
                    System.out.println("   Transaction ID: " + transfer.getTransactionId());
                    System.out.println("   Date: " + transfer.getTransactionDate().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    System.out.println("   Status: COMPLETED");
                }
                
                // Show daily transfer summary
                displayDailyTransferSummary(transferService, user);
                
            } else {
                System.out.println("\n❌ Transfer Failed: " + result.getMessage());
            }
        
        } catch (Exception e) {
            System.out.println("❌ An unexpected error occurred: " + e.getMessage());
            System.out.println("Please try again or contact support.");
        }
    }

    /**
     * Get recipient's mobile number with validation
     */
    private static String getRecipientMobileNumber() {
        int attempts = 0;
        final int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("📱 Enter recipient's mobile number (11 digits, 09xxxxxxxxx): ");
            String mobileNumber = scanner.nextLine().trim();
            
            // Check for cancellation
            if (mobileNumber.equalsIgnoreCase("cancel") || mobileNumber.equalsIgnoreCase("c")) {
                return null;
            }
            
            // Validate mobile number format
            if (mobileNumber.matches("^09\\d{9}$") && mobileNumber.length() == 11) {
                return mobileNumber;
            }
            
            attempts++;
            System.out.println("❌ Invalid mobile number format. Must be 11 digits starting with 09.");
            System.out.println("   Example: 09171234567");
            
            if (attempts < maxAttempts) {
                System.out.println("   Attempts remaining: " + (maxAttempts - attempts));
                System.out.println("   (Type 'cancel' to cancel transfer)");
            }
        }
        
        System.out.println("❌ Maximum attempts reached. Please try again later.");
        return null;
    }

    /**
     * Get transfer amount with validation
     */
    private static BigDecimal getTransferAmount(CashTransferService transferService) {
        int attempts = 0;
        final int maxAttempts = 3;
        final BigDecimal minAmount = BigDecimal.valueOf(1.00);
        final BigDecimal maxAmount = BigDecimal.valueOf(50000.00);
        
        while (attempts < maxAttempts) {
            try {
                System.out.print("💰 Enter transfer amount (₱1.00 - ₱50,000.00): ₱");
                String input = scanner.nextLine().trim();
                
                // Check for cancellation
                if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("c")) {
                    return null;
                }
                
                // Remove comma separators if present
                input = input.replace(",", "");
                
                BigDecimal amount = new BigDecimal(input);
                
                // Validate amount range
                if (amount.compareTo(minAmount) < 0) {
                    System.out.println("❌ Amount too low. Minimum transfer amount is ₱1.00");
                    attempts++;
                    continue;
                }
                
                if (amount.compareTo(maxAmount) > 0) {
                    System.out.println("❌ Amount too high. Maximum transfer amount is ₱50,000.00");
                    attempts++;
                    continue;
                }
                
                // Validate decimal places (max 2)
                if (amount.scale() > 2) {
                    System.out.println("❌ Invalid amount format. Maximum 2 decimal places allowed.");
                    attempts++;
                    continue;
                }
                
                // Preview service fee
                BigDecimal serviceFee = transferService.previewServiceFee(amount);
                if (serviceFee.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("ℹ️  Service fee: ₱" + String.format("%.2f", serviceFee) + 
                                    " (Free for transfers ≥ ₱500.00)");
                } else {
                    System.out.println("🎉 No service fee! (Amount ≥ ₱500.00)");
                }
                
                return amount;
                
            } catch (NumberFormatException e) {
                attempts++;
                System.out.println("❌ Invalid amount format. Please enter a valid number.");
                System.out.println("   Examples: 100, 100.50, 1500.75");
                
                if (attempts < maxAttempts) {
                    System.out.println("   Attempts remaining: " + (maxAttempts - attempts));
                    System.out.println("   (Type 'cancel' to cancel transfer)");
                }
            }
        }
        
        System.out.println("❌ Maximum attempts reached. Please try again later.");
        return null;
    }

    /**
     * Get final transfer confirmation
     */
    private static boolean confirmTransfer() {
        int attempts = 0;
        final int maxAttempts = 3;
        
        while (attempts < maxAttempts) {
            System.out.print("\n❓ Proceed with transfer? (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (confirmation.equals("yes") || confirmation.equals("y")) {
                return true;
            } else if (confirmation.equals("no") || confirmation.equals("n")) {
                return false;
            }
            
            attempts++;
            System.out.println("❌ Please enter 'yes' or 'no'.");
            
            if (attempts < maxAttempts) {
                System.out.println("   Attempts remaining: " + (maxAttempts - attempts));
            }
        }
        
        System.out.println("❌ Invalid response. Transfer cancelled for security.");
        return false;
    }

    /**
     * Display daily transfer summary
     */
    private static void displayDailyTransferSummary(CashTransferService transferService, UserAuthentication user) {
        try {
            CashTransferService.DailyTransferSummary summary = 
                transferService.getDailyTransferSummary(user.getId(), user.getNumber());
            
            System.out.println("\n📊 Today's Transfer Summary:");
            System.out.println("   Total Amount Transferred: " + summary.getFormattedTotalAmount());
            System.out.println("   Total Transactions: " + summary.getTotalTransactions());
            System.out.println("   Remaining Daily Limit: " + summary.getFormattedRemainingLimit());
            System.out.println("   Remaining Transactions: " + summary.getRemainingTransactions());
            
            // Warn if approaching limits
            if (summary.getRemainingTransactions() <= 3) {
                System.out.println("⚠️  Warning: You're approaching your daily transaction limit!");
            }
            
            if (summary.getRemainingLimit().compareTo(BigDecimal.valueOf(5000)) <= 0) {
                System.out.println("⚠️  Warning: You're approaching your daily amount limit!");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  Could not retrieve daily transfer summary.");
        }
    }

}


