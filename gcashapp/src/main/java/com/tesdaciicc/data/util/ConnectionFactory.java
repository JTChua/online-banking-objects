package com.tesdaciicc.data.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
  private static final String DB_URL;

  static {
    String rawUrl = Config.get("db.url"); // e.g. jdbc:sqlite:./sqlite3/gcashapp.db

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
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL);
  }
}
