package com.tesdaciicc;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.data.util.Config;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FreshDatabaseSetup {
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

      // Step 3: Create fresh connection (this will create the database file)
      System.out.println("Creating fresh database connection...");
      try (Connection conn = ConnectionFactory.getConnection()) {
        System.out.println("✅ Database file created successfully");
        System.out.println("Connection URL: " + conn.getMetaData().getURL());
        System.out.println("Database exists at: " + Paths.get(dbPath).toAbsolutePath());

        // Step 4: Create tables manually to ensure they work
        System.out.println("\nCreating database schema...");

        try (Statement stmt = conn.createStatement()) {

          // Create users table
          String createUsersTable = """
              CREATE TABLE users (
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

          // Create Balance table
          String createBalanceTable = """
              CREATE TABLE Balance (
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
          stmt.execute("CREATE INDEX idx_users_email ON users(email)");
          stmt.execute("CREATE INDEX idx_users_number ON users(number)");
          stmt.execute("CREATE INDEX idx_balance_user_id ON Balance(user_ID)");
          System.out.println("✅ Database indexes created");

          // Step 5: Verify tables were created
          System.out.println("\nVerifying database schema...");
          ResultSet rs = stmt.executeQuery(
              "SELECT name, sql FROM sqlite_master WHERE type='table' ORDER BY name");

          int tableCount = 0;
          while (rs.next()) {
            tableCount++;
            System.out.println("📋 Table: " + rs.getString("name"));
            // Uncomment next line to see full table definition
            // System.out.println(" SQL: " + rs.getString("sql"));
          }
          rs.close();

          System.out.println("✅ Total tables created: " + tableCount);

          // Step 6: Test a simple insert
          System.out.println("\nTesting database operations...");

          String insertSQL = """
              INSERT INTO users (name, email, number, pin)
              VALUES (?, ?, ?, ?)
              """;

          try (var pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, "Test User");
            pstmt.setString(2, "test@example.com");
            pstmt.setString(3, "09123456789");
            pstmt.setString(4, "1234");

            int result = pstmt.executeUpdate();
            System.out.println("✅ Test user inserted: " + result + " row affected");
          }

          // Verify the insert
          ResultSet userRs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
          if (userRs.next()) {
            System.out.println("✅ Total users in database: " + userRs.getInt("count"));
          }
          userRs.close();

          System.out.println("\n🎉 Database setup completed successfully!");
          System.out.println("\nNext steps:");
          System.out.println("1. Your database is now ready at: " + Paths.get(dbPath).toAbsolutePath());
          System.out.println("2. You can now run your App.java without initialization issues");
          System.out.println("3. The database has been tested and is working properly");

        }

      }

    } catch (Exception e) {
      System.err.println("❌ Database setup failed: " + e.getMessage());
      e.printStackTrace();
    }
  }
}