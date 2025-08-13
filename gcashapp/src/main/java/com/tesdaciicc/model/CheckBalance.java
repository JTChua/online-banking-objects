package com.tesdaciicc.model;

public class CheckBalance {

  private int balanceId;
  private double amount;
  private int userId;

  public CheckBalance(int balanceId, double amount, int userId) {
    this.balanceId = balanceId;
    this.amount = amount;
    this.userId = userId;
  }

  public CheckBalance(double amount, int userId) {
    this.amount = amount;
    this.userId = userId;
  }

  public int getbalanceId() {
    return balanceId;
  }

  public double getAmount() {
    return amount;
  }

  public int getUserId() {
    return userId;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

}
