package com.tesdaciicc.data.util;

// import java.io.IOException;
// import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final Properties properties = new Properties();

  // static {
  //   try (InputStream input = Config.class.getClassLoader()
  //       .getResourceAsStream("config.properties")) {
  //     if (input == null) {
  //       throw new RuntimeException("config.properties not found in resources folder");
  //     }
  //     properties.load(input);
  //   } catch (IOException e) {
  //     throw new RuntimeException("Failed to load config.properties", e);
  //   }
  // }

  // Database configuration - Fixed to use full path
  public static final String DATABASE_NAME = "gcashapp.db";
  public static final String DATABASE_URL = "jdbc:sqlite:./gcashapp.db";
  public static final String DATABASE_DRIVER = "org.sqlite.JDBC";
  public static final String INIT_SQL_FILE = "/sql/001_init.sql";
  public static final String INDEX_SQL_FILE = "/sql/002_indexes.sql";
  public static final String DATA_SQL_FILE = "/sql/003_data.sql";

  // Application settings
  public static final String APP_NAME = "GCash App";
  public static final String APP_VERSION = "1.0.0";

  // Security settings
  public static final int MIN_PIN_LENGTH = 4;
  public static final int MAX_PIN_LENGTH = 6;
  public static final int MIN_NAME_LENGTH = 2;
  public static final int MAX_LOGIN_ATTEMPTS = 3;

  // Phone number validation
  public static final String PHONE_PATTERN = "^09\\d{9}$"; // Philippine mobile format

  // Email validation
  public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";

  private Config() {
    // Utility class - prevent instantiation
  }

  public static String get(String key) {
    return properties.getProperty(key);
  }

  public static String getDbUrl() {
        return properties.getProperty("DATABASE_URL", "jdbc:sqlite:./gcashapp.db");
    }
    
  public static String getDbDriver() {
        return properties.getProperty("DATABASE_DRIVER", "org.sqlite.JDBC");
    }
}