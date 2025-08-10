package com.tesdaciicc.data.repository;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.model.Balance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceDAO {

  public void addBalance(Balance balance) throws SQLException {
    String sql = "INSERT INTO Balance (amount, user_ID) VALUES (?, ?)";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setDouble(1, balance.getAmount());
      pstmt.setInt(2, balance.getUserId());
      pstmt.executeUpdate();
    }
  }

  public Balance getBalanceByUserId(int userId) throws SQLException {
    String sql = "SELECT * FROM Balance WHERE user_ID = ?";
    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        return new Balance(
            rs.getInt("ID"),
            rs.getDouble("amount"),
            rs.getInt("user_ID"));
      }
    }
    return null;
  }

}
