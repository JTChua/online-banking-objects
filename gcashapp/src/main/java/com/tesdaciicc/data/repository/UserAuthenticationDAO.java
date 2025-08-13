package com.tesdaciicc.data.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAuthenticationDAO {

  private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationDAO.class);

  // SQL Queries
  private static final String INSERT_USER = "INSERT INTO users (name, email, number, pin, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";

  private static final String SELECT_USER_BY_ID = "SELECT id, name, email, number, pin, created_at, updated_at FROM users WHERE id = ?";

  private static final String SELECT_USER_BY_EMAIL = "SELECT id, name, email, number, pin, created_at, updated_at FROM users WHERE email = ?";

  private static final String SELECT_USER_BY_NUMBER = "SELECT id, name, email, number, pin, created_at, updated_at FROM users WHERE number = ?";

  private static final String SELECT_USER_BY_EMAIL_OR_NUMBER = "SELECT id, name, email, number, pin, created_at, updated_at FROM users WHERE email = ? OR number = ?";

  private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, number = ?, updated_at = ? WHERE id = ?";

  private static final String UPDATE_PIN = "UPDATE users SET pin = ?, updated_at = ? WHERE id = ?";

  private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

  private static final String SELECT_ALL_USERS = "SELECT id, name, email, number, pin, created_at, updated_at FROM users ORDER BY created_at DESC";

  private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";
}