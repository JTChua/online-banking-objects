package com.tesdaciicc.data.repository;

import com.tesdaciicc.model.CashTransfer;
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

public class CashTransferDAO {

     private static final Logger logger = LoggerFactory.getLogger(CashTransferDAO.class);
    
    // SQL queries for cash transfer operations
    private static final String INSERT_TRANSFER = 
        "INSERT INTO transactions (transactionAmount, transactionName, userId, transferToAccountNo, " +
        "transferFromAccountNo, accountNumber, transactionDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_TRANSFER_BY_ID = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions WHERE transactionId = ?";
    
    private static final String SELECT_TRANSFERS_BY_USER_ID = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions WHERE userId = ? " +
        "ORDER BY transactionDate DESC";
    
    private static final String SELECT_TRANSFERS_BY_ACCOUNT = 
        "SELECT transactionId, transactionAmount, transactionName, userId, transactionDate, " +
        "transferToAccountNo, transferFromAccountNo, accountNumber FROM transactions " +
        "WHERE transferToAccountNo = ? OR transferFromAccountNo = ? ORDER BY transactionDate DESC";
    
    private static final String SELECT_DAILY_TRANSFER_AMOUNT = 
        "SELECT COALESCE(SUM(transactionAmount), 0) FROM transactions " +
        "WHERE userId = ? AND transferFromAccountNo = ? AND DATE(transactionDate) = DATE('now')";
    
    private static final String COUNT_DAILY_TRANSFERS = 
        "SELECT COUNT(*) FROM transactions " +
        "WHERE userId = ? AND transferFromAccountNo = ? AND DATE(transactionDate) = DATE('now')";
    
    private static final String UPDATE_TRANSFER_STATUS = 
        "UPDATE transactions SET transactionName = ? WHERE transactionId = ?";
    
    /**
     * Save a new cash transfer transaction
     * @param transfer CashTransfer object to save
     * @return Optional containing saved transfer with generated ID, or empty if failed
     */
    public Optional<CashTransfer> save(CashTransfer transfer) {
        logger.debug("Saving cash transfer from {} to {}", 
                    transfer.getTransferFromAccountNo(), transfer.getTransferToAccountNo());
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(INSERT_TRANSFER, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setBigDecimal(1, transfer.getTransactionAmount());
            stmt.setString(2, transfer.getTransactionName());
            stmt.setInt(3, transfer.getUserId());
            stmt.setString(4, transfer.getTransferToAccountNo());
            stmt.setString(5, transfer.getTransferFromAccountNo());
            stmt.setString(6, transfer.getAccountNumber());
            stmt.setString(7, transfer.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transfer.setTransactionId(generatedKeys.getInt(1));
                        logger.info("Cash transfer saved successfully with ID: {}", transfer.getTransactionId());
                        return Optional.of(transfer);
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error saving cash transfer: {}", e.getMessage(), e);
        }
        
        return Optional.empty();
    }
    
    /**
     * Find transfer by transaction ID
     * @param transactionId Transaction ID to search for
     * @return Optional containing CashTransfer if found
     */
    public Optional<CashTransfer> findById(int transactionId) {
        logger.debug("Finding transfer by ID: {}", transactionId);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TRANSFER_BY_ID)) {
            
            stmt.setInt(1, transactionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CashTransfer transfer = mapResultSetToTransfer(rs);
                    logger.debug("Transfer found: {}", transfer);
                    return Optional.of(transfer);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error finding transfer by ID {}: {}", transactionId, e.getMessage(), e);
        }
        
        logger.debug("Transfer not found with ID: {}", transactionId);
        return Optional.empty();
    }
    
    /**
     * Find all transfers for a specific user
     * @param userId User ID
     * @return List of transfers for the user
     */
    public List<CashTransfer> findByUserId(int userId) {
        logger.debug("Finding transfers for user ID: {}", userId);
        List<CashTransfer> transfers = new ArrayList<>();
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TRANSFERS_BY_USER_ID)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transfers.add(mapResultSetToTransfer(rs));
                }
            }
            
            logger.debug("Found {} transfers for user ID: {}", transfers.size(), userId);
            
        } catch (SQLException e) {
            logger.error("Error finding transfers for user ID {}: {}", userId, e.getMessage(), e);
        }
        
        return transfers;
    }
    
    /**
     * Find all transfers involving a specific account number
     * @param accountNumber Account number (mobile number)
     * @return List of transfers involving the account
     */
    public List<CashTransfer> findByAccountNumber(String accountNumber) {
        logger.debug("Finding transfers for account: {}", accountNumber);
        List<CashTransfer> transfers = new ArrayList<>();
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_TRANSFERS_BY_ACCOUNT)) {
            
            stmt.setString(1, accountNumber);
            stmt.setString(2, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transfers.add(mapResultSetToTransfer(rs));
                }
            }
            
            logger.debug("Found {} transfers for account: {}", transfers.size(), accountNumber);
            
        } catch (SQLException e) {
            logger.error("Error finding transfers for account {}: {}", accountNumber, e.getMessage(), e);
        }
        
        return transfers;
    }
    
    /**
     * Get total amount transferred by a user today
     * @param userId User ID
     * @param accountNumber User's account number
     * @return Total amount transferred today
     */
    public BigDecimal getDailyTransferAmount(int userId, String accountNumber) {
        logger.debug("Getting daily transfer amount for user {} with account {}", userId, accountNumber);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(SELECT_DAILY_TRANSFER_AMOUNT)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal(1);
                    logger.debug("Daily transfer amount: {}", amount);
                    return amount != null ? amount : BigDecimal.ZERO;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting daily transfer amount for user {} with account {}: {}", 
                        userId, accountNumber, e.getMessage(), e);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Get count of transfers made by a user today
     * @param userId User ID
     * @param accountNumber User's account number
     * @return Number of transfers made today
     */
    public int getDailyTransferCount(int userId, String accountNumber) {
        logger.debug("Getting daily transfer count for user {} with account {}", userId, accountNumber);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(COUNT_DAILY_TRANSFERS)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, accountNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.debug("Daily transfer count: {}", count);
                    return count;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error getting daily transfer count for user {} with account {}: {}", 
                        userId, accountNumber, e.getMessage(), e);
        }
        
        return 0;
    }
    
    /**
     * Update transfer status (using transactionName field to store status)
     * @param transactionId Transaction ID
     * @param status New status
     * @return true if successful
     */
    public boolean updateStatus(int transactionId, String status) {
        logger.debug("Updating transfer {} status to: {}", transactionId, status);
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_TRANSFER_STATUS)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, transactionId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Transfer status updated successfully for ID: {}", transactionId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.error("Error updating transfer status for ID {}: {}", transactionId, e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to CashTransfer object
     * @param rs ResultSet containing transfer data
     * @return CashTransfer object
     * @throws SQLException if mapping fails
     */
    private CashTransfer mapResultSetToTransfer(ResultSet rs) throws SQLException {
        CashTransfer transfer = new CashTransfer();
        
        transfer.setTransactionId(rs.getInt("transactionId"));
        transfer.setTransactionAmount(rs.getBigDecimal("transactionAmount"));
        transfer.setTransactionName(rs.getString("transactionName"));
        transfer.setUserId(rs.getInt("userId"));
        transfer.setTransferToAccountNo(rs.getString("transferToAccountNo"));
        transfer.setTransferFromAccountNo(rs.getString("transferFromAccountNo"));
        transfer.setAccountNumber(rs.getString("accountNumber"));
        
        // Parse transaction date
        String dateStr = rs.getString("transactionDate");
        if (dateStr != null) {
            try {
                transfer.setTransactionDate(LocalDateTime.parse(dateStr.replace(" ", "T")));
            } catch (Exception e) {
                logger.warn("Error parsing transaction date: {}", dateStr);
                transfer.setTransactionDate(LocalDateTime.now());
            }
        }
        
        return transfer;
    }

}
