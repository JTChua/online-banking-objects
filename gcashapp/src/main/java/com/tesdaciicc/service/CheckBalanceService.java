package com.tesdaciicc.service;

import com.tesdaciicc.data.util.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBalanceService {

  public double getBalanceByUserId(int userId) {
    String sql = "SELECT amount FROM Balance WHERE user_ID = ?";
    double balance = -1;

    try (Connection conn = ConnectionFactory.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, userId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        balance = rs.getDouble("amount");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return balance;
  }

}
