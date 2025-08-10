package com.tesdaciicc.model;

public class Balance {

  private int id;
  private double amount;
  private int userId;

  public Balance(int id, double amount, int userId) {
    this.id = id;
    this.amount = amount;
    this.userId = userId;
  }

  public Balance(double amount, int userId) {
    this.amount = amount;
    this.userId = userId;
  }

  public int getId() {
    return id;
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
