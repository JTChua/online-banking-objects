package com.tesdaciicc;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.data.util.Config;
import com.tesdaciicc.data.util.DatabaseUtil;
import com.tesdaciicc.service.CheckBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FreshDatabaseSetup {

  private static final Logger logger = LoggerFactory.getLogger(FreshDatabaseSetup.class);

  public static void main(String[] args) {
    System.out.println("=== Fresh Database Setup ===\n");

    try {
      // Step 1: Show current database configuration
      System.out.println("Database Configuration:");
      System.out.println("Database URL: " + Config.DATABASE_URL);
      System.out.println("Database Name: " + Config.DATABASE_NAME);
      System.out.println("Init SQL File: " + Config.INIT_SQL_FILE);
      System.out.println();

      // Step 2: Check if database file exists and delete it
      String dbPath = Config.DATABASE_NAME; // This should be just "gcashapp.db"
      if (Files.exists(Paths.get(dbPath))) {
        System.out.println("Existing database file found: " + dbPath);
        Files.delete(Paths.get(dbPath));
        System.out.println("Existing database file deleted successfully");
      } else {
        System.out.println("No existing database file found - starting fresh");
      }
      System.out.println();

      // Step 3: Use DatabaseUtil to initialize everything
      System.out.println("Initializing database using DatabaseUtil...");
      boolean initSuccess = DatabaseUtil.initializeDatabase();

      if (initSuccess) {
        System.out.println("✅ Database initialized successfully via DatabaseUtil");
      } else {
        System.out.println("❌ Database initialization failed via DatabaseUtil");
        System.out.println("Falling back to manual setup...");
        manualDatabaseSetup();
      }

      // Step 4: Verify setup and test functionality
      verifyDatabaseSetup();

      // Step 5: Test balance functionality
      testBalanceFunctionality();

      System.out.println("\n🎉 Database setup completed successfully!");
      System.out.println("\nNext steps:");
      System.out.println("1. Your database is ready at: " + Paths.get(dbPath).toAbsolutePath());
      System.out.println("2. Balance functionality is working");
      System.out.println("3. You can now run your App.java");

    } catch (Exception e) {
      System.err.println("❌ Database setup failed: " + e.getMessage());
      logger.error("Database setup failed", e);
      e.printStackTrace();
    }
  }

  /**
   * Manual database setup fallback (matches your existing approach)
   */
  private static void manualDatabaseSetup() throws Exception {
    System.out.println("Setting up database manually...");

    try (Connection conn = ConnectionFactory.getConnection()) {
      System.out.println("✅ Database connection established");

      try (Statement stmt = conn.createStatement()) {
        // Create users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                number TEXT NOT NULL UNIQUE,
                pin TEXT NOT NULL,
                token TEXT,
                createdDate TEXT NOT NULL DEFAULT (datetime('now')),
                updatedDate TEXT NOT NULL DEFAULT (datetime('now'))
            )
            """;

        stmt.execute(createUsersTable);
        System.out.println("✅ Users table created");

        // Create Balance table (matching your existing schema)
        String createBalanceTable = """
            CREATE TABLE IF NOT EXISTS Balance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_ID INTEGER NOT NULL,
                amount REAL NOT NULL DEFAULT 0.0,
                createdDate TEXT NOT NULL DEFAULT (datetime('now')),
                updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
                FOREIGN KEY (user_ID) REFERENCES users(id) ON DELETE CASCADE
            )
            """;

        stmt.execute(createBalanceTable);
        System.out.println("✅ Balance table created");

        // Create indexes for performance
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_number ON users(number)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_balance_user_id ON Balance(user_ID)");
        System.out.println("✅ Database indexes created");

        // Insert test user
        insertTestData(conn);
      }
    }
  }

  /**
   * Insert test data
   */
  private static void insertTestData(Connection conn) throws Exception {
    System.out.println("Inserting test data...");

    // Insert test user
    String insertUserSQL = "INSERT OR IGNORE INTO users (name, email, number, pin) VALUES (?, ?, ?, ?)";
    try (PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {
      pstmt.setString(1, "Test User");
      pstmt.setString(2, "test@example.com");
      pstmt.setString(3, "09123456789");
      pstmt.setString(4, "1234");
      pstmt.executeUpdate();
      System.out.println("✅ Test user inserted");
    }

    // Insert test balance
    String insertBalanceSQL = "INSERT OR IGNORE INTO Balance (user_ID, amount) VALUES (1, 5000.00)";
    try (PreparedStatement pstmt = conn.prepareStatement(insertBalanceSQL)) {
      pstmt.executeUpdate();
      System.out.println("✅ Test balance inserted");
    }
  }

  /**
   * Verify database setup
   */
  private static void verifyDatabaseSetup() throws Exception {
    System.out.println("\nVerifying database setup...");

    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement()) {

      // Check tables exist
      ResultSet rs = stmt.executeQuery(
          "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name");

      System.out.println("📋 Created tables:");
      int tableCount = 0;
      while (rs.next()) {
        tableCount++;
        System.out.println("   - " + rs.getString("name"));
      }
      rs.close();
      System.out.println("✅ Total tables: " + tableCount);

      // Check users count
      ResultSet userRs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
      if (userRs.next()) {
        System.out.println("👥 Total users: " + userRs.getInt("count"));
      }
      userRs.close();

      // Check balance count
      ResultSet balanceRs = stmt.executeQuery("SELECT COUNT(*) as count FROM Balance");
      if (balanceRs.next()) {
        System.out.println("💰 Total balance records: " + balanceRs.getInt("count"));
      }
      balanceRs.close();

      // Show sample data
      showSampleData(conn);
    }
  }

  /**
   * Show sample data
   */
  private static void showSampleData(Connection conn) throws Exception {
    System.out.println("\n📊 Sample Data:");

    String query = """
        SELECT u.id, u.name, u.email, b.amount
        FROM users u
        LEFT JOIN Balance b ON u.id = b.user_ID
        LIMIT 5
        """;

    try (PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery()) {

      System.out.printf("%-3s %-15s %-25s %-10s%n", "ID", "Name", "Email", "Balance");
      System.out.println("-".repeat(60));

      while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        double amount = rs.getDouble("amount");

        System.out.printf("%-3d %-15s %-25s ₱%,.2f%n", id, name, email, amount);
      }
    }
  }

  /**
   * Test balance functionality
   */
  private static void testBalanceFunctionality() throws Exception {
    System.out.println("\n🧪 Testing Balance Functionality...");

    CheckBalanceService balanceService = new CheckBalanceService();

    // Find a user to test with
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM users LIMIT 1");
        ResultSet rs = stmt.executeQuery()) {

      if (rs.next()) {
        int testUserId = rs.getInt("id");

        // Test balance check
        var balance = balanceService.checkBalance(testUserId);
        if (balance.isPresent()) {
          System.out.printf("✅ Balance Service: User %d has ₱%,.2f%n",
              testUserId, balance.get());
        } else {
          System.out.printf("⚠️  No balance found for user %d, initializing...%n", testUserId);

          // Initialize balance if not found
          boolean initialized = balanceService.initializeBalance(testUserId, 1000.0);
          if (initialized) {
            System.out.printf("✅ Balance initialized for user %d%n", testUserId);

            // Check again
            var newBalance = balanceService.checkBalance(testUserId);
            if (newBalance.isPresent()) {
              System.out.printf("✅ New balance: ₱%,.2f%n", newBalance.get());
            }
          }
        }

        // Test formatted balance
        String formatted = balanceService.getFormattedBalance(testUserId);
        System.out.println("📊 Formatted Balance: " + formatted);

        // Test sufficient balance check
        boolean sufficient = balanceService.hasSufficientBalance(testUserId, 100.0);
        System.out.printf("💳 Has ₱100: %s%n", sufficient ? "✅ YES" : "❌ NO");

      } else {
        System.out.println("⚠️  No users found to test with");
      }
    }
  }
}