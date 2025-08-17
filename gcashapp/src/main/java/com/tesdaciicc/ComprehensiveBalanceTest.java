package com.tesdaciicc;

import com.tesdaciicc.service.CheckBalanceService;
import com.tesdaciicc.data.repository.BalanceDAO;
import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.data.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ComprehensiveBalanceTest {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveBalanceTest.class);
    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=== Comprehensive Balance Test Suite ===\n");

        try {
            // Initialize database with fresh data
            initializeTestDatabase();

            // Run all test categories
            testDatabaseIntegrity();
            testBalanceDAOOperations(); 
            testCheckBalanceService();
            testEdgeCases();
            testPerformance();

            // Summary
            printTestSummary();

        } catch (Exception e) {
            System.err.println("❌ Test suite failed: " + e.getMessage());
            logger.error("Test suite failed", e);
            e.printStackTrace();
        }
    }

    private static void initializeTestDatabase() {
        System.out.println("🔄 Initializing test database...");
        DatabaseUtil.dropAllTables();
        boolean result = DatabaseUtil.initializeDatabase();
        
        if (result) {
            System.out.println("✅ Database initialized successfully\n");
        } else {
            throw new RuntimeException("Failed to initialize test database");
        }
    }

    // ==================== DATABASE INTEGRITY TESTS ====================
    
    private static void testDatabaseIntegrity() throws SQLException {
        System.out.println("📊 Testing Database Integrity...");

        // Test 1: Check table structure
        testTableStructure();
        
        // Test 2: Check data consistency
        testDataConsistency();
        
        // Test 3: Check foreign key constraints
        testForeignKeyConstraints();
        
        System.out.println();
    }

    private static void testTableStructure() throws SQLException {
        testsTotal++;
        System.out.println("   🔍 Testing table structure...");

        String query = """
            SELECT 
                COUNT(*) as balance_count,
                (SELECT COUNT(*) FROM users) as user_count
            FROM balance
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int balanceCount = rs.getInt("balance_count");
                int userCount = rs.getInt("user_count");

                System.out.printf("      Balance records: %d%n", balanceCount);
                System.out.printf("      User records: %d%n", userCount);

                if (balanceCount > 0 && userCount > 0) {
                    System.out.println("      ✅ Tables have data");
                    testsPassed++;
                } else {
                    System.out.println("      ❌ Tables are empty");
                }
            }
        }
    }

    private static void testDataConsistency() throws SQLException {
        testsTotal++;
        System.out.println("   🔗 Testing data consistency...");

        String query = """
            SELECT 
                COUNT(*) as total_balances,
                COUNT(CASE WHEN user_ID IS NOT NULL THEN 1 END) as valid_user_refs,
                COUNT(CASE WHEN amount >= 0 THEN 1 END) as non_negative_amounts
            FROM balance
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int totalBalances = rs.getInt("total_balances");
                int validUserRefs = rs.getInt("valid_user_refs");
                int nonNegativeAmounts = rs.getInt("non_negative_amounts");

                boolean consistent = (totalBalances == validUserRefs && totalBalances == nonNegativeAmounts);
                
                System.out.printf("      Total balances: %d%n", totalBalances);
                System.out.printf("      Valid user references: %d%n", validUserRefs);
                System.out.printf("      Non-negative amounts: %d%n", nonNegativeAmounts);

                if (consistent) {
                    System.out.println("      ✅ Data is consistent");
                    testsPassed++;
                } else {
                    System.out.println("      ❌ Data inconsistencies found");
                }
            }
        }
    }

    private static void testForeignKeyConstraints() throws SQLException {
        testsTotal++;
        System.out.println("   🔑 Testing foreign key constraints...");

        String query = """
            SELECT 
                b.user_ID,
                u.id as actual_user_id,
                u.name
            FROM balance b
            LEFT JOIN users u ON b.user_ID = u.id
            WHERE u.id IS NULL
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            boolean hasOrphans = rs.next();
            
            if (!hasOrphans) {
                System.out.println("      ✅ All balance records have valid user references");
                testsPassed++;
            } else {
                System.out.println("      ❌ Found orphaned balance records");
                do {
                    System.out.printf("         Orphan user_ID: %d%n", rs.getInt("user_ID"));
                } while (rs.next());
            }
        }
    }

    // ==================== BALANCE DAO TESTS ====================

    private static void testBalanceDAOOperations() {
        System.out.println("🔧 Testing BalanceDAO Operations...");

        BalanceDAO balanceDAO = new BalanceDAO();

        // Test basic operations
        testFindAllBalances(balanceDAO);
        testFindByUserId(balanceDAO);
        testFindNonExistentUser(balanceDAO);

        System.out.println();
    }

    private static void testFindAllBalances(BalanceDAO balanceDAO) {
        testsTotal++;
        System.out.println("   📋 Testing findAll()...");

        var allBalances = balanceDAO.findAll();
        System.out.printf("      Found %d balance records%n", allBalances.size());

        if (allBalances.size() > 0) {
            System.out.println("      ✅ findAll() returns data");
            testsPassed++;
            
            // Show sample data
            System.out.println("      Sample records:");
            allBalances.stream().limit(3).forEach(balance -> 
                System.out.printf("         User %d: %s%n", 
                    balance.getUserId(), balance.getFormattedAmount()));
        } else {
            System.out.println("      ❌ findAll() returned no data");
        }
    }

    private static void testFindByUserId(BalanceDAO balanceDAO) {
        testsTotal++;
        System.out.println("   🔍 Testing findByUserId()...");

        // Test with known user ID (should exist from test data)
        int testUserId = 1;
        var userBalance = balanceDAO.findByUserId(testUserId);

        if (userBalance.isPresent()) {
            System.out.printf("      ✅ Found balance for user %d: %s%n",
                testUserId, userBalance.get().getFormattedAmount());
            testsPassed++;
        } else {
            System.out.printf("      ❌ Could not find balance for user %d%n", testUserId);
        }
    }

    private static void testFindNonExistentUser(BalanceDAO balanceDAO) {
        testsTotal++;
        System.out.println("   🚫 Testing findByUserId() with non-existent user...");

        int nonExistentUserId = 99999;
        var userBalance = balanceDAO.findByUserId(nonExistentUserId);

        if (userBalance.isEmpty()) {
            System.out.printf("      ✅ Correctly returned empty for non-existent user %d%n", 
                nonExistentUserId);
            testsPassed++;
        } else {
            System.out.printf("      ❌ Unexpectedly found balance for non-existent user %d%n", 
                nonExistentUserId);
        }
    }

    // ==================== SERVICE TESTS ====================

    private static void testCheckBalanceService() {
        System.out.println("⚙️  Testing CheckBalanceService...");

        CheckBalanceService balanceService = new CheckBalanceService();

        testServiceCheckBalance(balanceService);
        testServiceFormattedBalance(balanceService);
        testServiceSufficientBalance(balanceService);

        System.out.println();
    }

    private static void testServiceCheckBalance(CheckBalanceService balanceService) {
        testsTotal++;
        System.out.println("   💰 Testing checkBalance()...");

        int testUserId = 1;
        var balance = balanceService.checkBalance(testUserId);
        
        if (balance.isPresent()) {
            System.out.printf("      ✅ Service found balance for user %d: ₱%,.2f%n",
                testUserId, balance.get());
            testsPassed++;
        } else {
            System.out.printf("      ❌ Service could not find balance for user %d%n", testUserId);
        }
    }

    private static void testServiceFormattedBalance(CheckBalanceService balanceService) {
        testsTotal++;
        System.out.println("   📊 Testing getFormattedBalance()...");

        int testUserId = 1;
        String formatted = balanceService.getFormattedBalance(testUserId);
        
        if (formatted != null && !formatted.isEmpty()) {
            System.out.printf("      ✅ Formatted balance: %s%n", formatted);
            testsPassed++;
        } else {
            System.out.println("      ❌ Failed to get formatted balance");
        }
    }

    private static void testServiceSufficientBalance(CheckBalanceService balanceService) {
        testsTotal += 2;
        System.out.println("   💳 Testing hasSufficientBalance()...");

        int testUserId = 1;
        
        // Test with small amount (should pass)
        boolean sufficientSmall = balanceService.hasSufficientBalance(testUserId, 100.0);
        System.out.printf("      Sufficient for ₱100: %s%n", 
            sufficientSmall ? "✅ YES" : "❌ NO");
        if (sufficientSmall) testsPassed++;

        // Test with large amount (should fail)
        boolean sufficientLarge = balanceService.hasSufficientBalance(testUserId, 1000000.0);
        System.out.printf("      Sufficient for ₱1,000,000: %s%n", 
            sufficientLarge ? "⚠️  YES" : "✅ NO (expected)");
        if (!sufficientLarge) testsPassed++;
    }

    // ==================== EDGE CASES ====================

    private static void testEdgeCases() {
        System.out.println("🎯 Testing Edge Cases...");

        testZeroBalance();
        testInvalidUserIds();
        testNullHandling();

        System.out.println();
    }

    private static void testZeroBalance() {
        testsTotal++;
        System.out.println("   🔢 Testing zero balance handling...");

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT user_ID FROM balance WHERE amount = 0.0 LIMIT 1");
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int zeroBalanceUserId = rs.getInt("user_ID");
                
                CheckBalanceService service = new CheckBalanceService();
                var balance = service.checkBalance(zeroBalanceUserId);
                
                if (balance.isPresent() && balance.get().doubleValue() == 0.0) {
                    System.out.printf("      ✅ Zero balance handled correctly for user %d%n", 
                        zeroBalanceUserId);
                    testsPassed++;
                } else {
                    System.out.println("      ❌ Zero balance not handled correctly");
                }
            } else {
                System.out.println("      ℹ️  No zero balance records found to test");
                testsPassed++; // Not an error
            }

        } catch (SQLException e) {
            System.out.println("      ❌ Error testing zero balance: " + e.getMessage());
        }
    }

    private static void testInvalidUserIds() {
        testsTotal += 2;
        System.out.println("   🚫 Testing invalid user IDs...");

        CheckBalanceService service = new CheckBalanceService();

        // Test negative user ID
        var negativeResult = service.checkBalance(-1);
        if (negativeResult.isEmpty()) {
            System.out.println("      ✅ Negative user ID handled correctly");
            testsPassed++;
        } else {
            System.out.println("      ❌ Negative user ID not handled correctly");
        }

        // Test zero user ID
        var zeroResult = service.checkBalance(0);
        if (zeroResult.isEmpty()) {
            System.out.println("      ✅ Zero user ID handled correctly");
            testsPassed++;
        } else {
            System.out.println("      ❌ Zero user ID not handled correctly");
        }
    }

    private static void testNullHandling() {
        testsTotal++;
        System.out.println("   🔄 Testing null/empty data handling...");

        BalanceDAO dao = new BalanceDAO();
        
        try {
            // This should not throw exceptions
            var allBalances = dao.findAll();
            var emptyResult = dao.findByUserId(99999);
            
            System.out.println("      ✅ Null/empty data handled gracefully");
            testsPassed++;
        } catch (Exception e) {
            System.out.println("      ❌ Exception in null handling: " + e.getMessage());
        }
    }

    // ==================== PERFORMANCE TESTS ====================

    private static void testPerformance() {
        System.out.println("⚡ Testing Performance...");

        testQueryPerformance();
        testBulkOperations();

        System.out.println();
    }

    private static void testQueryPerformance() {
        testsTotal++;
        System.out.println("   ⏱️  Testing query performance...");

        long startTime = System.currentTimeMillis();
        
        BalanceDAO dao = new BalanceDAO();
        CheckBalanceService service = new CheckBalanceService();
        
        // Perform multiple operations
        for (int i = 1; i <= 10; i++) {
            dao.findByUserId(i);
            service.checkBalance(i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("      ⏱️  20 operations completed in %d ms%n", duration);
        
        if (duration < 1000) { // Less than 1 second
            System.out.println("      ✅ Performance is acceptable");
            testsPassed++;
        } else {
            System.out.println("      ⚠️  Performance might need optimization");
        }
    }

    private static void testBulkOperations() {
        testsTotal++;
        System.out.println("   📊 Testing bulk operations...");

        long startTime = System.currentTimeMillis();
        
        BalanceDAO dao = new BalanceDAO();
        var allBalances = dao.findAll();
        
        // Process all balances
        double totalBalance = allBalances.stream()
            .mapToDouble(balance -> balance.getAmount().doubleValue())
            .sum();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.printf("      📊 Processed %d records in %d ms%n", 
            allBalances.size(), duration);
        System.out.printf("      💰 Total balance across all users: ₱%,.2f%n", totalBalance);
        
        if (duration < 500) { // Less than 0.5 seconds
            System.out.println("      ✅ Bulk operation performance is good");
            testsPassed++;
        } else {
            System.out.println("      ⚠️  Bulk operations might need optimization");
        }
    }

    // ==================== SUMMARY ====================

    private static void printTestSummary() {
        System.out.println("=".repeat(50));
        System.out.println("📋 TEST SUMMARY");
        System.out.println("=".repeat(50));
        
        System.out.printf("Tests Run: %d%n", testsTotal);
        System.out.printf("Tests Passed: %d%n", testsPassed);
        System.out.printf("Tests Failed: %d%n", (testsTotal - testsPassed));
        
        double successRate = (double) testsPassed / testsTotal * 100;
        System.out.printf("Success Rate: %.1f%%%n", successRate);
        
        if (testsPassed == testsTotal) {
            System.out.println("\n🎉 ALL TESTS PASSED! Your balance system is working perfectly!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Check the output above for details.");
        }
        
        System.out.println("=".repeat(50));
    }

}
