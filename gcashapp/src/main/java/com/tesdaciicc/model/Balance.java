package com.tesdaciicc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Balance {

  private int id;
  private int userId;
  private BigDecimal amount; 
  private String createdDate;
  private String updatedDate;

  // Default constructor
  public Balance() {
  }

  // Constructor for creating new balance (without ID)
  public Balance(BigDecimal amount, int userId) {
    setAmount(amount);
    setUserId(userId);
  }

  // Full constructor (for reading from DB)
  public Balance(int id, int userId, BigDecimal amount, String createdDate, String updatedDate) {
    setId(id);
    setUserId(userId);
    setAmount(amount);
    setCreatedDate(createdDate);
    setUpdatedDate(updatedDate);
  }

  public Balance(int userId, BigDecimal amount) {
    setUserId(userId);
    setAmount(amount);
    setCreatedDate(createdDate);
    setUpdatedDate(updatedDate);
  }

  // Getters & Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount != null ? amount : BigDecimal.ZERO;
  }

  // Convenience method for double values
  public void setAmount(double amount) {
    this.amount = BigDecimal.valueOf(amount);
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public String getUpdatedDate() {
    return updatedDate;
  }

  public void setUpdatedDate(String updatedDate) {
    this.updatedDate = updatedDate;
  }

  // Utility method to get formatted amount
  public String getFormattedAmount() {
    return "₱" + (amount != null ? String.format("%,.2f", amount) : "0.00");
  }

  // Utility method to check if amount is sufficient for a transaction
  public boolean isSufficientFor(BigDecimal transactionAmount) {
    if (amount == null || transactionAmount == null) {
      return false;
    }
    return amount.compareTo(transactionAmount) >= 0;
  }

  // Convenience method for double comparison
  public boolean isSufficientFor(double transactionAmount) {
    return isSufficientFor(BigDecimal.valueOf(transactionAmount));
  }

  // Utility methods
    
    /**
     * Add amount to current balance
     * @param additionalAmount Amount to add
     * @return New balance amount
     */
    public BigDecimal add(BigDecimal additionalAmount) {
        if (additionalAmount != null) {
            this.amount = this.amount.add(additionalAmount);
        }
        return this.amount;
    }
    
    /**
     * Subtract amount from current balance
     * @param deductionAmount Amount to subtract
     * @return New balance amount
     */
    public BigDecimal subtract(BigDecimal deductionAmount) {
        if (deductionAmount != null) {
            this.amount = this.amount.subtract(deductionAmount);
        }
        return this.amount;
    }
    
    /**
     * Check if balance is zero
     * @return true if balance is zero
     */
    public boolean isZero() {
        //return amount.compareTo(BigDecimal.ZERO) == 0;
        return amount != null && amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Check if balance is negative
     * @return true if balance is negative
     */
    public boolean isNegative() {
        //return amount.compareTo(BigDecimal.ZERO) < 0;
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Check if balance is positive
     * @return true if balance is positive
     */
    public boolean isPositive() {
        //return amount.compareTo(BigDecimal.ZERO) > 0;
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

  @Override
  public String toString() {
    return "Balance{" +
        "id=" + id +
        ", userId=" + userId +
        ", amount=" + amount +
        ", createdDate='" + createdDate + '\'' +
        ", updatedDate='" + updatedDate + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Balance balance = (Balance) o;
    return id == balance.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}