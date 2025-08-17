package com.tesdaciicc;

import com.tesdaciicc.service.CheckBalanceService;
import com.tesdaciicc.data.repository.BalanceDAO;
import com.tesdaciicc.data.util.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.math.BigDecimal;

public class SimpleBalanceTest {

  private static final Logger logger = LoggerFactory.getLogger(SimpleBalanceTest.class);

  public static void main(String[] args) {
    System.out.println("=== Testing Your Existing Balance Setup ===\n");

    try {
      // Test 1: Check if Balance table exists and has data
      testExistingBalanceTable();

      // Test 2: Test the new DAO with existing data
      testBalanceDAO();

      // Test 3: Test the CheckBalanceService
      testCheckBalanceService();

      System.out.println("\n✅ All tests completed!");

    } catch (Exception e) {
      System.err.println("❌ Test failed: " + e.getMessage());
      logger.error("Test failed", e);
      e.printStackTrace();
    }
  }

  private static void testExistingBalanceTable() throws SQLException {
    System.out.println("📊 Testing Existing Balance Table...");

    String query = """
        SELECT
            COUNT(*) as total_records,
            COALESCE(AVG(amount), 0) as avg_amount,
            COALESCE(MIN(amount), 0) as min_amount,
            COALESCE(MAX(amount), 0) as max_amount
        FROM balance
        """;

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        int totalRecords = rs.getInt("total_records");
        double avgAmount = rs.getDouble("avg_amount");
        double minAmount = rs.getDouble("min_amount");
        double maxAmount = rs.getDouble("max_amount");

        System.out.printf("   Total Records: %d%n", totalRecords);
        System.out.printf("   Average Balance: ₱%.2f%n", avgAmount);
        System.out.printf("   Min Balance: ₱%.2f%n", minAmount);
        System.out.printf("   Max Balance: ₱%.2f%n", maxAmount);

        if (totalRecords > 0) {
          System.out.println("   ✅ Balance table has data");
          showSampleBalanceData();
        } else {
          System.out.println("   ⚠️  Balance table is empty");
          System.out.println("   💡 Run your FreshDatabaseSetup.java to populate it");
        }
      }
    }

    System.out.println();
  }

  private static void showSampleBalanceData() throws SQLException {
    System.out.println("   📋 Sample Balance Data:");

    String query = """
        SELECT b.id, b.user_ID, b.amount, u.name
        FROM balance b
        LEFT JOIN users u ON b.user_ID = u.id
        LIMIT 5
        """;

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()) {

      System.out.printf("      %-3s %-7s %-15s %-10s%n", "ID", "UserID", "Name", "Amount");
      System.out.println("      " + "-".repeat(40));

      while (rs.next()) {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_ID");
        double amount = rs.getDouble("amount");
        String name = rs.getString("name");

        System.out.printf("      %-3d %-7d %-15s ₱%,.2f%n",
            id, userId, (name != null ? name : "Unknown"), amount);
      }
    }
  }

  private static void testBalanceDAO() {
    System.out.println("🔧 Testing BalanceDAO...");

    BalanceDAO balanceDAO = new BalanceDAO();

    // Test finding all balances
    var allBalances = balanceDAO.findAll();
    System.out.printf("   Found %d balance records%n", allBalances.size());

    if (!allBalances.isEmpty()) {
      // Test finding specific user balance
      int testUserId = allBalances.get(0).getUserId();
      var userBalance = balanceDAO.findByUserId(testUserId);

      if (userBalance.isPresent()) {
        System.out.printf("   ✅ Found balance for user %d: %s%n",
            testUserId, userBalance.get().getFormattedAmount());
      } else {
        System.out.printf("   ❌ Could not find balance for user %d%n", testUserId);
      }
    }

    System.out.println();
  }

  private static void testCheckBalanceService() {
    System.out.println("⚙️  Testing CheckBalanceService...");

    CheckBalanceService balanceService = new CheckBalanceService();

    // Get first user to test with
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT user_ID FROM balance LIMIT 1");
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        int testUserId = rs.getInt("user_ID");

        // Test balance check
        var balance = balanceService.checkBalance(testUserId);
        if (balance.isPresent()) {
          System.out.printf("   ✅ Service Test: User %d has balance: ₱%,.2f%n",
              testUserId, balance.get());
        } else {
          System.out.printf("   ❌ Service Test: No balance found for user %d%n", testUserId);
        }

        // Test formatted balance
        String formatted = balanceService.getFormattedBalance(testUserId);
        System.out.printf("   📊 Formatted Balance: %s%n", formatted);

        // Test sufficient balance check
        boolean sufficient = balanceService.hasSufficientBalance(testUserId, 100.0);
        System.out.printf("   💳 Sufficient for ₱100: %s%n",
            sufficient ? "✅ YES" : "❌ NO");

      } else {
        System.out.println("   ⚠️  No balance records found to test with");
      }

    } catch (SQLException e) {
      System.err.println("   ❌ Error testing service: " + e.getMessage());
      logger.error("Error testing CheckBalanceService", e);
    }

    System.out.println();
  }
}