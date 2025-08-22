package com.tesdaciicc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CashTransfer {

    private int transactionId;
    private BigDecimal transactionAmount;
    private String transactionName;
    private int userId; // Sender's user ID
    private LocalDateTime transactionDate;
    private String transferToAccountNo; // Recipient's mobile number (11 digits)
    private String transferFromAccountNo; // Sender's mobile number (11 digits)
    private String accountNumber; // Sender's account number (same as transferFromAccountNo)
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED
    private String description; // Transfer description/notes
    
    // Default constructor
    public CashTransfer() {
    }
    
    // Constructor for new cash transfer (without ID)
    public CashTransfer(BigDecimal transactionAmount, String transactionName, int userId, 
                       String transferToAccountNo, String transferFromAccountNo, String description) {
        setTransactionAmount(transactionAmount);
        setTransactionName(transactionName);
        setUserId(userId);
        setTransferToAccountNo(transferToAccountNo);
        setTransferFromAccountNo(transferFromAccountNo);
        setAccountNumber(transferFromAccountNo); // Same as sender's number
        setDescription(description);
        setTransactionDate(LocalDateTime.now());
        setStatus("PENDING");
    }
    
    // Full constructor (for reading from DB)
    public CashTransfer(int transactionId, BigDecimal transactionAmount, String transactionName, 
                       int userId, LocalDateTime transactionDate, String transferToAccountNo, 
                       String transferFromAccountNo, String accountNumber, String status, String description) {
        setTransactionId(transactionId);
        setTransactionAmount(transactionAmount);
        setTransactionName(transactionName);
        setUserId(userId);
        setTransactionDate(transactionDate);
        setTransferToAccountNo(transferToAccountNo);
        setTransferFromAccountNo(transferFromAccountNo);
        setAccountNumber(accountNumber);
        setStatus(status);
        setDescription(description);
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status != null ? status : "PENDING";
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    // Utility methods
    
    /**
     * Check if the transfer is to the same account (self-transfer)
     * @return true if transferring to same account
     */
    public boolean isSelfTransfer() {
        return transferToAccountNo != null && transferFromAccountNo != null && 
               transferToAccountNo.equals(transferFromAccountNo);
    }
    
    /**
     * Check if the transfer amount is valid (positive and not zero)
     * @return true if amount is valid
     */
    public boolean isValidAmount() {
        return transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if the mobile numbers are valid (11 digits)
     * @return true if both mobile numbers are valid
     */
    public boolean areValidMobileNumbers() {
        return isValidMobileNumber(transferToAccountNo) && isValidMobileNumber(transferFromAccountNo);
    }
    
    /**
     * Validate mobile number format (11 digits starting with 09)
     * @param mobileNumber Mobile number to validate
     * @return true if valid
     */
    private boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber != null && 
               mobileNumber.matches("^09\\d{9}$") && 
               mobileNumber.length() == 11;
    }
    
    /**
     * Get formatted transaction amount with peso sign
     * @return Formatted amount string
     */
    public String getFormattedAmount() {
        return "₱" + (transactionAmount != null ? String.format("%,.2f", transactionAmount) : "0.00");
    }
    
    /**
     * Check if transfer is completed
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * Check if transfer is pending
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    /**
     * Check if transfer failed
     * @return true if status is FAILED
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }
    
    /**
     * Mark transfer as completed
     */
    public void markAsCompleted() {
        setStatus("COMPLETED");
    }
    
    /**
     * Mark transfer as failed
     */
    public void markAsFailed() {
        setStatus("FAILED");
    }
    
    /**
     * Mark transfer as cancelled
     */
    public void markAsCancelled() {
        setStatus("CANCELLED");
    }
    
    @Override
    public String toString() {
        return "CashTransfer{" +
                "transactionId=" + transactionId +
                ", transactionAmount=" + transactionAmount +
                ", transactionName='" + transactionName + '\'' +
                ", userId=" + userId +
                ", transactionDate=" + transactionDate +
                ", transferToAccountNo='" + transferToAccountNo + '\'' +
                ", transferFromAccountNo='" + transferFromAccountNo + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashTransfer that = (CashTransfer) o;
        return transactionId == that.transactionId;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

}
