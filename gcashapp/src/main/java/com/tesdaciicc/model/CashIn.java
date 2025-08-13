package com.tesdaciicc.model;

import java.time.LocalDateTime;

public class CashIn {

  private int id;
  private double amount;
  private String name;
  private int accountId;
  private LocalDateTime date;
  private Integer transferToId;
  private Integer transferFromId;

  public CashIn(double amount, String name, int accountId, Integer transferToId, Integer transferFromId) {
    this.amount = amount;
    this.name = name;
    this.accountId = accountId;
    this.date = LocalDateTime.now();
    this.transferToId = transferToId;
    this.transferFromId = transferFromId;
  }

  public int getAccountId() {
    return accountId; 
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public double getAmount() {
    return amount;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public Integer getTransferToId() {
    return transferToId;
  }

  public Integer getTransferFromId() {
    return transferFromId;
  }

  @Override
  public String toString() {
    return "CashIn [id=" + id + ", amount=" + amount + ", name=" + name + ", accountId=" + accountId + ", date="
        + date + ", transferToId=" + transferToId + ", transferFromId=" + transferFromId + "]";
  }

}
