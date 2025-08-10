package com.tesdaciicc.service;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.model.Balance;
import com.tesdaciicc.data.repository.BalanceDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckBalanceService {

  private BalanceDAO balanceDAO;

  public CheckBalanceService() {
    this.balanceDAO = new BalanceDAO();
  }

  public double checkBalance(int userId) {
    try {
      Balance balance = balanceDAO.getBalanceByUserId(userId);
      if (balance != null) {
        return balance.getAmount();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1; // not found
  }

}
