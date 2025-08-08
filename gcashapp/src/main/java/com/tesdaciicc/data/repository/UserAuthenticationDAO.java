package com.tesdaciicc.data.repository;

import model.UserAuthentication;
import util.ConnectionFactory;

import java.sql.*;

public class UserAuthenticationDAO {

  public void register(UserAuthentication userAuthentication) throws SQLException {
    String sql = "INSERT INTO users (name, email, number, pin) VALUES (?, ?, ?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, userAuthentication.getName());
      pstmt.setString(2, userAuthentication.getEmail());
      pstmt.setString(3, userAuthentication.getNumber());
      pstmt.setString(4, userAuthentication.getPin());
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
        return new User(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("number"),
            rs.getString("pin"));
      }
    }
    return null;
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

}
