package com.tesdaciicc.data.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tesdaciicc.data.util.ConnectionFactory;

/**
 * Utility class for database operations and initialization
 */
public class DatabaseUtil {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);
  
  private DatabaseUtil() {
    // Utility class - prevent instantiation
  }

  /** Call this once on app startup. */
  public static void initializeDatabase() {
    ensureDatabaseFolderExists();
    runSqlFromResource("sql/001_init.sql"); // put your create-table here
    // Add more migrations as needed (002_..., 003_..., etc.)
    seedBalanceData(); // insert dummy balances if missing
  }

  /** If db.url points to ./database/gcashapp.db, make sure ./database exists. */
  private static void ensureDatabaseFolderExists() {
    String url = Config.get("db.url");
    if (url == null)
      return;
    if (!url.startsWith("jdbc:sqlite:"))
      return;

    String pathPart = url.substring("jdbc:sqlite:".length());
    Path dbPath = Paths.get(pathPart).normalize();

    Path parent = dbPath.getParent();
    if (parent != null) {
      try {
        Files.createDirectories(parent);
      } catch (Exception e) {
        throw new RuntimeException("Failed to create database directory: " + parent, e);
      }
    }
  }

  /** Executes a SQL file from resources (classpath). */
  public static void runSqlFromResource(String resourcePath) {
    try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (in == null)
        return;
      String sql = new BufferedReader(new InputStreamReader(in))
          .lines().collect(Collectors.joining("\n"));

      for (String stmt : sql.split(";")) {
        String trimmed = stmt.trim();
        if (trimmed.isEmpty())
          continue;
        executeUpdate(trimmed);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to run SQL resource: " + resourcePath, e);
    }
  }

  private static void executeUpdate(String sql) {
    try (Connection c = ConnectionFactory.getConnection();
        Statement st = c.createStatement()) {
      st.executeUpdate(sql);
    } catch (Exception e) {
      throw new RuntimeException("SQL failed: " + sql, e);
    }
  }

  /** Inserts dummy balance data for each user if missing */
  private static void seedBalanceData() {
    String getUsersSql = "SELECT id FROM users";
    String checkBalanceSql = "SELECT COUNT(*) FROM Balance WHERE user_ID = ?";
    String insertSql = "INSERT INTO Balance (amount, user_ID) VALUES (?, ?)";

    Random random = new Random();

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement getUsersStmt = conn.prepareStatement(getUsersSql);
        ResultSet usersRs = getUsersStmt.executeQuery()) {

      while (usersRs.next()) {
        int userId = usersRs.getInt("id");

        // Check if balance exists for this user
        try (PreparedStatement checkStmt = conn.prepareStatement(checkBalanceSql)) {
          checkStmt.setInt(1, userId);
          ResultSet checkRs = checkStmt.executeQuery();

          if (checkRs.next() && checkRs.getInt(1) == 0) {
            // Generate a random starting balance between ₱100 and ₱5000
            double startingBalance = 100 + (5000 - 100) * random.nextDouble();
            startingBalance = Math.round(startingBalance * 100.0) / 100.0; // 2 decimal places

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
              insertStmt.setDouble(1, startingBalance);
              insertStmt.setInt(2, userId);
              insertStmt.executeUpdate();
              System.out.println("[DatabaseUtil] Seeded Balance for User ID " + userId +
                  " (₱" + startingBalance + ")");
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}