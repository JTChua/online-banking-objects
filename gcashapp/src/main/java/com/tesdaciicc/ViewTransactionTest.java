package com.tesdaciicc;

import com.tesdaciicc.service.TransactionsService;
import com.tesdaciicc.service.TransactionsService.TransactionStatistics;
import com.tesdaciicc.model.Transactions;
import com.tesdaciicc.data.repository.TransactionsDAO;
import com.tesdaciicc.data.repository.UserDAO;
import com.tesdaciicc.model.UserAuthentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Standalone test class for Transactions functionality
 * Tests the viewAll, viewUserAll, and viewTransaction methods
 */
public class ViewTransactionTest {
    
    private final TransactionsService transactionsService;
    private final TransactionsDAO transactionsDAO;
    private final UserDAO userDAO;
    private final Scanner scanner;
    
    // Test constants
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ViewTransactionTest() {
        this.transactionsService = new TransactionsService();
        this.transactionsDAO = new TransactionsDAO();
        this.userDAO = new UserDAO();
        this.scanner = new Scanner(System.in);
    }
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    VIEW TRANSACTIONS TEST SUITE        ");
        System.out.println("==========================================");
        
        ViewTransactionTest test = new ViewTransactionTest();
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
                    testViewAllTransactions();
                    break;
                case "2":
                    testViewUserAllTransactions();
                    break;
                case "3":
                    testViewSingleTransaction();
                    break;
                case "4":
                    testTransactionStatistics();
                    break;
                case "5":
                    testPaginatedTransactions();
                    break;
                case "6":
                    testSearchTransactions();
                    break;
                case "7":
                    testDateRangeTransactions();
                    break;
                case "8":
                    testRecentTransactions();
                    break;
                case "9":
                    testTransactionCounts();
                    break;
                case "10":
                    testEdgeCases();
                    break;
                case "11":
                    displaySystemInfo();
                    break;
                case "12":
                    displaySampleUsers();
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
        System.out.println("          VIEW TRANSACTIONS MENU        ");
        System.out.println("==========================================");
        System.out.println("1.  📄 View All Transactions");
        System.out.println("2.  👤 View User Transactions");
        System.out.println("3.  🔍 View Single Transaction");
        System.out.println("4.  📊 Transaction Statistics");
        System.out.println("5.  📖 Paginated Transactions");
        System.out.println("6.  🔎 Search Transactions");
        System.out.println("7.  📅 Date Range Transactions");
        System.out.println("8.  🕐 Recent Transactions");
        System.out.println("9.  🔢 Transaction Counts");
        System.out.println("10. ⚠️  Edge Cases Test");
        System.out.println("11. ℹ️  System Information");
        System.out.println("12. 👥 Sample Users");
        System.out.println("0.  🚪 Exit");
        System.out.println("==========================================");
    }
    
    /**
     * Test 1: View All Transactions
     */
    public void testViewAllTransactions() {
        System.out.println("\n📄 VIEW ALL TRANSACTIONS TEST");
        System.out.println("==============================");
        
        try {
            System.out.println("⏳ Retrieving all transactions...");
            List<Transactions> transactions = transactionsService.viewAll();
            
            System.out.println("✅ Retrieved " + transactions.size() + " transactions");
            
            if (transactions.isEmpty()) {
                System.out.println("ℹ️  No transactions found in the database.");
                return;
            }
            
            // Display summary
            displayTransactionSummary(transactions, "All Transactions");
            
            // Display first few transactions
            System.out.println("\n📋 Sample Transactions (first 10):");
            displayTransactionList(transactions.subList(0, Math.min(10, transactions.size())));
            
            // Ask if user wants to see more details
            String choice = getUserInput("View detailed list of all transactions? (y/N): ");
            if (choice.toLowerCase().startsWith("y")) {
                displayDetailedTransactionList(transactions);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error during view all transactions test: " + e.getMessage());
        }
    }
    
    /**
     * Test 2: View User Transactions
     */
    public void testViewUserAllTransactions() {
        System.out.println("\n👤 VIEW USER TRANSACTIONS TEST");
        System.out.println("===============================");
        
        try {
            // Display available users
            displaySampleUsers();
            
            String userIdStr = getUserInput("Enter User ID to view transactions: ");
            int userId = Integer.parseInt(userIdStr);
            
            System.out.println("⏳ Retrieving transactions for user " + userId + "...");
            List<Transactions> transactions = transactionsService.viewUserAll(userId);
            
            System.out.println("✅ Retrieved " + transactions.size() + " transactions for user " + userId);
            
            if (transactions.isEmpty()) {
                System.out.println("ℹ️  No transactions found for user " + userId);
                return;
            }
            
            // Display summary
            displayTransactionSummary(transactions, "User " + userId + " Transactions");
            
            // Display transactions
            displayDetailedTransactionList(transactions);
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid user ID format. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error during view user transactions test: " + e.getMessage());
        }
    }
    
    /**
     * Test 3: View Single Transaction
     */
    public void testViewSingleTransaction() {
        System.out.println("\n🔍 VIEW SINGLE TRANSACTION TEST");
        System.out.println("===============================");
        
        try {
            String transactionIdStr = getUserInput("Enter Transaction ID to view: ");
            int transactionId = Integer.parseInt(transactionIdStr);
            
            System.out.println("⏳ Retrieving transaction " + transactionId + "...");
            Optional<Transactions> transactionOpt = transactionsService.viewTransaction(transactionId);
            
            if (transactionOpt.isPresent()) {
                Transactions transaction = transactionOpt.get();
                System.out.println("✅ Transaction found!");
                
                // Display detailed transaction information
                displaySingleTransactionDetails(transaction);
                
            } else {
                System.out.println("❌ Transaction with ID " + transactionId + " not found.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid transaction ID format. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error during view single transaction test: " + e.getMessage());
        }
    }
    
    /**
     * Test 4: Transaction Statistics
     */
    public void testTransactionStatistics() {
        System.out.println("\n📊 TRANSACTION STATISTICS TEST");
        System.out.println("===============================");
        
        try {
            System.out.println("⏳ Generating statistics...");
            
            // All transactions statistics
            TransactionStatistics allStats = transactionsService.getAllTransactionStatistics();
            System.out.println("\n📈 ALL TRANSACTIONS STATISTICS:");
            System.out.println("================================");
            displayStatistics(allStats);
            
            // User-specific statistics
            String choice = getUserInput("\nGenerate user-specific statistics? (y/N): ");
            if (choice.toLowerCase().startsWith("y")) {
                displaySampleUsers();
                String userIdStr = getUserInput("Enter User ID: ");
                int userId = Integer.parseInt(userIdStr);
                
                TransactionStatistics userStats = transactionsService.getUserTransactionStatistics(userId);
                System.out.println("\n👤 USER " + userId + " STATISTICS:");
                System.out.println("=====================");
                displayStatistics(userStats);
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid user ID format.");
        } catch (Exception e) {
            System.out.println("❌ Error during statistics test: " + e.getMessage());
        }
    }
    
    /**
     * Test 5: Paginated Transactions
     */
    public void testPaginatedTransactions() {
        System.out.println("\n📖 PAGINATED TRANSACTIONS TEST");
        System.out.println("===============================");
        
        try {
            String pageSizeStr = getUserInput("Enter page size (default 10): ");
            int pageSize = pageSizeStr.isEmpty() ? DEFAULT_PAGE_SIZE : Integer.parseInt(pageSizeStr);
            
            int currentPage = 1;
            
            while (true) {
                System.out.println("\n⏳ Loading page " + currentPage + "...");
                List<Transactions> transactions = transactionsService.viewAllPaginated(currentPage, pageSize);
                
                if (transactions.isEmpty()) {
                    System.out.println("📄 No more transactions to display.");
                    break;
                }
                
                System.out.println("📄 Page " + currentPage + " (" + transactions.size() + " transactions):");
                displayTransactionList(transactions);
                
                String choice = getUserInput("Next page (n), Previous page (p), Quit (q): ");
                switch (choice.toLowerCase()) {
                    case "n":
                    case "next":
                        currentPage++;
                        break;
                    case "p":
                    case "prev":
                    case "previous":
                        if (currentPage > 1) {
                            currentPage--;
                        }
                        break;
                    case "q":
                    case "quit":
                        return;
                    default:
                        System.out.println("❌ Invalid choice.");
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid page size format.");
        } catch (Exception e) {
            System.out.println("❌ Error during pagination test: " + e.getMessage());
        }
    }
    
    /**
     * Test 6: Search Transactions
     */
    public void testSearchTransactions() {
        System.out.println("\n🔎 SEARCH TRANSACTIONS TEST");
        System.out.println("============================");
        
        try {
            String searchTerm = getUserInput("Enter search term (transaction name): ");
            
            System.out.println("⏳ Searching transactions...");
            List<Transactions> results = transactionsService.searchTransactionsByName(searchTerm);
            
            System.out.println("✅ Found " + results.size() + " transactions matching '" + searchTerm + "'");
            
            if (results.isEmpty()) {
                System.out.println("ℹ️  No transactions found matching the search term.");
            } else {
                displayTransactionList(results);
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error during search test: " + e.getMessage());
        }
    }
    
    /**
     * Test 7: Date Range Transactions
     */
    public void testDateRangeTransactions() {
        System.out.println("\n📅 DATE RANGE TRANSACTIONS TEST");
        System.out.println("================================");
        
        try {
            System.out.println("Enter date range (format: yyyy-MM-dd HH:mm:ss)");
            String startDateStr = getUserInput("Start date: ");
            String endDateStr = getUserInput("End date: ");
            
            LocalDateTime startDate = LocalDateTime.parse(startDateStr.replace(" ", "T"));
            LocalDateTime endDate = LocalDateTime.parse(endDateStr.replace(" ", "T"));
            
            System.out.println("⏳ Retrieving transactions in date range...");
            List<Transactions> results = transactionsService.getTransactionsByDateRange(startDate, endDate);
            
            System.out.println("✅ Found " + results.size() + " transactions in the specified date range");
            
            if (results.isEmpty()) {
                System.out.println("ℹ️  No transactions found in the specified date range.");
            } else {
                displayTransactionList(results);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error during date range test: " + e.getMessage());
            System.out.println("💡 Tip: Use format 'yyyy-MM-dd HH:mm:ss' (e.g., '2023-01-01 00:00:00')");
        }
    }
    
    /**
     * Test 8: Recent Transactions
     */
    public void testRecentTransactions() {
        System.out.println("\n🕐 RECENT TRANSACTIONS TEST");
        System.out.println("============================");
        
        try {
            System.out.println("⏳ Retrieving recent transactions (last 30 days)...");
            List<Transactions> results = transactionsService.getRecentTransactions();
            
            System.out.println("✅ Found " + results.size() + " recent transactions");
            
            if (results.isEmpty()) {
                System.out.println("ℹ️  No recent transactions found.");
            } else {
                displayTransactionSummary(results, "Recent Transactions (Last 30 days)");
                displayTransactionList(results.subList(0, Math.min(10, results.size())));
            }
            
            // Test recent user transactions
            String choice = getUserInput("Test recent transactions for specific user? (y/N): ");
            if (choice.toLowerCase().startsWith("y")) {
                displaySampleUsers();
                String userIdStr = getUserInput("Enter User ID: ");
                int userId = Integer.parseInt(userIdStr);
                
                List<Transactions> userRecent = transactionsService.getRecentUserTransactions(userId);
                System.out.println("✅ Found " + userRecent.size() + " recent transactions for user " + userId);
                
                if (!userRecent.isEmpty()) {
                    displayTransactionList(userRecent);
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid user ID format.");
        } catch (Exception e) {
            System.out.println("❌ Error during recent transactions test: " + e.getMessage());
        }
    }
    
    /**
     * Test 9: Transaction Counts
     */
    public void testTransactionCounts() {
        System.out.println("\n🔢 TRANSACTION COUNTS TEST");
        System.out.println("===========================");
        
        try {
            System.out.println("⏳ Getting transaction counts...");
            
            // Total count
            long totalCount = transactionsService.getTotalTransactionCount();
            System.out.println("📊 Total Transactions: " + totalCount);
            
            // User-specific count
            displaySampleUsers();
            String userIdStr = getUserInput("Enter User ID to get count: ");
            int userId = Integer.parseInt(userIdStr);
            
            long userCount = transactionsService.getUserTransactionCount(userId);
            System.out.println("👤 User " + userId + " Transactions: " + userCount);
            
            // Calculate percentage
            if (totalCount > 0) {
                double percentage = (double) userCount / totalCount * 100;
                System.out.println("📈 User represents " + String.format("%.2f", percentage) + "% of all transactions");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid user ID format.");
        } catch (Exception e) {
            System.out.println("❌ Error during counts test: " + e.getMessage());
        }
    }
    
    /**
     * Test 10: Edge Cases
     */
    public void testEdgeCases() {
        System.out.println("\n⚠️  EDGE CASES TEST");
        System.out.println("===================");
        
        // Test invalid user IDs
        System.out.println("🧪 Testing invalid user IDs:");
        testInvalidUserId(0);
        testInvalidUserId(-1);
        testInvalidUserId(999999);
        
        // Test invalid transaction IDs
        System.out.println("\n🧪 Testing invalid transaction IDs:");
        testInvalidTransactionId(0);
        testInvalidTransactionId(-1);
        testInvalidTransactionId(999999);
        
        // Test empty/null search terms
        System.out.println("\n🧪 Testing invalid search terms:");
        testInvalidSearchTerm("");
        testInvalidSearchTerm("   ");
        
        // Test invalid date ranges
        System.out.println("\n🧪 Testing invalid date ranges:");
        testInvalidDateRange();
        
        System.out.println("\n✅ Edge cases test completed!");
    }
    
    // Helper methods for edge case testing
    
    private void testInvalidUserId(int userId) {
        try {
            transactionsService.viewUserAll(userId);
            System.out.println("❌ Expected exception for user ID: " + userId);
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly handled invalid user ID " + userId + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️  Unexpected exception for user ID " + userId + ": " + e.getMessage());
        }
    }
    
    private void testInvalidTransactionId(int transactionId) {
        try {
            transactionsService.viewTransaction(transactionId);
            System.out.println("❌ Expected exception for transaction ID: " + transactionId);
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly handled invalid transaction ID " + transactionId + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️  Unexpected exception for transaction ID " + transactionId + ": " + e.getMessage());
        }
    }
    
    private void testInvalidSearchTerm(String searchTerm) {
        try {
            transactionsService.searchTransactionsByName(searchTerm);
            System.out.println("❌ Expected exception for search term: '" + searchTerm + "'");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly handled invalid search term '" + searchTerm + "': " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️  Unexpected exception for search term '" + searchTerm + "': " + e.getMessage());
        }
    }
    
    private void testInvalidDateRange() {
        try {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = endDate.plusDays(1); // Invalid: start after end
            transactionsService.getTransactionsByDateRange(startDate, endDate);
            System.out.println("❌ Expected exception for invalid date range");
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Correctly handled invalid date range: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️  Unexpected exception for invalid date range: " + e.getMessage());
        }
    }
    
    /**
     * Display system information
     */
    public void displaySystemInfo() {
        System.out.println("\nℹ️  SYSTEM INFORMATION");
        System.out.println("======================");
        
        try {
            long totalTransactions = transactionsService.getTotalTransactionCount();
            System.out.println("📊 Total Transactions in Database: " + totalTransactions);
            
            if (totalTransactions > 0) {
                // Get sample of transactions to analyze
                List<Transactions> sampleTransactions = transactionsService.viewAllPaginated(1, 100);
                
                if (!sampleTransactions.isEmpty()) {
                    System.out.println("📈 Sample Analysis (first 100 transactions):");
                    
                    long transferCount = sampleTransactions.stream().mapToLong(t -> t.isTransfer() ? 1 : 0).sum();
                    long cashInCount = sampleTransactions.stream().mapToLong(t -> t.isCashIn() ? 1 : 0).sum();
                    long cashOutCount = sampleTransactions.stream().mapToLong(t -> t.isCashOut() ? 1 : 0).sum();
                    
                    System.out.println("   💸 Transfers: " + transferCount);
                    System.out.println("   💰 Cash-In: " + cashInCount);
                    System.out.println("   💳 Cash-Out: " + cashOutCount);
                    System.out.println("   🔄 Other: " + (sampleTransactions.size() - transferCount - cashInCount - cashOutCount));
                    
                    // Date range of transactions
                    LocalDateTime oldestDate = sampleTransactions.stream()
                        .map(Transactions::getTransactionDate)
                        .min(LocalDateTime::compareTo)
                        .orElse(null);
                    
                    LocalDateTime newestDate = sampleTransactions.stream()
                        .map(Transactions::getTransactionDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(null);
                    
                    if (oldestDate != null && newestDate != null) {
                        System.out.println("📅 Date Range:");
                        System.out.println("   Oldest: " + oldestDate.format(DATE_FORMATTER));
                        System.out.println("   Newest: " + newestDate.format(DATE_FORMATTER));
                    }
                }
            } else {
                System.out.println("ℹ️  No transactions found in the database.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error getting system information: " + e.getMessage());
        }
    }
    
    /**
     * Display sample users for reference
     */
    public void displaySampleUsers() {
        System.out.println("\n👥 SAMPLE USERS");
        System.out.println("===============");
        
        try {
            // Get first few users from database
            for (int i = 1; i <= 5; i++) {
                Optional<UserAuthentication> userOpt = userDAO.findById(i);
                if (userOpt.isPresent()) {
                    UserAuthentication user = userOpt.get();
                    long transactionCount = transactionsService.getUserTransactionCount(i);
                    System.out.println("ID: " + user.getId() + " | Name: " + user.getName() + 
                                     " | Mobile: " + user.getNumber() + " | Transactions: " + transactionCount);
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error displaying sample users: " + e.getMessage());
        }
    }
    
    // Display helper methods
    
    private void displayTransactionSummary(List<Transactions> transactions, String title) {
        System.out.println("\n📋 " + title.toUpperCase() + " SUMMARY:");
        System.out.println("Total Transactions: " + transactions.size());
        
        if (transactions.isEmpty()) {
            return;
        }
        
        // Calculate totals
        BigDecimal totalAmount = transactions.stream()
            .map(Transactions::getTransactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageAmount = totalAmount.divide(
            BigDecimal.valueOf(transactions.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        // Count by type
        long transferCount = transactions.stream().mapToLong(t -> t.isTransfer() ? 1 : 0).sum();
        long cashInCount = transactions.stream().mapToLong(t -> t.isCashIn() ? 1 : 0).sum();
        long cashOutCount = transactions.stream().mapToLong(t -> t.isCashOut() ? 1 : 0).sum();
        long otherCount = transactions.size() - transferCount - cashInCount - cashOutCount;
        
        System.out.println("Total Amount: ₱" + String.format("%,.2f", totalAmount));
        System.out.println("Average Amount: ₱" + String.format("%,.2f", averageAmount));
        System.out.println("Transaction Types:");
        System.out.println("  💸 Transfers: " + transferCount);
        System.out.println("  💰 Cash-In: " + cashInCount);
        System.out.println("  💳 Cash-Out: " + cashOutCount);
        System.out.println("  🔄 Other: " + otherCount);
    }
    
    private void displayTransactionList(List<Transactions> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-5s %-8s %-15s %-25s %-10s %-15s %-15s %-20s%n",
                         "ID", "User ID", "Amount", "Name", "Type", "From", "To", "Date");
        System.out.println("=".repeat(120));
        
        for (Transactions t : transactions) {
            System.out.printf("%-5d %-8d %-15s %-25s %-10s %-15s %-15s %-20s%n",
                             t.getTransactionId(),
                             t.getUserId(),
                             t.getFormattedAmount(),
                             truncateString(t.getTransactionName(), 24),
                             t.getTransactionType(),
                             truncateString(t.getTransferFromAccountNo(), 14),
                             truncateString(t.getTransferToAccountNo(), 14),
                             t.getTransactionDate().format(DATE_FORMATTER));
        }
    }
    
    private void displayDetailedTransactionList(List<Transactions> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions to display.");
            return;
        }
        
        System.out.println("\n📄 DETAILED TRANSACTION LIST:");
        
        for (int i = 0; i < transactions.size(); i++) {
            Transactions t = transactions.get(i);
            System.out.println("\n" + "-".repeat(60));
            System.out.println("Transaction #" + (i + 1));
            System.out.println("-".repeat(60));
            displaySingleTransactionDetails(t);
        }
    }
    
    private void displaySingleTransactionDetails(Transactions transaction) {
        System.out.println("🆔 Transaction ID: " + transaction.getTransactionId());
        System.out.println("👤 User ID: " + transaction.getUserId());
        System.out.println("💰 Amount: " + transaction.getFormattedAmount());
        System.out.println("📝 Name: " + transaction.getTransactionName());
        System.out.println("🏷️  Type: " + transaction.getTransactionType());
        System.out.println("📅 Date: " + transaction.getTransactionDate().format(DATE_FORMATTER));
        System.out.println("🔢 Account Number: " + safeString(transaction.getAccountNumber()));
        System.out.println("📤 From Account: " + safeString(transaction.getTransferFromAccountNo()));
        System.out.println("📥 To Account: " + safeString(transaction.getTransferToAccountNo()));
        
        // Additional analysis
        if (transaction.isTransfer()) {
            System.out.println("🔄 Transfer Details:");
            System.out.println("   From: " + transaction.getTransferFromAccountNo());
            System.out.println("   To: " + transaction.getTransferToAccountNo());
        }
        
        if (transaction.isPositiveAmount()) {
            System.out.println("✅ Amount is positive");
        } else {
            System.out.println("❌ Amount is not positive");
        }
    }
    
    private void displayStatistics(TransactionStatistics stats) {
        System.out.println("📊 " + stats.getLabel());
        System.out.println("   Total Count: " + stats.getTotalCount());
        System.out.println("   Total Amount: " + stats.getFormattedTotalAmount());
        System.out.println("   Average Amount: " + stats.getFormattedAverageAmount());
        System.out.println("   Maximum Amount: " + stats.getFormattedMaxAmount());
        System.out.println("   Minimum Amount: " + stats.getFormattedMinAmount());
        System.out.println("   Transaction Types:");
        System.out.println("     💸 Transfers: " + stats.getTransferCount());
        System.out.println("     💰 Cash-In: " + stats.getCashInCount());
        System.out.println("     💳 Cash-Out: " + stats.getCashOutCount());
        System.out.println("     🔄 Other: " + stats.getOtherCount());
    }
    
    // Utility methods
    
    private String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private String truncateString(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private String safeString(String str) {
        return str != null ? str : "N/A";
    }
    
    /**
     * Quick test method to verify all main functionalities
     */
    public void runQuickTest() {
        System.out.println("\n🚀 QUICK TEST - VERIFYING ALL FUNCTIONALITIES");
        System.out.println("==============================================");
        
        try {
            // Test 1: View All
            System.out.println("1️⃣  Testing viewAll()...");
            List<Transactions> allTransactions = transactionsService.viewAll();
            System.out.println("   ✅ Retrieved " + allTransactions.size() + " transactions");
            
            // Test 2: View User All (if transactions exist)
            if (!allTransactions.isEmpty()) {
                int sampleUserId = allTransactions.get(0).getUserId();
                System.out.println("2️⃣  Testing viewUserAll(" + sampleUserId + ")...");
                List<Transactions> userTransactions = transactionsService.viewUserAll(sampleUserId);
                System.out.println("   ✅ Retrieved " + userTransactions.size() + " user transactions");
                
                // Test 3: View Transaction (if user transactions exist)
                if (!userTransactions.isEmpty()) {
                    int sampleTransactionId = userTransactions.get(0).getTransactionId();
                    System.out.println("3️⃣  Testing viewTransaction(" + sampleTransactionId + ")...");
                    Optional<Transactions> singleTransaction = transactionsService.viewTransaction(sampleTransactionId);
                    System.out.println("   ✅ Retrieved transaction: " + singleTransaction.isPresent());
                }
            }
            
            // Test 4: Statistics
            System.out.println("4️⃣  Testing statistics...");
            TransactionStatistics stats = transactionsService.getAllTransactionStatistics();
            System.out.println("   ✅ Generated statistics for " + stats.getTotalCount() + " transactions");
            
            System.out.println("\n🎉 Quick test completed successfully!");
            
        } catch (Exception e) {
            System.out.println("❌ Quick test failed: " + e.getMessage());
        }
    }
}
