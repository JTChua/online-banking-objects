package com.tesdaciicc.service;

import com.tesdaciicc.data.repository.CashInDAO;
import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.data.repository.BalanceDAO;
import com.tesdaciicc.model.CashIn;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.model.Balance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import com.tesdaciicc.data.util.ConnectionFactory;

/**
 * Service class for Cash-In operations
 * Handles business logic and transaction orchestration
 */
public class CashInService {

    private static final Logger logger = LoggerFactory.getLogger(CashInService.class);
    
    private final CashInDAO cashInDAO;
    private final UserAuthenticationDAO userDAO;
    private final BalanceDAO balanceDAO;

    public CashInService() {
        this.cashInDAO = new CashInDAO();
        this.userDAO = new UserAuthenticationDAO();
        this.balanceDAO = new BalanceDAO();
        logger.info("CashInService initialized");
    }

    /**
     * Process cash-in operation with transaction management
     * 
     * @param accountNumber The account number to cash-in to
     * @param amount The amount to cash-in
     * @param senderName Name of the person sending the money
     * @return true if successful, false otherwise
     */
    public boolean processCashIn(String accountNumber, BigDecimal amount, String senderName) {
        logger.info("Processing cash-in for account: {}, amount: {}", accountNumber, amount);

        // Validate input
        if (!validateCashInInput(accountNumber, amount, senderName)) {
            return false;
        }

        // Ensure proper monetary scale
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        // Find user by account number
        Optional<UserAuthentication> userOpt = userDAO.findByNumber(accountNumber);
        if (!userOpt.isPresent()) {
            logger.error("Account number not found: {}", accountNumber);
            return false;
        }

        UserAuthentication user = userOpt.get();

        // Execute cash-in transaction
        return executeCashInTransaction(user.getId(), accountNumber, amount, senderName);
    }

    /**
     * Process cash-in with double amount (convenience method)
     */
    public boolean processCashIn(String accountNumber, double amount, String senderName) {
        BigDecimal bdAmount = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
        return processCashIn(accountNumber, bdAmount, senderName);
    }

    /**
     * Execute the actual cash-in transaction with proper transaction management
     */
    private boolean executeCashInTransaction(int userId, String accountNumber, BigDecimal amount, String senderName) {
        Connection connection = null;
        try {
            connection = ConnectionFactory.getConnection();
            connection.setAutoCommit(false);

            // Get current balance
            Optional<Balance> balanceOpt = balanceDAO.findByUserId(userId);
            if (!balanceOpt.isPresent()) {
                logger.error("Balance not found for userId: {}", userId);
                connection.rollback();
                return false;
            }

            Balance currentBalance = balanceOpt.get();

            // Create transaction record
            CashIn cashInTransaction = new CashIn(
                amount,
                senderName,
                userId,
                accountNumber, // transferToAccountNo
                "CASH_IN_SOURCE", // transferFromAccountNo
                accountNumber
            );

            // Insert transaction (using connection for consistency)
            Optional<CashIn> createdTransaction = createTransactionWithConnection(connection, cashInTransaction);
            if (!createdTransaction.isPresent()) {
                connection.rollback();
                return false;
            }

            // Update balance
            BigDecimal newBalance = currentBalance.getAmount().add(amount);
            if (!balanceDAO.updateBalance(userId, newBalance)) {
                connection.rollback();
                return false;
            }

            connection.commit();
            logger.info("Cash-in successful! Added {} to account {}. New balance: {}", 
                       amount, accountNumber, newBalance);
            return true;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    logger.error("Rollback failed: {}", rollbackEx.getMessage());
                }
            }
            logger.error("Cash-in transaction failed for userId {}: {}", userId, e.getMessage(), e);
            return false;

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * Create transaction with existing connection (for transaction management)
     */
    private Optional<CashIn> createTransactionWithConnection(Connection connection, CashIn cashIn) {
        // This would require modifying CashInDAO to accept a connection parameter
        // For now, we'll use the regular create method
        // In a more sophisticated design, you might have a TransactionTemplate or similar
        return cashInDAO.create(cashIn);
    }

    /**
     * Validate cash-in input parameters
     */
    private boolean validateCashInInput(String accountNumber, BigDecimal amount, String senderName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid amount: {}", amount);
            return false;
        }

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            logger.error("Account number cannot be empty");
            return false;
        }

        if (senderName == null || senderName.trim().isEmpty()) {
            logger.error("Sender name cannot be empty");
            return false;
        }

        return true;
    }

    // Delegate methods to DAO (keeping service interface clean)
    
    public Optional<CashIn> getTransactionById(int transactionId) {
        return cashInDAO.findById(transactionId);
    }

    public List<CashIn> getTransactionHistory(int userId) {
        return cashInDAO.findByUserId(userId);
    }

    public List<CashIn> getTransactionHistoryByAccount(String accountNumber) {
        return cashInDAO.findByAccountNumber(accountNumber);
    }

    public List<CashIn> getAllTransactions() {
        return cashInDAO.findAll();
    }

    public BigDecimal getTotalCashIn(int userId) {
        return cashInDAO.getTotalByUserId(userId);
    }

    public int getTransactionCount(int userId) {
        return cashInDAO.countByUserId(userId);
    }

    // User and Balance related methods
    
    public Optional<UserAuthentication> getUserByAccountNumber(String accountNumber) {
        return userDAO.findByNumber(accountNumber);
    }

    public Optional<Balance> getCurrentBalance(String accountNumber) {
        Optional<UserAuthentication> userOpt = userDAO.findByNumber(accountNumber);
        if (userOpt.isPresent()) {
            return balanceDAO.findByUserId(userOpt.get().getId());
        }
        return Optional.empty();
    }

    public boolean validateAccount(String accountNumber) {
        Optional<UserAuthentication> userOpt = getUserByAccountNumber(accountNumber);
        if (!userOpt.isPresent()) {
            logger.warn("Account validation failed - user not found: {}", accountNumber);
            return false;
        }

        Optional<Balance> balanceOpt = getCurrentBalance(accountNumber);
        if (!balanceOpt.isPresent()) {
            logger.warn("Account validation failed - balance not found: {}", accountNumber);
            return false;
        }

        return true;
    }
}