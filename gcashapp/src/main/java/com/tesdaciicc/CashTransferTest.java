package com.tesdaciicc;

import com.tesdaciicc.service.CashTransferService;
import com.tesdaciicc.service.CashTransferService.TransferResult;
import com.tesdaciicc.service.CashTransferService.DailyTransferSummary;
import com.tesdaciicc.model.CashTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class CashTransferTest {

    private static final Logger logger = LoggerFactory.getLogger(CashTransferTest.class);
    private final CashTransferService transferService;
    private final Scanner scanner;
    
    public CashTransferTest() {
        this.transferService = new CashTransferService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main cash transfer method that handles user interaction
     * @param currentUserId Currently logged-in user's ID
     */
    public void performCashTransfer(int currentUserId) {
        System.out.println("\n=== CASH TRANSFER ===");
        
        try {
            // Step 1: Get recipient mobile number
            System.out.print("Enter recipient's mobile number (11 digits, e.g., 09123456789): ");
            String recipientMobile = scanner.nextLine().trim();
            
            // Validate mobile number format
            if (!transferService.isValidMobileNumber(recipientMobile)) {
                System.out.println("❌ Invalid mobile number format. Must be 11 digits starting with 09.");
                return;
            }
            
            // Check if recipient exists
            if (!transferService.recipientExists(recipientMobile)) {
                System.out.println("❌ Recipient not found. Please verify the mobile number.");
                return;
            }
            
            // Display recipient name
            String recipientName = transferService.getRecipientName(recipientMobile);
            System.out.println("📱 Recipient: " + recipientName + " (" + recipientMobile + ")");
            
            // Step 2: Get transfer amount
            System.out.print("Enter amount to transfer (₱1.00 - ₱50,000.00): ₱");
            String amountStr = scanner.nextLine().trim();
            
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid amount format. Please enter a valid number.");
                return;
            }
            
            // Step 3: Preview service fee
            BigDecimal serviceFee = transferService.previewServiceFee(amount);
            BigDecimal totalAmount = amount.add(serviceFee);
            
            System.out.println("\n--- TRANSFER SUMMARY ---");
            System.out.println("Transfer Amount: ₱" + String.format("%,.2f", amount));
            System.out.println("Service Fee: ₱" + String.format("%,.2f", serviceFee));
            System.out.println("Total Deduction: ₱" + String.format("%,.2f", totalAmount));
            
            if (serviceFee.compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("🎉 FREE TRANSFER! (Amount ≥ ₱500.00)");
            }
            
            // Step 4: Get transfer description (optional)
            System.out.print("Enter description (optional): ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                description = "Cash Transfer";
            }
            
            // Step 5: Confirm transfer
            System.out.print("\nConfirm transfer? (y/N): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                System.out.println("❌ Transfer cancelled.");
                return;
            }
            
            // Step 6: Execute transfer
            System.out.println("\n⏳ Processing transfer...");
            TransferResult result = transferService.cashTransfer(currentUserId, recipientMobile, amount, description);
            
            // Step 7: Display result
            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                
                // Display transfer details
                CashTransfer transfer = result.getTransfer();
                if (transfer != null) {
                    System.out.println("\n--- TRANSFER RECEIPT ---");
                    System.out.println("Transaction ID: " + transfer.getTransactionId());
                    System.out.println("Date: " + transfer.getTransactionDate());
                    System.out.println("To: " + recipientName + " (" + recipientMobile + ")");
                    System.out.println("Amount: " + transfer.getFormattedAmount());
                    System.out.println("Status: " + transfer.getStatus());
                    System.out.println("Description: " + transfer.getDescription());
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }
            
        } catch (Exception e) {
            logger.error("Error during cash transfer: {}", e.getMessage(), e);
            System.out.println("❌ An unexpected error occurred. Please try again.");
        }
    }
    
    /**
     * Display transfer history for current user
     * @param currentUserId Currently logged-in user's ID
     */
    public void showTransferHistory(int currentUserId) {
        System.out.println("\n=== TRANSFER HISTORY ===");
        
        try {
            List<CashTransfer> transfers = transferService.getTransferHistory(currentUserId);
            
            if (transfers.isEmpty()) {
                System.out.println("📝 No transfer history found.");
                return;
            }
            
            System.out.println("Found " + transfers.size() + " transfer(s):\n");
            
            for (CashTransfer transfer : transfers) {
                System.out.println("--- Transaction ID: " + transfer.getTransactionId() + " ---");
                System.out.println("Date: " + transfer.getTransactionDate());
                System.out.println("Amount: " + transfer.getFormattedAmount());
                System.out.println("To: " + transfer.getTransferToAccountNo());
                System.out.println("Status: " + transfer.getStatus());
                System.out.println("Description: " + transfer.getDescription());
                System.out.println();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving transfer history: {}", e.getMessage(), e);
            System.out.println("❌ Error retrieving transfer history.");
        }
    }
    
    /**
     * Display daily transfer summary
     * @param currentUserId Currently logged-in user's ID
     * @param userMobileNumber User's mobile number
     */
    public void showDailyTransferSummary(int currentUserId, String userMobileNumber) {
        System.out.println("\n=== DAILY TRANSFER SUMMARY ===");
        
        try {
            DailyTransferSummary summary = transferService.getDailyTransferSummary(currentUserId, userMobileNumber);
            
            System.out.println("Today's Transfers:");
            System.out.println("📊 Total Amount: " + summary.getFormattedTotalAmount());
            System.out.println("📈 Total Transactions: " + summary.getTotalTransactions());
            System.out.println();
            System.out.println("Remaining Limits:");
            System.out.println("💰 Amount Limit: " + summary.getFormattedRemainingLimit());
            System.out.println("🔢 Transaction Limit: " + summary.getRemainingTransactions());
            
            // Display warnings if close to limits
            if (summary.getRemainingTransactions() <= 3) {
                System.out.println("⚠️  Warning: You are close to your daily transaction limit!");
            }
            
            BigDecimal remainingAmount = summary.getRemainingLimit();
            if (remainingAmount.compareTo(BigDecimal.valueOf(10000)) < 0) {
                System.out.println("⚠️  Warning: You are close to your daily amount limit!");
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving daily summary: {}", e.getMessage(), e);
            System.out.println("❌ Error retrieving daily summary.");
        }
    }
    
    /**
     * Validate transfer before execution (for preview purposes)
     * @param currentUserId Current user's ID
     * @param recipientMobile Recipient's mobile number
     * @param amount Transfer amount
     */
    public void previewTransfer(int currentUserId, String recipientMobile, BigDecimal amount) {
        System.out.println("\n=== TRANSFER PREVIEW ===");
        
        // Check recipient
        if (!transferService.recipientExists(recipientMobile)) {
            System.out.println("❌ Recipient not found.");
            return;
        }
        
        String recipientName = transferService.getRecipientName(recipientMobile);
        System.out.println("📱 To: " + recipientName + " (" + recipientMobile + ")");
        
        // Calculate fees
        BigDecimal serviceFee = transferService.previewServiceFee(amount);
        BigDecimal totalAmount = amount.add(serviceFee);
        
        System.out.println("💰 Amount: ₱" + String.format("%,.2f", amount));
        System.out.println("💳 Service Fee: ₱" + String.format("%,.2f", serviceFee));
        System.out.println("📊 Total: ₱" + String.format("%,.2f", totalAmount));
        
        if (serviceFee.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("🎉 FREE TRANSFER!");
        }
    }
    
    /**
     * Example usage in a menu system
     */
    public static void demonstrateUsage() {
        CashTransferTest example = new CashTransferTest();
        
        // Example: Current user ID (would come from session management)
        int currentUserId = 1;
        String currentUserMobile = "09123456789";
        
        System.out.println("=== CASH TRANSFER SYSTEM DEMO ===");
        
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Send Money");
            System.out.println("2. Transfer History");
            System.out.println("3. Daily Summary");
            System.out.println("4. Preview Transfer");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    example.performCashTransfer(currentUserId);
                    break;
                case "2":
                    example.showTransferHistory(currentUserId);
                    break;
                case "3":
                    example.showDailyTransferSummary(currentUserId, currentUserMobile);
                    break;
                case "4":
                    System.out.print("Enter recipient mobile: ");
                    String mobile = scanner.nextLine().trim();
                    System.out.print("Enter amount: ₱");
                    try {
                        BigDecimal amt = new BigDecimal(scanner.nextLine().trim());
                        example.previewTransfer(currentUserId, mobile, amt);
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid amount.");
                    }
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
    
    public static void main(String[] args) {
        demonstrateUsage();
    }

}
