package com.tesdaciicc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transactions model class representing transaction data from the database
 */
public class Transactions {
    
    private int transactionId;
    private BigDecimal transactionAmount;
    private String transactionName;
    private int userId;
    private LocalDateTime transactionDate;
    private String transferToAccountNo;
    private String transferFromAccountNo;
    private String accountNumber;
    
    // Default constructor
    public Transactions() {
    }
    
    // Constructor without ID (for creating new transactions)
    public Transactions(BigDecimal transactionAmount, String transactionName, int userId, 
                       LocalDateTime transactionDate, String transferToAccountNo, 
                       String transferFromAccountNo, String accountNumber) {
        setTransactionAmount(transactionAmount);
        setTransactionName(transactionName);
        setUserId(userId);
        setTransactionDate(transactionDate);
        setTransferToAccountNo(transferToAccountNo);
        setTransferFromAccountNo(transferFromAccountNo);
        setAccountNumber(accountNumber);
    }
    
    // Full constructor (for reading from database)
    public Transactions(int transactionId, BigDecimal transactionAmount, String transactionName, 
                       int userId, LocalDateTime transactionDate, String transferToAccountNo, 
                       String transferFromAccountNo, String accountNumber) {
        setTransactionId(transactionId);
        setTransactionAmount(transactionAmount);
        setTransactionName(transactionName);
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
    
    // Convenience method for double values
    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = BigDecimal.valueOf(transactionAmount);
    }
    
    public String getTransactionName() {
        return transactionName;
    }
    
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
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
        this.transactionDate = transactionDate != null ? transactionDate : LocalDateTime.now();
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
    
    // Utility methods
    
    /**
     * Get formatted transaction amount with peso sign
     * @return Formatted amount string
     */
    public String getFormattedAmount() {
        return "₱" + (transactionAmount != null ? String.format("%,.2f", transactionAmount) : "0.00");
    }
    
    /**
     * Get formatted transaction date
     * @return Formatted date string
     */
    public String getFormattedDate() {
        return transactionDate != null ? transactionDate.toString().replace("T", " ") : "";
    }
    
    /**
     * Check if transaction is a transfer (has both to and from account numbers)
     * @return true if it's a transfer transaction
     */
    public boolean isTransfer() {
        return transferToAccountNo != null && !transferToAccountNo.trim().isEmpty() &&
               transferFromAccountNo != null && !transferFromAccountNo.trim().isEmpty();
    }
    
    /**
     * Check if transaction is a cash-in (has only to account number)
     * @return true if it's a cash-in transaction
     */
    public boolean isCashIn() {
        return transferToAccountNo != null && !transferToAccountNo.trim().isEmpty() &&
               (transferFromAccountNo == null || transferFromAccountNo.trim().isEmpty());
    }
    
    /**
     * Check if transaction is a cash-out (has only from account number)
     * @return true if it's a cash-out transaction
     */
    public boolean isCashOut() {
        return transferFromAccountNo != null && !transferFromAccountNo.trim().isEmpty() &&
               (transferToAccountNo == null || transferToAccountNo.trim().isEmpty());
    }
    
    /**
     * Get transaction type as string
     * @return Transaction type (TRANSFER, CASH_IN, CASH_OUT, OTHER)
     */
    public String getTransactionType() {
        if (isTransfer()) {
            return "TRANSFER";
        } else if (isCashIn()) {
            return "CASH_IN";
        } else if (isCashOut()) {
            return "CASH_OUT";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * Check if transaction amount is positive
     * @return true if amount is positive
     */
    public boolean isPositiveAmount() {
        return transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if transaction involves a specific account number
     * @param accountNo Account number to check
     * @return true if transaction involves the account
     */
    public boolean involvesAccount(String accountNo) {
        if (accountNo == null) {
            return false;
        }
        
        return accountNo.equals(transferToAccountNo) || 
               accountNo.equals(transferFromAccountNo) ||
               accountNo.equals(accountNumber);
    }
    
    /**
     * Get the other party's account number in a transfer
     * @param myAccountNo My account number
     * @return Other party's account number, or null if not applicable
     */
    public String getOtherPartyAccount(String myAccountNo) {
        if (myAccountNo == null || !isTransfer()) {
            return null;
        }
        
        if (myAccountNo.equals(transferFromAccountNo)) {
            return transferToAccountNo;
        } else if (myAccountNo.equals(transferToAccountNo)) {
            return transferFromAccountNo;
        }
        
        return null;
    }
    
    /**
     * Check if this is an incoming transaction for the given account
     * @param accountNo Account number to check
     * @return true if it's an incoming transaction
     */
    public boolean isIncoming(String accountNo) {
        return accountNo != null && accountNo.equals(transferToAccountNo);
    }
    
    /**
     * Check if this is an outgoing transaction for the given account
     * @param accountNo Account number to check
     * @return true if it's an outgoing transaction
     */
    public boolean isOutgoing(String accountNo) {
        return accountNo != null && accountNo.equals(transferFromAccountNo);
    }
    
    @Override
    public String toString() {
        return "Transactions{" +
                "transactionId=" + transactionId +
                ", transactionAmount=" + transactionAmount +
                ", transactionName='" + transactionName + '\'' +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", transferToAccountNo='" + transferToAccountNo + '\'' +
                ", transferFromAccountNo='" + transferFromAccountNo + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transactions that = (Transactions) o;
        return transactionId == that.transactionId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}