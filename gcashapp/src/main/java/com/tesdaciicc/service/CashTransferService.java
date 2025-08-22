package com.tesdaciicc.service;

import com.tesdaciicc.model.CashTransfer;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.model.Balance;
import com.tesdaciicc.data.repository.CashTransferDAO;
import com.tesdaciicc.data.repository.UserDAO;
import com.tesdaciicc.data.repository.BalanceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.tesdaciicc.data.util.ConnectionFactory;

public class CashTransferService {
    
    private static final Logger logger = LoggerFactory.getLogger(CashTransferService.class);
    
    // DAOs
    private final CashTransferDAO transferDAO;
    private final UserDAO userDAO;
    private final BalanceDAO balanceDAO;
    
    // Transfer limits and restrictions
    private static final BigDecimal MIN_TRANSFER_AMOUNT = BigDecimal.valueOf(1.00);
    private static final BigDecimal MAX_TRANSFER_AMOUNT = BigDecimal.valueOf(50000.00);
    private static final BigDecimal DAILY_TRANSFER_LIMIT = BigDecimal.valueOf(100000.00);
    private static final int MAX_DAILY_TRANSFERS = 20;
    private static final BigDecimal SERVICE_FEE = BigDecimal.valueOf(5.00);
    private static final BigDecimal FREE_TRANSFER_THRESHOLD = BigDecimal.valueOf(500.00);
    
    // Mobile number validation pattern (Philippine format: 09XXXXXXXXX)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^09\\d{9}$");
    
    public CashTransferService() {
        this.transferDAO = new CashTransferDAO();
        this.userDAO = new UserDAO();
        this.balanceDAO = new BalanceDAO();
    }
    
    /**
     * Transfer cash from one account to another
     * @param senderUserId Sender's user ID
     * @param recipientMobileNumber Recipient's 11-digit mobile number
     * @param amount Amount to transfer
     * @param description Transfer description/notes
     * @return TransferResult containing success/failure information
     */
    public TransferResult cashTransfer(int senderUserId, String recipientMobileNumber, 
                                     BigDecimal amount, String description) {
        
        logger.info("Initiating cash transfer: User {} -> {} Amount: {}", 
                   senderUserId, recipientMobileNumber, amount);
        
        // Step 1: Validate input parameters
        TransferResult validationResult = validateTransferInput(senderUserId, recipientMobileNumber, amount);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }
        
        // Step 2: Get sender information
        Optional<UserAuthentication> senderOpt = userDAO.findById(senderUserId);
        if (!senderOpt.isPresent()) {
            return TransferResult.failure("Sender account not found. Please log in again.");
        }
        
        UserAuthentication sender = senderOpt.get();
        
        // Step 3: Check sender's balance
        Optional<Balance> senderBalanceOpt = balanceDAO.findByUserId(senderUserId);
        if (!senderBalanceOpt.isPresent()) {
            return TransferResult.failure("Sender balance not found. Please contact support.");
        }
        
        Balance senderBalance = senderBalanceOpt.get();
        
        // Step 4: Calculate total amount including fees
        BigDecimal serviceFee = calculateServiceFee(amount);
        BigDecimal totalAmount = amount.add(serviceFee);
        
        // Step 5: Validate sender's balance
        TransferResult balanceResult = validateSenderBalance(senderBalance, totalAmount);
        if (!balanceResult.isSuccess()) {
            return balanceResult;
        }
        
        // Step 6: Check daily limits
        TransferResult limitResult = validateDailyLimits(senderUserId, sender.getNumber(), amount);
        if (!limitResult.isSuccess()) {
            return limitResult;
        }
        
        // Step 7: Validate recipient
        TransferResult recipientResult = validateRecipient(recipientMobileNumber, sender.getNumber());
        if (!recipientResult.isSuccess()) {
            return recipientResult;
        }
        
        Optional<UserAuthentication> recipientOpt = userDAO.findByNumber(recipientMobileNumber);
        if (!recipientOpt.isPresent()) {
            return TransferResult.failure("Recipient account not found. Please verify the mobile number.");
        }
        
        UserAuthentication recipient = recipientOpt.get();
        
        // Step 8: Get recipient's balance
        Optional<Balance> recipientBalanceOpt = balanceDAO.findByUserId(recipient.getId());
        if (!recipientBalanceOpt.isPresent()) {
            return TransferResult.failure("Recipient balance not found. Transfer cannot proceed.");
        }
        
        Balance recipientBalance = recipientBalanceOpt.get();
        
        // Step 9: Execute transfer using database transaction
        return executeTransfer(sender, recipient, senderBalance, recipientBalance, amount, serviceFee, description);
    }
    
    /**
     * Validate transfer input parameters
     */
    private TransferResult validateTransferInput(int senderUserId, String recipientMobileNumber, BigDecimal amount) {
        // Validate sender ID
        if (senderUserId <= 0) {
            return TransferResult.failure("Invalid sender ID.");
        }
        
        // Validate recipient mobile number
        if (recipientMobileNumber == null || recipientMobileNumber.trim().isEmpty()) {
            return TransferResult.failure("Recipient mobile number is required.");
        }
        
        if (!MOBILE_PATTERN.matcher(recipientMobileNumber).matches()) {
            return TransferResult.failure("Invalid mobile number format. Must be 11 digits starting with 09.");
        }
        
        // Validate amount
        if (amount == null) {
            return TransferResult.failure("Transfer amount is required.");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return TransferResult.failure("Transfer amount must be greater than zero.");
        }
        
        if (amount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            return TransferResult.failure("Minimum transfer amount is ₱" + MIN_TRANSFER_AMOUNT);
        }
        
        if (amount.compareTo(MAX_TRANSFER_AMOUNT) > 0) {
            return TransferResult.failure("Maximum transfer amount is ₱" + String.format("%,.2f", MAX_TRANSFER_AMOUNT));
        }
        
        return TransferResult.success("Input validation passed.", null);
    }
    
    /**
     * Validate sender's balance
     */
    private TransferResult validateSenderBalance(Balance senderBalance, BigDecimal totalAmount) {
        if (!senderBalance.isSufficientFor(totalAmount)) {
            return TransferResult.failure(String.format(
                "Insufficient balance. Required: ₱%,.2f, Available: ₱%,.2f", 
                totalAmount, senderBalance.getAmount()
            ));
        }
        
        // Check if balance would go negative
        BigDecimal remainingBalance = senderBalance.getAmount().subtract(totalAmount);
        if (remainingBalance.compareTo(BigDecimal.ZERO) < 0) {
            return TransferResult.failure("Transfer would result in negative balance.");
        }
        
        return TransferResult.success("Balance validation passed.", null);
    }
    
    /**
     * Validate daily transfer limits
     */
    private TransferResult validateDailyLimits(int senderUserId, String senderMobileNumber, BigDecimal amount) {
        // Check daily transfer count
        int dailyCount = transferDAO.getDailyTransferCount(senderUserId, senderMobileNumber);
        if (dailyCount >= MAX_DAILY_TRANSFERS) {
            return TransferResult.failure("Daily transfer limit exceeded. Maximum " + MAX_DAILY_TRANSFERS + " transfers per day.");
        }
        
        // Check daily transfer amount
        BigDecimal dailyAmount = transferDAO.getDailyTransferAmount(senderUserId, senderMobileNumber);
        BigDecimal totalDailyAmount = dailyAmount.add(amount);
        
        if (totalDailyAmount.compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            return TransferResult.failure(String.format(
                "Daily transfer limit exceeded. Limit: ₱%,.2f, Already used: ₱%,.2f, Requested: ₱%,.2f",
                DAILY_TRANSFER_LIMIT, dailyAmount, amount
            ));
        }
        
        return TransferResult.success("Daily limits validation passed.", null);
    }
    
    /**
     * Validate recipient
     */
    private TransferResult validateRecipient(String recipientMobileNumber, String senderMobileNumber) {
        // Prevent self-transfer
        if (recipientMobileNumber.equals(senderMobileNumber)) {
            return TransferResult.failure("Cannot transfer to your own account.");
        }
        
        return TransferResult.success("Recipient validation passed.", null);
    }
    
    /**
     * Calculate service fee based on transfer amount
     */
    private BigDecimal calculateServiceFee(BigDecimal amount) {
        // Free transfers for amounts above threshold
        if (amount.compareTo(FREE_TRANSFER_THRESHOLD) >= 0) {
            return BigDecimal.ZERO;
        }
        return SERVICE_FEE;
    }
    
    /**
     * Execute the actual transfer using database transaction
     */
    private TransferResult executeTransfer(UserAuthentication sender, UserAuthentication recipient,
                                         Balance senderBalance, Balance recipientBalance,
                                         BigDecimal amount, BigDecimal serviceFee, String description) {
        
        Connection connection = null;
        
        try {
            // Start database transaction
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);
            
            // Step 1: Deduct amount and service fee from sender's balance
            BigDecimal totalDeduction = amount.add(serviceFee);
            BigDecimal newSenderBalance = senderBalance.getAmount().subtract(totalDeduction);
            
            boolean senderUpdated = balanceDAO.updateBalance(sender.getId(), newSenderBalance);
            if (!senderUpdated) {
                connection.rollback();
                return TransferResult.failure("Failed to update sender's balance. Transfer cancelled.");
            }
            
            // Step 2: Add amount to recipient's balance
            BigDecimal newRecipientBalance = recipientBalance.getAmount().add(amount);
            
            boolean recipientUpdated = balanceDAO.updateBalance(recipient.getId(), newRecipientBalance);
            if (!recipientUpdated) {
                connection.rollback();
                return TransferResult.failure("Failed to update recipient's balance. Transfer cancelled.");
            }
            
            // Step 3: Create transfer record
            CashTransfer transfer = new CashTransfer(
                amount,
                "CASH_TRANSFER_COMPLETED",
                sender.getId(),
                recipient.getNumber(),
                sender.getNumber(),
                description != null ? description : "Cash Transfer"
            );
            
            Optional<CashTransfer> savedTransfer = transferDAO.save(transfer);
            if (!savedTransfer.isPresent()) {
                connection.rollback();
                return TransferResult.failure("Failed to record transfer transaction. Transfer cancelled.");
            }
            
            // Commit transaction
            connection.commit();
            
            logger.info("Cash transfer completed successfully: {} -> {} Amount: ₱{}", 
                       sender.getNumber(), recipient.getNumber(), amount);
            
            // Create success result with transfer details
            return TransferResult.success(
                String.format("Transfer successful! ₱%,.2f sent to %s. Service fee: ₱%,.2f", 
                             amount, recipient.getNumber(), serviceFee),
                savedTransfer.get()
            );
            
        } catch (SQLException e) {
            logger.error("Database error during transfer execution: {}", e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error rolling back transaction: {}", rollbackEx.getMessage(), rollbackEx);
            }
            return TransferResult.failure("Database error occurred. Transfer cancelled.");
            
        } catch (Exception e) {
            logger.error("Unexpected error during transfer execution: {}", e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error rolling back transaction: {}", rollbackEx.getMessage(), rollbackEx);
            }
            return TransferResult.failure("Unexpected error occurred. Transfer cancelled.");
            
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * Get transfer history for a user
     * @param userId User ID
     * @return List of transfers
     */
    public List<CashTransfer> getTransferHistory(int userId) {
        logger.debug("Getting transfer history for user: {}", userId);
        return transferDAO.findByUserId(userId);
    }
    
    /**
     * Get transfer history for an account number
     * @param accountNumber Account number (mobile number)
     * @return List of transfers
     */
    public List<CashTransfer> getTransferHistory(String accountNumber) {
        logger.debug("Getting transfer history for account: {}", accountNumber);
        return transferDAO.findByAccountNumber(accountNumber);
    }
    
    /**
     * Find transfer by transaction ID
     * @param transactionId Transaction ID
     * @return Optional containing transfer if found
     */
    public Optional<CashTransfer> findTransfer(int transactionId) {
        return transferDAO.findById(transactionId);
    }
    
    /**
     * Get daily transfer summary for a user
     * @param userId User ID
     * @param accountNumber Account number
     * @return DailyTransferSummary
     */
    public DailyTransferSummary getDailyTransferSummary(int userId, String accountNumber) {
        BigDecimal dailyAmount = transferDAO.getDailyTransferAmount(userId, accountNumber);
        int dailyCount = transferDAO.getDailyTransferCount(userId, accountNumber);
        
        return new DailyTransferSummary(
            dailyAmount,
            dailyCount,
            DAILY_TRANSFER_LIMIT.subtract(dailyAmount),
            MAX_DAILY_TRANSFERS - dailyCount
        );
    }
    
    /**
     * Calculate service fee for preview
     * @param amount Transfer amount
     * @return Service fee amount
     */
    public BigDecimal previewServiceFee(BigDecimal amount) {
        return calculateServiceFee(amount);
    }
    
    /**
     * Validate mobile number format
     * @param mobileNumber Mobile number to validate
     * @return true if valid format
     */
    public boolean isValidMobileNumber(String mobileNumber) {
        return mobileNumber != null && MOBILE_PATTERN.matcher(mobileNumber).matches();
    }
    
    /**
     * Check if recipient exists
     * @param mobileNumber Recipient's mobile number
     * @return true if recipient exists
     */
    public boolean recipientExists(String mobileNumber) {
        return userDAO.findByNumber(mobileNumber).isPresent();
    }
    
    /**
     * Get recipient name by mobile number
     * @param mobileNumber Mobile number
     * @return Recipient name or null if not found
     */
    public String getRecipientName(String mobileNumber) {
        Optional<UserAuthentication> recipient = userDAO.findByNumber(mobileNumber);
        return recipient.map(UserAuthentication::getName).orElse(null);
    }
    
    // Inner classes for result handling
    
    /**
     * Transfer result class to encapsulate success/failure information
     */
    public static class TransferResult {
        private final boolean success;
        private final String message;
        private final CashTransfer transfer;
        
        private TransferResult(boolean success, String message, CashTransfer transfer) {
            this.success = success;
            this.message = message;
            this.transfer = transfer;
        }
        
        public static TransferResult success(String message, CashTransfer transfer) {
            return new TransferResult(true, message, transfer);
        }
        
        public static TransferResult failure(String message) {
            return new TransferResult(false, message, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public CashTransfer getTransfer() {
            return transfer;
        }
        
        @Override
        public String toString() {
            return "TransferResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", transfer=" + transfer +
                    '}';
        }
    }
    
    /**
     * Daily transfer summary class
     */
    public static class DailyTransferSummary {
        private final BigDecimal totalAmountTransferred;
        private final int totalTransactions;
        private final BigDecimal remainingLimit;
        private final int remainingTransactions;
        
        public DailyTransferSummary(BigDecimal totalAmountTransferred, int totalTransactions,
                                  BigDecimal remainingLimit, int remainingTransactions) {
            this.totalAmountTransferred = totalAmountTransferred;
            this.totalTransactions = totalTransactions;
            this.remainingLimit = remainingLimit;
            this.remainingTransactions = remainingTransactions;
        }
        
        public BigDecimal getTotalAmountTransferred() {
            return totalAmountTransferred;
        }
        
        public int getTotalTransactions() {
            return totalTransactions;
        }
        
        public BigDecimal getRemainingLimit() {
            return remainingLimit;
        }
        
        public int getRemainingTransactions() {
            return remainingTransactions;
        }
        
        public String getFormattedTotalAmount() {
            return "₱" + String.format("%,.2f", totalAmountTransferred);
        }
        
        public String getFormattedRemainingLimit() {
            return "₱" + String.format("%,.2f", remainingLimit);
        }
        
        @Override
        public String toString() {
            return "DailyTransferSummary{" +
                    "totalAmountTransferred=" + totalAmountTransferred +
                    ", totalTransactions=" + totalTransactions +
                    ", remainingLimit=" + remainingLimit +
                    ", remainingTransactions=" + remainingTransactions +
                    '}';
        }
    }
}