package com.tesdaciicc.data.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
  private static final String DB_URL = Config.get("db.url");

  private ConnectionFactory() {
  }

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DB_URL);
  }
}
