package com.tesdaciicc.data.repository;

import com.tesdaciicc.model.Transactions;
import com.tesdaciicc.data.util.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Transactions table operations
 */
public class TransactionsDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionsDAO.class);
    
    // SQL queries for transactions operations
    private static final String SELECT_ALL_TRANSACTIONS = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions " +
        "ORDER BY transactionDate DESC";
    
    private static final String SELECT_TRANSACTIONS_BY_USER_ID = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions " +
        "WHERE userId = ? ORDER BY transactionDate DESC";
    
    private static final String SELECT_TRANSACTION_BY_ID = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions " +
        "WHERE transactionId = ?";
    
    private static final String COUNT_ALL_TRANSACTIONS = 
        "SELECT COUNT(*) FROM transactions";
    
    private static final String COUNT_USER_TRANSACTIONS = 
        "SELECT COUNT(*) FROM transactions WHERE userId = ?";
    
    /**
     * View all transactions from the database
     * @return List of all transactions ordered by date (newest first)
     */
    public List<Transactions> viewAll() {
        logger.debug("Retrieving all transactions from database");
        List<Transactions> transactions = new ArrayList<>();
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_TRANSACTIONS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
            logger.info("Retrieved {} transactions from database", transactions.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving all transactions: {}", e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * View all transactions for a specific user
     * @param userId User ID to filter transactions
     * @return List of transactions for the specified user ordered by date (newest first)
     */
    public List<Transactions> viewUserAll(int userId) {
        logger.debug("Retrieving all transactions for user ID: {}", userId);
        List<Transactions> transactions = new ArrayList<>();
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TRANSACTIONS_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            logger.info("Retrieved {} transactions for user ID: {}", transactions.size(), userId);
            
        } catch (SQLException e) {
            logger.error("Error retrieving transactions for user ID {}: {}", userId, e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * View a specific transaction by transaction ID
     * @param transactionId Transaction ID to retrieve
     * @return Optional containing the transaction if found, empty otherwise
     */
    public Optional<Transactions> viewTransaction(int transactionId) {
        logger.debug("Retrieving transaction by ID: {}", transactionId);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TRANSACTION_BY_ID)) {
            
            stmt.setInt(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transactions transaction = mapResultSetToTransaction(rs);
                    logger.debug("Transaction found: {}", transaction);
                    return Optional.of(transaction);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving transaction by ID {}: {}", transactionId, e.getMessage(), e);
        }
        
        logger.debug("Transaction not found with ID: {}", transactionId);
        return Optional.empty();
    }
    
    /**
     * Get total count of all transactions
     * @return Total number of transactions in the database
     */
    public long getTotalTransactionCount() {
        logger.debug("Getting total transaction count");
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_ALL_TRANSACTIONS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                long count = rs.getLong(1);
                logger.debug("Total transaction count: {}", count);
                return count;
            }
            
        } catch (SQLException e) {
            logger.error("Error getting total transaction count: {}", e.getMessage(), e);
        }
        
        return 0;
    }
    
    /**
     * Get total count of transactions for a specific user
     * @param userId User ID
     * @return Total number of transactions for the user
     */
    public long getUserTransactionCount(int userId) {
        logger.debug("Getting transaction count for user ID: {}", userId);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_USER_TRANSACTIONS)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long count = rs.getLong(1);
                    logger.debug("Transaction count for user {}: {}", userId, count);
                    return count;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting transaction count for user ID {}: {}", userId, e.getMessage(), e);
        }
        
        return 0;
    }
    
    /**
     * Get transactions with pagination support
     * @param offset Starting position
     * @param limit Number of transactions to retrieve
     * @return List of transactions with pagination
     */
    public List<Transactions> viewAllWithPagination(int offset, int limit) {
        logger.debug("Retrieving transactions with pagination: offset={}, limit={}", offset, limit);
        List<Transactions> transactions = new ArrayList<>();
        
        String sql = SELECT_ALL_TRANSACTIONS + " LIMIT ? OFFSET ?";
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            logger.info("Retrieved {} transactions with pagination (offset: {}, limit: {})", 
                       transactions.size(), offset, limit);
            
        } catch (SQLException e) {
            logger.error("Error retrieving transactions with pagination: {}", e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * Get user transactions with pagination support
     * @param userId User ID
     * @param offset Starting position
     * @param limit Number of transactions to retrieve
     * @return List of user transactions with pagination
     */
    public List<Transactions> viewUserAllWithPagination(int userId, int offset, int limit) {
        logger.debug("Retrieving user transactions with pagination: userId={}, offset={}, limit={}", 
                    userId, offset, limit);
        List<Transactions> transactions = new ArrayList<>();
        
        String sql = SELECT_TRANSACTIONS_BY_USER_ID + " LIMIT ? OFFSET ?";
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            logger.info("Retrieved {} user transactions with pagination (userId: {}, offset: {}, limit: {})", 
                       transactions.size(), userId, offset, limit);
            
        } catch (SQLException e) {
            logger.error("Error retrieving user transactions with pagination: {}", e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * Search transactions by transaction name
     * @param searchTerm Search term to match against transaction name
     * @return List of transactions matching the search term
     */
    public List<Transactions> searchTransactionsByName(String searchTerm) {
        logger.debug("Searching transactions by name: {}", searchTerm);
        List<Transactions> transactions = new ArrayList<>();
        
        String sql = SELECT_ALL_TRANSACTIONS.replace("ORDER BY", 
                    "WHERE LOWER(transactionName) LIKE LOWER(?) ORDER BY");
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            logger.info("Found {} transactions matching search term: {}", transactions.size(), searchTerm);
            
        } catch (SQLException e) {
            logger.error("Error searching transactions by name: {}", e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * Get transactions within a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of transactions within the date range
     */
    public List<Transactions> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Retrieving transactions between {} and {}", startDate, endDate);
        List<Transactions> transactions = new ArrayList<>();
        
        String sql = SELECT_ALL_TRANSACTIONS.replace("ORDER BY", 
                    "WHERE transactionDate BETWEEN ? AND ? ORDER BY");
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(2, endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
            
            logger.info("Retrieved {} transactions between {} and {}", 
                       transactions.size(), startDate, endDate);
            
        } catch (SQLException e) {
            logger.error("Error retrieving transactions by date range: {}", e.getMessage(), e);
        }
        
        return transactions;
    }
    
    /**
     * Map ResultSet to Transactions object
     * @param rs ResultSet containing transaction data
     * @return Transactions object
     * @throws SQLException if mapping fails
     */
    private Transactions mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transactions transaction = new Transactions();
        
        transaction.setTransactionId(rs.getInt("transactionId"));
        transaction.setTransactionAmount(rs.getBigDecimal("transactionAmount"));
        transaction.setTransactionName(rs.getString("transactionName"));
        transaction.setUserId(rs.getInt("userId"));
        transaction.setTransferToAccountNo(rs.getString("transferToAccountNo"));
        transaction.setTransferFromAccountNo(rs.getString("transferFromAccountNo"));
        transaction.setAccountNumber(rs.getString("accountNumber"));
        
        // Parse transaction date
        String dateStr = rs.getString("transactionDate");
        if (dateStr != null) {
            try {
                // Handle different date formats
                if (dateStr.contains("T")) {
                    transaction.setTransactionDate(LocalDateTime.parse(dateStr));
                } else {
                    transaction.setTransactionDate(LocalDateTime.parse(dateStr.replace(" ", "T")));
                }
            } catch (Exception e) {
                logger.warn("Error parsing transaction date: {}", dateStr);
                transaction.setTransactionDate(LocalDateTime.now());
            }
        }
        
        return transaction;
    }
}