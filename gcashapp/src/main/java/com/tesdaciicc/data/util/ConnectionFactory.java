package com.tesdaciicc.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
  private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

  private static final String DB_URL;

  static {
    String rawUrl = Config.DATABASE_URL;

    if (rawUrl.startsWith("jdbc:sqlite:")) {
      String pathPart = rawUrl.substring("jdbc:sqlite:".length());

      // If path is relative (starts with ./ or doesn't start with /)
      if (!pathPart.startsWith("/")) {
        String basePath = System.getProperty("user.dir");
        pathPart = basePath + "/" + pathPart.replaceFirst("^\\./", "");
      }

      DB_URL = "jdbc:sqlite:" + pathPart;
    } else {
      DB_URL = rawUrl;
    }

    System.out.println("Resolved DB Path: " + DB_URL); // Debug
  }

  private ConnectionFactory() {
    // Utility class - prevent instantiation
  }

  /**
   * Creates a new database connection
   * 
   * @return Connection object
   * @throws SQLException if connection fails
   */
  public static Connection getConnection() throws SQLException {
    try {
      // Load SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      // Create connection using resolved path
      Connection connection = DriverManager.getConnection(DB_URL);

      // Enable foreign key constraints
      connection.createStatement().execute("PRAGMA foreign_keys = ON");

      logger.debug("Database connection established: {}", DB_URL);
      return connection;

    } catch (ClassNotFoundException e) {
      logger.error("SQLite JDBC driver not found", e);
      throw new SQLException("Database driver not found", e);
    } catch (SQLException e) {
      logger.error("Failed to create database connection", e);
      throw e;
    }
  }

  /**
   * Tests database connectivity
   * 
   * @return true if connection is successful
   */
  public static boolean testConnection() {
    try (Connection connection = getConnection()) {
      return connection.isValid(5); // 5 second timeout
    } catch (SQLException e) {
      logger.error("Database connection test failed", e);
      return false;
    }
  }

  /**
   * Closes a connection safely
   * 
   * @param connection Connection to close
   */
  public static void closeConnection(Connection connection) {
    if (connection != null) {
      try {
        connection.close();
        logger.debug("Database connection closed");
      } catch (SQLException e) {
        logger.error("Error closing database connection", e);
      }
    }
  }
}
