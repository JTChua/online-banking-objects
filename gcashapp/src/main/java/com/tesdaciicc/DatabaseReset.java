package com.tesdaciicc;

import com.tesdaciicc.data.util.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseReset {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseReset.class);

  public static void main(String[] args) {
    logger.info("Starting complete database reset...");

    try {
      // Option 1: Delete the database file completely
      resetByDeletingFile();

      // Option 2: Drop all tables (alternative method)
      // resetByDroppingTables();

      logger.info("✅ Database reset completed successfully!");
      System.out.println("✅ Database has been completely reset.");
      System.out.println("ℹ️  You can now run your FreshDatabaseSetup.java with the updated SQL.");

    } catch (Exception e) {
      logger.error("Error during database reset: {}", e.getMessage(), e);
      System.err.println("❌ Database reset failed: " + e.getMessage());
    }
  }

  /**
   * Reset by deleting the database file completely
   */
  private static void resetByDeletingFile() {
    logger.info("Resetting database by deleting file...");

    // Path to your database file
    String dbPath = "gcashapp.db"; // Adjust if your path is different
    File dbFile = new File(dbPath);

    if (dbFile.exists()) {
      if (dbFile.delete()) {
        logger.info("✅ Database file deleted successfully: {}", dbPath);
        System.out.println("✅ Database file deleted: " + dbPath);
      } else {
        logger.error("❌ Failed to delete database file: {}", dbPath);
        System.err.println("❌ Failed to delete database file: " + dbPath);
      }
    } else {
      logger.info("ℹ️ Database file not found: {}", dbPath);
      System.out.println("ℹ️ Database file not found: " + dbPath);
    }
  }

  /**
   * Reset by dropping all tables (alternative method)
   */
  private static void resetByDroppingTables() throws SQLException {
    logger.info("Resetting database by dropping tables...");

    try (Connection connection = ConnectionFactory.getConnection();
        Statement stmt = connection.createStatement()) {

      // Drop tables in correct order (balance first due to foreign key)
      try {
        stmt.execute("DROP TABLE IF EXISTS balance");
        logger.info("✅ Dropped balance table");
      } catch (SQLException e) {
        logger.warn("Could not drop balance table: {}", e.getMessage());
      }

      try {
        stmt.execute("DROP TABLE IF EXISTS users");
        logger.info("✅ Dropped users table");
      } catch (SQLException e) {
        logger.warn("Could not drop users table: {}", e.getMessage());
      }

      // Drop any other tables you might have
      // stmt.execute("DROP TABLE IF EXISTS transactions");
      // stmt.execute("DROP TABLE IF EXISTS cashin");

      logger.info("✅ All tables dropped successfully");
    }
  }

  /**
   * Backup existing data before reset (optional)
   */
  private static void backupExistingData() throws SQLException {
    logger.info("Creating backup of existing data...");

    String backupSQL = """
        -- Backup existing users
        SELECT 'INSERT INTO users (name, email, number, pin, createdDate, updatedDate) VALUES (' ||
               quote(name) || ', ' || quote(email) || ', ' || quote(number) || ', ' ||
               quote(pin) || ', ' || quote(createdDate) || ', ' || quote(updatedDate) || ');'
        FROM users;
        """;

    try (Connection connection = ConnectionFactory.getConnection();
        Statement stmt = connection.createStatement()) {

      // You can execute this to get SQL statements for restoring data
      var rs = stmt.executeQuery("SELECT * FROM users");
      System.out.println("\n=== EXISTING USERS DATA ===");
      while (rs.next()) {
        System.out.printf("User ID: %d, Name: %s, Email: %s%n",
            rs.getInt("id"), rs.getString("name"), rs.getString("email"));
      }
    }
  }
}