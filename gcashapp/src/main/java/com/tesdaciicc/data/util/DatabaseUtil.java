package com.tesdaciicc.data.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Objects;
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
  }

  /** If db.url points to ./database/gcashapp.db, make sure ./database exists. */
  private static void ensureDatabaseFolderExists() {
    String url = Config.get("db.url"); // e.g., jdbc:sqlite:./database/gcashapp.db
    if (url == null)
      return;
    // Only act for file-based SQLite URLs
    if (!url.startsWith("jdbc:sqlite:"))
      return;

    String pathPart = url.substring("jdbc:sqlite:".length()); // ./database/gcashapp.db or gcashapp.db
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
        return; // no-op if file not present
      String sql = new BufferedReader(new InputStreamReader(in))
          .lines().collect(Collectors.joining("\n"));

      // naive splitter by ';' - fine for simple DDL; avoid for complex scripts
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
}