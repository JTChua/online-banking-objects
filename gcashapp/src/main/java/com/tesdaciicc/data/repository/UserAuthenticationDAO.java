package com.tesdaciicc.data.repository;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.data.util.ConnectionFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAuthenticationDAO {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public int getNextUserId() throws SQLException {
    String sql = "SELECT COALESCE(MAX(userId), 0) + 1 AS nextId FROM users";
    try (Connection conn = ConnectionFactory.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
      if (rs.next()) {
        return rs.getInt("nextId");
      }
    }
    return 1;
  }

  public void register(UserAuthentication userAuthentication) throws SQLException {
    String sql = "INSERT INTO users (userId, name, email, number, pin, createdDate) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, userAuthentication.getId());
      pstmt.setString(2, userAuthentication.getName());
      pstmt.setString(3, userAuthentication.getEmail());
      pstmt.setString(4, userAuthentication.getNumber());
      pstmt.setString(5, userAuthentication.getPin());
      pstmt.setString(6, userAuthentication.getFormattedCreatedDate());
      pstmt.executeUpdate();
    }
  }

  public UserAuthentication login(String number, String pin) throws SQLException {
    String sql = "SELECT * FROM users WHERE number = ? AND pin = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, number);
      pstmt.setString(2, pin);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        updateLastLogin(rs.getInt("userId"));
        return new UserAuthentication(
            rs.getInt("userId"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("number"),
            rs.getString("pin"),
            LocalDateTime.parse(rs.getString("createdDate"), FORMATTER),
            rs.getString("lastLogin") != null ? LocalDateTime.parse(rs.getString("lastLogin"), FORMATTER) : null);
      }
    }
    return null;
  }

  public void updateLastLogin(int userId, String lastLogin) throws SQLException {
    String sql = "UPDATE users SET lastLogin = ? WHERE userId = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, lastLogin); // use the passed-in value
      pstmt.setInt(2, userId);
      pstmt.executeUpdate();
    }
  }

  public void updateLastLogin(int userId) throws SQLException {
    // forward to the 2-arg method using formatted now()
    String nowStr = LocalDateTime.now().format(FORMATTER);
    updateLastLogin(userId, nowStr);
  }

  public boolean updatePin(String number, String oldPin, String newPin) throws SQLException {
    String sql = "UPDATE users SET pin = ? WHERE number = ? AND pin = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, newPin);
      pstmt.setString(2, number);
      pstmt.setString(3, oldPin);
      return pstmt.executeUpdate() > 0;
    }
  }

  public boolean deleteUser(String number, String pin) throws SQLException {
    String sql = "DELETE FROM users WHERE number = ? AND pin = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, number);
      pstmt.setString(2, pin);
      return pstmt.executeUpdate() > 0;
    }
  }
}