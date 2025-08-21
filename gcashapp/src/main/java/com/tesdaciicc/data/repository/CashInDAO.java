package com.tesdaciicc.data.repository;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.model.CashIn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for CashIn transactions
 * Focuses solely on CRUD operations for transaction data
 */
public class CashInDAO {

    private static final Logger logger = LoggerFactory.getLogger(CashInDAO.class);
    
    // SQL queries matching your existing CashIn table schema
    private static final String INSERT_TRANSACTION = """
        INSERT INTO transactions (transactionAmount, name, userId, transactionDate, 
                               transferToAccountNo, transferFromAccountNo, accountNumber)VALUES (?, ?, ?, ?, ?, ?, ?)"""; 

    private static final String SELECT_BY_ID = "SELECT * FROM transactions WHERE transactionId = ?";
    private static final String SELECT_BY_USER_ID = "SELECT * FROM transactions WHERE userId = ? ORDER BY transactionDate DESC";
    private static final String SELECT_BY_ACCOUNT_NUMBER = "SELECT * FROM transactions WHERE accountNumber = ? ORDER BY transactionDate DESC";
    private static final String SELECT_ALL = "SELECT * FROM transactions ORDER BY transactionDate DESC";
    private static final String SELECT_TOTAL_BY_USER_ID = "SELECT COALESCE(SUM(transactionAmount), 0) as total FROM transactions WHERE userId = ?";
    private static final String COUNT_BY_USER_ID = "SELECT COUNT(*) as count FROM transactions WHERE userId = ?";

    /**
     * Create a new cash-in transaction record
     * 
     * @param cashIn The cash-in transaction to create
     * @return Optional containing the created transaction with generated ID
     */
    public Optional<CashIn> create(CashIn cashIn) {
        logger.debug("Creating cash-in transaction for userId: {}", cashIn.getUserId());

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBigDecimal(1, cashIn.getTransactionAmount());
            stmt.setString(2, cashIn.getName());
            stmt.setInt(3, cashIn.getUserId());
            stmt.setString(4, cashIn.getTransactionDate().toString());
            stmt.setString(5, cashIn.getTransferToAccountNo());
            stmt.setString(6, cashIn.getTransferFromAccountNo());
            stmt.setString(7, cashIn.getAccountNumber());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cashIn.setTransactionId(generatedKeys.getInt(1));
                        logger.info("Cash-in transaction created successfully with ID: {}", cashIn.getTransactionId());
                        return Optional.of(cashIn);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("Error creating cash-in transaction for userId {}: {}", cashIn.getUserId(), e.getMessage(), e);
        }

        return Optional.empty();
    }

    /**
     * Find transaction by ID
     * 
     * @param transactionId Transaction ID
     * @return Optional containing the transaction if found
     */
    public Optional<CashIn> findById(int transactionId) {
        logger.debug("Finding transaction by ID: {}", transactionId);

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, transactionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CashIn transaction = mapResultSetToCashIn(rs);
                    logger.debug("Transaction found with ID: {}", transactionId);
                    return Optional.of(transaction);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding transaction by ID {}: {}", transactionId, e.getMessage(), e);
        }

        logger.debug("Transaction not found with ID: {}", transactionId);
        return Optional.empty();
    }

    /**
     * Find all transactions by user ID
     * 
     * @param userId User ID
     * @return List of transactions
     */
    public List<CashIn> findByUserId(int userId) {
        logger.debug("Finding transactions for userId: {}", userId);
        List<CashIn> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCashIn(rs));
                }
            }

            logger.debug("Found {} transactions for userId: {}", transactions.size(), userId);

        } catch (SQLException e) {
            logger.error("Error finding transactions for userId {}: {}", userId, e.getMessage(), e);
        }

        return transactions;
    }

    /**
     * Find all transactions by account number
     * 
     * @param accountNumber Account number
     * @return List of transactions
     */
    public List<CashIn> findByAccountNumber(String accountNumber) {
        logger.debug("Finding transactions for account number: {}", accountNumber);
        List<CashIn> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ACCOUNT_NUMBER)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToCashIn(rs));
                }
            }

            logger.debug("Found {} transactions for account number: {}", transactions.size(), accountNumber);

        } catch (SQLException e) {
            logger.error("Error finding transactions for account number {}: {}", accountNumber, e.getMessage(), e);
        }

        return transactions;
    }

    /**
     * Find all transactions
     * 
     * @return List of all transactions
     */
    public List<CashIn> findAll() {
        logger.debug("Finding all transactions");
        List<CashIn> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                transactions.add(mapResultSetToCashIn(rs));
            }

            logger.debug("Found {} transactions", transactions.size());

        } catch (SQLException e) {
            logger.error("Error finding all transactions: {}", e.getMessage(), e);
        }

        return transactions;
    }

    /**
     * Get total cash-in amount for a user
     * 
     * @param userId User ID
     * @return Total amount
     */
    public BigDecimal getTotalByUserId(int userId) {
        logger.debug("Calculating total cash-in for userId: {}", userId);

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TOTAL_BY_USER_ID)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    logger.debug("Total cash-in for userId {}: {}", userId, total);
                    return total;
                }
            }

        } catch (SQLException e) {
            logger.error("Error calculating total cash-in for userId {}: {}", userId, e.getMessage(), e);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Get transaction count for a user
     * 
     * @param userId User ID
     * @return Transaction count
     */
    public int countByUserId(int userId) {
        logger.debug("Counting transactions for userId: {}", userId);

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_BY_USER_ID)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    logger.debug("Transaction count for userId {}: {}", userId, count);
                    return count;
                }
            }

        } catch (SQLException e) {
            logger.error("Error counting transactions for userId {}: {}", userId, e.getMessage(), e);
        }

        return 0;
    }

    /**
     * Maps ResultSet to CashIn object
     */
    private CashIn mapResultSetToCashIn(ResultSet rs) throws SQLException {
        return new CashIn(
            rs.getInt("transactionId"),
            rs.getBigDecimal("transactionAmount"),
            rs.getString("name"),
            rs.getInt("userId"),
            LocalDateTime.parse(rs.getString("transactionDate")),
            rs.getString("transferToAccountNo"),
            rs.getString("transferFromAccountNo"),
            rs.getString("accountNumber")
        );
    }

}
