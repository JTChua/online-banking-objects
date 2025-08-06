package com.tesdaciicc.data.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final Properties props = new Properties();

  static {
    try (InputStream input = Config.class.getClassLoader()
        .getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new RuntimeException("config.properties not found in resources folder");
      }
      props.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config.properties", e);
    }
  }

  private Config() {
  }

  public static String get(String key) {
    return props.getProperty(key);
  }

}
