package com.tesdaciicc;

import com.tesdaciicc.service.CashTransferService;
import com.tesdaciicc.service.CashTransferService.TransferResult;
import com.tesdaciicc.service.CashTransferService.DailyTransferSummary;
import com.tesdaciicc.model.CashTransfer;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.model.Balance;
import com.tesdaciicc.data.repository.UserDAO;
import com.tesdaciicc.data.repository.BalanceDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CashTransTest {

    private final CashTransferService transferService;
    private final UserDAO userDAO;
    private final BalanceDAO balanceDAO;
    private final Scanner scanner;
    
    // Test user IDs (you can modify these based on your test data)
    private static final int TEST_SENDER_ID = 1;
    private static final int TEST_RECIPIENT_ID = 2;
    
    public CashTransTest() {
        this.transferService = new CashTransferService();
        this.userDAO = new UserDAO();
        this.balanceDAO = new BalanceDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    CASH TRANSFER SYSTEM TEST SUITE    ");
        System.out.println("========================================");
        
        CashTransTest test = new CashTransTest();
        test.runInteractiveTest();
    }
    
    /**
     * Interactive test menu
     */
    public void runInteractiveTest() {
        while (true) {
            displayMainMenu();
            String choice = getUserInput("Enter your choice: ");
            
            switch (choice) {
                case "1":
                    testBasicTransfer();
                    break;
                case "2":
                    testValidationScenarios();
                    break;
                case "3":
                    testDailyLimits();
                    break;
                case "4":
                    testEdgeCases();
                    break;
                case "5":
                    testTransferHistory();
                    break;
                case "6":
                    testInteractiveTransfer();
                    break;
                case "7":
                    setupTestData();
                    break;
                case "8":
                    displayTestUsers();
                    break;
                case "9":
                    displayUserBalances();
                    break;
                case "0":
                    System.out.println("👋 Test suite completed. Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid choice. Please try again.");
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    /**
     * Display main test menu
     */
    private void displayMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("            TEST MENU OPTIONS            ");
        System.out.println("==========================================");
        System.out.println("1. 🧪 Basic Transfer Test");
        System.out.println("2. ✅ Validation Scenarios Test");
        System.out.println("3. 📊 Daily Limits Test");
        System.out.println("4. ⚠️  Edge Cases Test");
        System.out.println("5. 📝 Transfer History Test");
        System.out.println("6. 💰 Interactive Transfer Test");
        System.out.println("7. 🔧 Setup Test Data");
        System.out.println("8. 👥 Display Test Users");
        System.out.println("9. 💳 Display User Balances");
        System.out.println("0. 🚪 Exit");
        System.out.println("==========================================");
    }
    
    /**
     * Test 1: Basic Transfer Functionality
     */
    public void testBasicTransfer() {
        System.out.println("\n🧪 BASIC TRANSFER TEST");
        System.out.println("======================");
        
        try {
            // Get test users
            Optional<UserAuthentication> senderOpt = userDAO.findById(TEST_SENDER_ID);
            Optional<UserAuthentication> recipientOpt = userDAO.findById(TEST_RECIPIENT_ID);
            
            if (!senderOpt.isPresent() || !recipientOpt.isPresent()) {
                System.out.println("❌ Test users not found. Please run 'Setup Test Data' first.");
                return;
            }
            
            UserAuthentication sender = senderOpt.get();
            UserAuthentication recipient = recipientOpt.get();
            
            System.out.println("👤 Sender: " + sender.getName() + " (" + sender.getNumber() + ")");
            System.out.println("👤 Recipient: " + recipient.getName() + " (" + recipient.getNumber() + ")");
            
            // Display current balances
            displayUserBalance("Sender", TEST_SENDER_ID);
            displayUserBalance("Recipient", TEST_RECIPIENT_ID);
            
            // Test transfer
            BigDecimal transferAmount = BigDecimal.valueOf(500.00);
            System.out.println("\n💰 Testing transfer of ₱" + transferAmount);
            
            TransferResult result = transferService.cashTransfer(
                TEST_SENDER_ID, 
                recipient.getNumber(), 
                transferAmount, 
                "Basic Transfer Test"
            );
            
            if (result.isSuccess()) {
                System.out.println("✅ " + result.getMessage());
                
                // Display updated balances
                System.out.println("\nUpdated Balances:");
                displayUserBalance("Sender", TEST_SENDER_ID);
                displayUserBalance("Recipient", TEST_RECIPIENT_ID);
                
                // Display transfer details
                if (result.getTransfer() != null) {
                    displayTransferDetails(result.getTransfer());
                }
            } else {
                System.out.println("❌ " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error during basic transfer test: " + e.getMessage());
        }
    }
    
    /**
     * Test 2: Validation Scenarios
     */
    public void testValidationScenarios() {
        System.out.println("\n✅ VALIDATION SCENARIOS TEST");
        System.out.println("============================");
        
        Optional<UserAuthentication> senderOpt = userDAO.findById(TEST_SENDER_ID);
        if (!senderOpt.isPresent()) {
            System.out.println("❌ Test sender not found.");
            return;
        }
        
        UserAuthentication sender = senderOpt.get();
        
        // Test cases
        TestCase[] testCases = {
            new TestCase("Invalid Amount - Zero", sender.getNumber(), BigDecimal.ZERO, "Should fail"),
            new TestCase("Invalid Amount - Negative", sender.getNumber(), BigDecimal.valueOf(-100), "Should fail"),
            new TestCase("Invalid Amount - Too Small", sender.getNumber(), BigDecimal.valueOf(0.50), "Should fail"),
            new TestCase("Invalid Amount - Too Large", sender.getNumber(), BigDecimal.valueOf(100000), "Should fail"),
            new TestCase("Invalid Mobile - Wrong Format", "1234567890", BigDecimal.valueOf(100), "Should fail"),
            new TestCase("Invalid Mobile - Self Transfer", sender.getNumber(), BigDecimal.valueOf(100), "Should fail"),
            new TestCase("Invalid Mobile - Non-existent", "09999999999", BigDecimal.valueOf(100), "Should fail"),
            new TestCase("Valid Transfer", getValidRecipientNumber(), BigDecimal.valueOf(100), "Should succeed")
        };
        
        for (TestCase testCase : testCases) {
            System.out.println("\n📋 Test: " + testCase.name);
            System.out.println("   Amount: ₱" + testCase.amount);
            System.out.println("   Recipient: " + testCase.recipientMobile);
            
            TransferResult result = transferService.cashTransfer(
                TEST_SENDER_ID, 
                testCase.recipientMobile, 
                testCase.amount, 
                testCase.description
            );
            
            System.out.println("   Result: " + (result.isSuccess() ? "✅ SUCCESS" : "❌ FAILED"));
            System.out.println("   Message: " + result.getMessage());
        }
    }
    
    /**
     * Test 3: Daily Limits
     */
    public void testDailyLimits() {
        System.out.println("\n📊 DAILY LIMITS TEST");
        System.out.println("====================");
        
        Optional<UserAuthentication> senderOpt = userDAO.findById(TEST_SENDER_ID);
        if (!senderOpt.isPresent()) {
            System.out.println("❌ Test sender not found.");
            return;
        }
        
        UserAuthentication sender = senderOpt.get();
        
        // Display current daily summary
        DailyTransferSummary summary = transferService.getDailyTransferSummary(TEST_SENDER_ID, sender.getNumber());
        System.out.println("📈 Current Daily Summary:");
        System.out.println("   Total Amount: " + summary.getFormattedTotalAmount());
        System.out.println("   Total Transactions: " + summary.getTotalTransactions());
        System.out.println("   Remaining Limit: " + summary.getFormattedRemainingLimit());
        System.out.println("   Remaining Transactions: " + summary.getRemainingTransactions());
        
        // Test small transfers to approach transaction limit
        String recipientMobile = getValidRecipientNumber();
        if (recipientMobile != null) {
            System.out.println("\n🧪 Testing multiple small transfers...");
            
            for (int i = 1; i <= 5; i++) {
                System.out.println("\nTransfer #" + i + ":");
                TransferResult result = transferService.cashTransfer(
                    TEST_SENDER_ID,
                    recipientMobile,
                    BigDecimal.valueOf(50.00),
                    "Daily Limit Test #" + i
                );
                
                System.out.println("   " + (result.isSuccess() ? "✅" : "❌") + " " + result.getMessage());
                
                if (!result.isSuccess()) {
                    break;
                }
            }
            
            // Display updated summary
            summary = transferService.getDailyTransferSummary(TEST_SENDER_ID, sender.getNumber());
            System.out.println("\n📊 Updated Daily Summary:");
            System.out.println("   Total Amount: " + summary.getFormattedTotalAmount());
            System.out.println("   Total Transactions: " + summary.getTotalTransactions());
        }
    }
    
    /**
     * Test 4: Edge Cases
     */
    public void testEdgeCases() {
        System.out.println("\n⚠️  EDGE CASES TEST");
        System.out.println("==================");
        
        // Test service fee calculation
        System.out.println("💳 Service Fee Tests:");
        BigDecimal[] testAmounts = {
            BigDecimal.valueOf(1.00),
            BigDecimal.valueOf(499.99),
            BigDecimal.valueOf(500.00),
            BigDecimal.valueOf(1000.00)
        };
        
        for (BigDecimal amount : testAmounts) {
            BigDecimal fee = transferService.previewServiceFee(amount);
            System.out.println("   ₱" + amount + " → Service Fee: ₱" + fee);
        }
        
        // Test mobile number validation
        System.out.println("\n📱 Mobile Number Validation Tests:");
        String[] testNumbers = {
            "09123456789", // Valid
            "0912345678",  // Too short
            "091234567890", // Too long
            "08123456789", // Wrong prefix
            "9123456789",  // Missing 0
            "abcdefghijk"  // Invalid characters
        };
        
        for (String number : testNumbers) {
            boolean valid = transferService.isValidMobileNumber(number);
            System.out.println("   " + number + " → " + (valid ? "✅ Valid" : "❌ Invalid"));
        }
        
        // Test recipient existence
        System.out.println("\n👥 Recipient Existence Tests:");
        String[] testRecipients = {
            "09123456789",
            "09999999999"
        };
        
        for (String number : testRecipients) {
            boolean exists = transferService.recipientExists(number);
            String name = transferService.getRecipientName(number);
            System.out.println("   " + number + " → " + (exists ? "✅ Exists (" + name + ")" : "❌ Not Found"));
        }
    }
    
    /**
     * Test 5: Transfer History
     */
    public void testTransferHistory() {
        System.out.println("\n📝 TRANSFER HISTORY TEST");
        System.out.println("========================");
        
        // Get transfer history for sender
        List<CashTransfer> transfers = transferService.getTransferHistory(TEST_SENDER_ID);
        
        System.out.println("📊 Transfer History for User ID " + TEST_SENDER_ID + ":");
        System.out.println("   Total Transfers: " + transfers.size());
        
        if (transfers.isEmpty()) {
            System.out.println("   No transfers found.");
        } else {
            System.out.println("   Recent Transfers:");
            
            // Display last 5 transfers
            int count = Math.min(5, transfers.size());
            for (int i = 0; i < count; i++) {
                CashTransfer transfer = transfers.get(i);
                System.out.println("   " + (i + 1) + ". ID: " + transfer.getTransactionId() + 
                                 " | Amount: " + transfer.getFormattedAmount() + 
                                 " | To: " + transfer.getTransferToAccountNo() + 
                                 " | Date: " + transfer.getTransactionDate());
            }
        }
    }
    
    /**
     * Test 6: Interactive Transfer
     */
    public void testInteractiveTransfer() {
        System.out.println("\n💰 INTERACTIVE TRANSFER TEST");
        System.out.println("============================");
        
        try {
            // Display available users
            displayTestUsers();
            
            String senderIdStr = getUserInput("Enter sender User ID: ");
            int senderId = Integer.parseInt(senderIdStr);
            
            String recipientMobile = getUserInput("Enter recipient mobile number: ");
            String amountStr = getUserInput("Enter transfer amount: ");
            BigDecimal amount = new BigDecimal(amountStr);
            String description = getUserInput("Enter description: ");
            
            // Preview transfer
            System.out.println("\n📋 Transfer Preview:");
            BigDecimal fee = transferService.previewServiceFee(amount);
            System.out.println("   Amount: ₱" + String.format("%.2f", amount));
            System.out.println("   Service Fee: ₱" + String.format("%.2f", fee));
            System.out.println("   Total: ₱" + String.format("%.2f", amount.add(fee)));
            
            String confirm = getUserInput("Confirm transfer? (y/n): ");
            if (confirm.toLowerCase().startsWith("y")) {
                TransferResult result = transferService.cashTransfer(senderId, recipientMobile, amount, description);
                
                System.out.println("\n" + (result.isSuccess() ? "✅" : "❌") + " " + result.getMessage());
                
                if (result.isSuccess() && result.getTransfer() != null) {
                    displayTransferDetails(result.getTransfer());
                }
            } else {
                System.out.println("❌ Transfer cancelled.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number format.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    /**
     * Setup test data (create users and balances if they don't exist)
     */
    public void setupTestData() {
        System.out.println("\n🔧 SETUP TEST DATA");
        System.out.println("==================");
        
        try {
            // Create test users if they don't exist
            createTestUserIfNotExists(1, "John Doe", "john@test.com", "09123456789", "1234");
            createTestUserIfNotExists(2, "Jane Smith", "jane@test.com", "09987654321", "5678");
            createTestUserIfNotExists(3, "Bob Johnson", "bob@test.com", "09555666777", "9999");
            
            // Create balances if they don't exist
            createTestBalanceIfNotExists(1, 10000.00);
            createTestBalanceIfNotExists(2, 5000.00);
            createTestBalanceIfNotExists(3, 2000.00);
            
            System.out.println("✅ Test data setup completed!");
            
        } catch (Exception e) {
            System.out.println("❌ Error setting up test data: " + e.getMessage());
        }
    }
    
    /**
     * Display test users
     */
    public void displayTestUsers() {
        System.out.println("\n👥 TEST USERS");
        System.out.println("=============");
        
        try {
            for (int i = 1; i <= 3; i++) {
                Optional<UserAuthentication> userOpt = userDAO.findById(i);
                if (userOpt.isPresent()) {
                    UserAuthentication user = userOpt.get();
                    System.out.println("ID: " + user.getId() + " | Name: " + user.getName() + 
                                     " | Mobile: " + user.getNumber() + " | Email: " + user.getEmail());
                } else {
                    System.out.println("ID: " + i + " | Not found");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error displaying users: " + e.getMessage());
        }
    }
    
    /**
     * Display user balances
     */
    public void displayUserBalances() {
        System.out.println("\n💳 USER BALANCES");
        System.out.println("================");
        
        for (int i = 1; i <= 3; i++) {
            displayUserBalance("User " + i, i);
        }
    }
    
    // Helper methods
    
    private void displayUserBalance(String label, int userId) {
        try {
            Optional<Balance> balanceOpt = balanceDAO.findByUserId(userId);
            if (balanceOpt.isPresent()) {
                Balance balance = balanceOpt.get();
                System.out.println("💰 " + label + " Balance: " + balance.getFormattedAmount());
            } else {
                System.out.println("💰 " + label + " Balance: Not found");
            }
        } catch (Exception e) {
            System.out.println("💰 " + label + " Balance: Error - " + e.getMessage());
        }
    }
    
    private void displayTransferDetails(CashTransfer transfer) {
        System.out.println("\n📄 Transfer Details:");
        System.out.println("   Transaction ID: " + transfer.getTransactionId());
        System.out.println("   Amount: " + transfer.getFormattedAmount());
        System.out.println("   From: " + transfer.getTransferFromAccountNo());
        System.out.println("   To: " + transfer.getTransferToAccountNo());
        System.out.println("   Status: " + transfer.getStatus());
        System.out.println("   Date: " + transfer.getTransactionDate());
        System.out.println("   Description: " + transfer.getDescription());
    }
    
    private String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private String getValidRecipientNumber() {
        // Try to get a valid recipient from test users
        Optional<UserAuthentication> recipientOpt = userDAO.findById(TEST_RECIPIENT_ID);
        return recipientOpt.map(UserAuthentication::getNumber).orElse(null);
    }
    
    private void createTestUserIfNotExists(int id, String name, String email, String number, String pin) {
        Optional<UserAuthentication> userOpt = userDAO.findById(id);
        if (!userOpt.isPresent()) {
            UserAuthentication user = new UserAuthentication(name, email, number, pin);
            user.setId(id);
            userDAO.save(user);
            System.out.println("✅ Created test user: " + name + " (" + number + ")");
        } else {
            System.out.println("ℹ️  Test user already exists: " + userOpt.get().getName());
        }
    }
    
    private void createTestBalanceIfNotExists(int userId, double amount) {
        Optional<Balance> balanceOpt = balanceDAO.findByUserId(userId);
        if (!balanceOpt.isPresent()) {
            Balance balance = new Balance(BigDecimal.valueOf(amount), userId);
            balanceDAO.create(balance);
            System.out.println("✅ Created balance for User " + userId + ": ₱" + String.format("%.2f", amount));
        } else {
            System.out.println("ℹ️  Balance already exists for User " + userId + ": " + balanceOpt.get().getFormattedAmount());
        }
    }
    
    // Test case helper class
    private static class TestCase {
        final String name;
        final String recipientMobile;
        final BigDecimal amount;
        final String description;
        
        TestCase(String name, String recipientMobile, BigDecimal amount, String description) {
            this.name = name;
            this.recipientMobile = recipientMobile;
            this.amount = amount;
            this.description = description;
        }
    }

}
