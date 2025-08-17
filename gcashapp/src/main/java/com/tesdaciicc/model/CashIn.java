package com.tesdaciicc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashIn {

  private int transactionId;
  private BigDecimal transactionAmount;
  private String name;
  private int userId;
  private LocalDateTime transactionDate;
  private String transferToAccountNo;
  private String transferFromAccountNo;
  private String accountNumber;

   // Default constructor
    public CashIn() {
    }

    // Constructor for cash-in operations
    public CashIn(BigDecimal transactionAmount, String name, int userId, String transferToAccountNo, String transferFromAccountNo, String accountNumber) {
        setTransactionAmount(transactionAmount);
        setName(name);
        setUserId(userId);
        setTransferToAccountNo(transferToAccountNo);
        setTransferFromAccountNo(transferFromAccountNo);
        setAccountNumber(accountNumber);
        setTransactionDate(LocalDateTime.now());
    }


    // Full constructor (for reading from DB)
    public CashIn(int transactionId, BigDecimal transactionAmount, String name, int userId, LocalDateTime transactionDate, String transferToAccountNo, String transferFromAccountNo, String accountNumber) {
        setTransactionId(transactionId);
        setTransactionAmount(transactionAmount);
        setName(name);
        setUserId(userId);
        setTransactionDate(transactionDate);
        setTransferToAccountNo(transferToAccountNo);
        setTransferFromAccountNo(transferFromAccountNo);
        setAccountNumber(accountNumber);
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount != null ? transactionAmount : BigDecimal.ZERO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransferToAccountNo() {
        return transferToAccountNo;
    }

    public void setTransferToAccountNo(String transferToAccountNo) {
        this.transferToAccountNo = transferToAccountNo;
    }

    public String getTransferFromAccountNo() {
        return transferFromAccountNo;
    }

    public void setTransferFromAccountNo(String transferFromAccountNo) {
        this.transferFromAccountNo = transferFromAccountNo;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "CashIn{" +
                "transactionId=" + transactionId +
                ", transactionAmount=" + transactionAmount +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", transferToAccountNo='" + transferToAccountNo + '\'' +
                ", transferFromAccountNo='" + transferFromAccountNo + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }

}
