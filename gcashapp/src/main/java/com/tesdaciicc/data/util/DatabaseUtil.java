package com.tesdaciicc.data.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;

/**
 * Utility class for database operations and initialization
 */
public class DatabaseUtil {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

  private DatabaseUtil() {
    // Utility class - prevent instantiation
  }

  /**
   * Call this once on app startup
   * 
   * @return true if initialization is successful
   */
  public static boolean initializeDatabase() {
     try {
        logger.info("Initializing database...");
        ensureDatabaseFolderExists();

        // Simple version without URL checking
        InputStream sqlStream = DatabaseUtil.class.getResourceAsStream(Config.INIT_SQL_FILE);
        if (sqlStream == null) {
            logger.error("SQL resource not found: {}", Config.INIT_SQL_FILE);
            return false;
        }
        sqlStream.close();

        if (!runSqlFromResource(Config.INIT_SQL_FILE)) {
            logger.error("Failed to run initialization SQL");
            return false;
        }

        seedBalanceData();
        logger.info("Database initialized successfully");
        return true;

    } catch (Exception e) {
        logger.error("Database initialization failed", e);
        return false;
    }
  }

  /**
   * If db.url points to ./database/gcashapp.db, make sure ./database exists
   */
  private static void ensureDatabaseFolderExists() {
    // Use Config constant instead of Config.get()
    String url = Config.DATABASE_URL;
    if (url == null) {
      logger.warn("Database URL is null");
      return;
    }

    if (!url.startsWith("jdbc:sqlite:")) {
      logger.debug("Not an SQLite URL, skipping folder creation");
      return;
    }

    String pathPart = url.substring("jdbc:sqlite:".length());
    Path dbPath = Paths.get(pathPart).normalize();

    Path parent = dbPath.getParent();
    if (parent != null) {
      try {
        Files.createDirectories(parent);
        logger.debug("Created database directory: {}", parent);
      } catch (Exception e) {
        logger.error("Failed to create database directory: {}", parent, e);
        throw new RuntimeException("Failed to create database directory: " + parent, e);
      }
    }
  }

  /**
   * Executes a SQL file from resources (classpath)
   * 
   * @param resourcePath Path to SQL resource file
   * @return true if execution was successful
   */
  public static boolean runSqlFromResource(String resourcePath) {
    logger.debug("Running SQL from resource: {}", resourcePath);

    try (InputStream in = DatabaseUtil.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        logger.error("SQL resource not found: {}", resourcePath);
        return false;
      }

      String sql = new BufferedReader(new InputStreamReader(in))
          .lines()
          .collect(Collectors.joining("\n"));

      return executeMultipleStatements(sql);

    } catch (Exception e) {
      logger.error("Failed to run SQL resource: {}", resourcePath, e);
      return false;
    }
  }

  /**
   * Executes multiple SQL statements separated by semicolons
   * 
   * @param sql Multiple SQL statements
   * @return true if all statements executed successfully
   */
  private static boolean executeMultipleStatements(String sql) {
    String[] statements = sql.split(";(?=(?:[^']*'[^']*')*[^']*$)"); // Split on semicolons not inside quotes
    
    try (Connection connection = ConnectionFactory.getConnection();
         Statement statement = connection.createStatement()) {
        
        connection.setAutoCommit(false); // Start transaction
        
        for (String stmt : statements) {
            String trimmed = stmt.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    statement.execute(trimmed);
                    logger.debug("Executed SQL: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                } catch (SQLException e) {
                    logger.error("Failed to execute statement: {}", trimmed, e);
                    connection.rollback();
                    return false;
                }
            }
        }
        connection.commit(); // Commit transaction
        return true;
        
    } catch (SQLException e) {
        logger.error("Failed to execute SQL statements", e);
        return false;
    }
  }

  /**
   * Checks if the database exists and is accessible
   * 
   * @return true if database is ready
   */
  public static boolean isDatabaseReady() {
    try (Connection connection = ConnectionFactory.getConnection()) {
      // Try to query users table to verify database is properly set up
      connection.createStatement().executeQuery("SELECT COUNT(*) FROM users").close();
      logger.debug("Database is ready");
      return true;
    } catch (SQLException e) {
      logger.warn("Database not ready: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Inserts dummy balance data for each user if missing
   * Note: This requires a Balance table to exist
   */
  private static void seedBalanceData() {
    // Check if Balance table exists first
    if (!tableExists("balance")) {
      logger.debug("Balance table doesn't exist, skipping balance seeding");
      return;
    }

    logger.debug("Seeding balance data...");

    String getUsersSql = "SELECT id FROM users";
    String checkBalanceSql = "SELECT COUNT(*) FROM balance WHERE user_ID = ?";
    String insertSql = "INSERT INTO balance (amount, user_ID) VALUES (?, ?)";

    Random random = new Random();

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement getUsersStmt = conn.prepareStatement(getUsersSql);
        ResultSet usersRs = getUsersStmt.executeQuery()) {

      while (usersRs.next()) {
        int userId = usersRs.getInt("id");

        // Check if balance exists for this user
        try (PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSql)) {
          checkStmt.setInt(1, userId);
          try (ResultSet checkRs = checkStmt.executeQuery()) {
            if (checkRs.next() && checkRs.getInt(1) == 0) {
              // Generate a random starting balance between ₱100 and ₱5000
              double startingBalance = 100 + (5000 - 100) * random.nextDouble();
              startingBalance = Math.round(startingBalance * 100.0) / 100.0; // 2 decimal places

              try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setDouble(1, startingBalance);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
                logger.info("Seeded balance for User ID {} (₱{})", userId, startingBalance);
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      logger.error("Error seeding balance data", e);
    }
  }

  /**
   * Checks if a table exists in the database
   * 
   * @param tableName Table name to check
   * @return true if table exists
   */
  private static boolean tableExists(String tableName) {
    String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      statement.setString(1, tableName);
      try (ResultSet resultSet = statement.executeQuery()) {
        boolean exists = resultSet.next();
        logger.debug("Table '{}' exists: {}", tableName, exists);
        return exists;
      }

    } catch (SQLException e) {
      logger.error("Error checking if table '{}' exists", tableName, e);
      return false;
    }
  }

  /**
   * Drops all tables (for testing purposes)
   * 
   * @return true if successful
   */
  public static boolean dropAllTables() {
    logger.warn("Dropping all database tables");

    try (Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement()) {

      statement.execute("DROP TABLE IF EXISTS balance");
      statement.execute("DROP TABLE IF EXISTS users");
      logger.info("All tables dropped successfully");
      return true;

    } catch (SQLException e) {
      logger.error("Failed to drop tables", e);
      return false;
    }
  }
}