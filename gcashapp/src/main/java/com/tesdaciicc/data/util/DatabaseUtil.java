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

import com.tesdaciicc.data.util.ConnectionFactory;

public class DatabaseUtil {

  private DatabaseUtil() {
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
    String url = Config.get("db.url"); // e.g., jdbc:sqlite:./database/gcashapp.db
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

  /** Inserts dummy balance data if missing */
  private static void seedBalanceData() {
    String checkSql = "SELECT COUNT(*) FROM Balance";
    String insertSql = "INSERT INTO Balance (amount, user_ID) VALUES (?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        ResultSet rs = checkStmt.executeQuery()) {

      if (rs.next() && rs.getInt(1) == 0) { // no records yet
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
          insertStmt.setDouble(1, 1000.50);
          insertStmt.setInt(2, 1);
          insertStmt.executeUpdate();

          insertStmt.setDouble(1, 250.75);
          insertStmt.setInt(2, 2);
          insertStmt.executeUpdate();

          System.out.println("[DatabaseUtil] Seeded dummy Balance data.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}