package com.tesdaciicc.service;

import com.tesdaciicc.model.Transactions;
import com.tesdaciicc.data.repository.TransactionsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Transactions business logic
 * Provides business layer operations for transaction management
 */
public class TransactionsService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionsService.class);
    
    private final TransactionsDAO transactionsDAO;
    
    public TransactionsService() {
        this.transactionsDAO = new TransactionsDAO();
    }
    
    // Constructor for dependency injection (testing purposes)
    public TransactionsService(TransactionsDAO transactionsDAO) {
        this.transactionsDAO = transactionsDAO;
    }
    
    /**
     * View all transactions from the database
     * @return List of all transactions with business logic applied
     */
    public List<Transactions> viewAll() {
        logger.info("Retrieving all transactions via service layer");
        
        try {
            List<Transactions> transactions = transactionsDAO.viewAll();
            
            // Apply business logic if needed
            // For example, filter out invalid transactions, apply formatting, etc.
            List<Transactions> validTransactions = transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
            
            logger.info("Service returned {} valid transactions out of {} total", 
                       validTransactions.size(), transactions.size());
            
            return validTransactions;
            
        } catch (Exception e) {
            logger.error("Error in service layer while retrieving all transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve all transactions", e);
        }
    }
    
    /**
     * View all transactions for a specific user
     * @param userId User ID to filter transactions
     * @return List of transactions for the specified user
     */
    public List<Transactions> viewUserAll(int userId) {
        logger.info("Retrieving all transactions for user ID: {} via service layer", userId);
        
        // Validate input
        if (userId <= 0) {
            logger.warn("Invalid user ID provided: {}", userId);
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        
        try {
            List<Transactions> transactions = transactionsDAO.viewUserAll(userId);
            
            // Apply business logic for user-specific transactions
            List<Transactions> validTransactions = transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
            
            logger.info("Service returned {} valid transactions for user {} out of {} total", 
                       validTransactions.size(), userId, transactions.size());
            
            return validTransactions;
            
        } catch (Exception e) {
            logger.error("Error in service layer while retrieving transactions for user {}: {}", 
                        userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions for user " + userId, e);
        }
    }
    
    /**
     * View a specific transaction by transaction ID
     * @param transactionId Transaction ID to retrieve
     * @return Optional containing the transaction if found and valid
     */
    public Optional<Transactions> viewTransaction(int transactionId) {
        logger.info("Retrieving transaction by ID: {} via service layer", transactionId);
        
        // Validate input
        if (transactionId <= 0) {
            logger.warn("Invalid transaction ID provided: {}", transactionId);
            throw new IllegalArgumentException("Transaction ID must be a positive integer");
        }
        
        try {
            Optional<Transactions> transactionOpt = transactionsDAO.viewTransaction(transactionId);
            
            if (transactionOpt.isPresent()) {
                Transactions transaction = transactionOpt.get();
                
                // Apply business validation
                if (isValidTransaction(transaction)) {
                    logger.info("Service returned valid transaction with ID: {}", transactionId);
                    return Optional.of(transaction);
                } else {
                    logger.warn("Transaction with ID {} failed business validation", transactionId);
                    return Optional.empty();
                }
            } else {
                logger.info("Transaction with ID {} not found", transactionId);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            logger.error("Error in service layer while retrieving transaction {}: {}", 
                        transactionId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transaction " + transactionId, e);
        }
    }
    
    /**
     * Get transaction statistics for all transactions
     * @return TransactionStatistics object with summary information
     */
    public TransactionStatistics getAllTransactionStatistics() {
        logger.info("Generating statistics for all transactions");
        
        try {
            List<Transactions> allTransactions = viewAll();
            return generateStatistics(allTransactions, "All Transactions");
            
        } catch (Exception e) {
            logger.error("Error generating all transaction statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate transaction statistics", e);
        }
    }
    
    /**
     * Get transaction statistics for a specific user
     * @param userId User ID
     * @return TransactionStatistics object with user-specific summary information
     */
    public TransactionStatistics getUserTransactionStatistics(int userId) {
        logger.info("Generating statistics for user ID: {}", userId);
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        
        try {
            List<Transactions> userTransactions = viewUserAll(userId);
            return generateStatistics(userTransactions, "User " + userId + " Transactions");
            
        } catch (Exception e) {
            logger.error("Error generating user {} transaction statistics: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate user transaction statistics", e);
        }
    }
    
    /**
     * Get transactions with pagination
     * @param page Page number (1-based)
     * @param pageSize Number of transactions per page
     * @return Paginated list of transactions
     */
    public List<Transactions> viewAllPaginated(int page, int pageSize) {
        logger.info("Retrieving paginated transactions: page={}, pageSize={}", page, pageSize);
        
        if (page <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page and page size must be positive integers");
        }
        
        try {
            int offset = (page - 1) * pageSize;
            List<Transactions> transactions = transactionsDAO.viewAllWithPagination(offset, pageSize);
            
            return transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error retrieving paginated transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve paginated transactions", e);
        }
    }
    
    /**
     * Get user transactions with pagination
     * @param userId User ID
     * @param page Page number (1-based)
     * @param pageSize Number of transactions per page
     * @return Paginated list of user transactions
     */
    public List<Transactions> viewUserAllPaginated(int userId, int page, int pageSize) {
        logger.info("Retrieving paginated user transactions: userId={}, page={}, pageSize={}", 
                   userId, page, pageSize);
        
        if (userId <= 0 || page <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("User ID, page, and page size must be positive integers");
        }
        
        try {
            int offset = (page - 1) * pageSize;
            List<Transactions> transactions = transactionsDAO.viewUserAllWithPagination(userId, offset, pageSize);
            
            return transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error retrieving paginated user transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve paginated user transactions", e);
        }
    }
    
    /**
     * Search transactions by transaction name
     * @param searchTerm Search term to match against transaction name
     * @return List of transactions matching the search term
     */
    public List<Transactions> searchTransactionsByName(String searchTerm) {
        logger.info("Searching transactions by name: {}", searchTerm);
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        
        try {
            List<Transactions> transactions = transactionsDAO.searchTransactionsByName(searchTerm.trim());
            
            return transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error searching transactions by name: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search transactions", e);
        }
    }
    
    /**
     * Get transactions within a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of transactions within the date range
     */
    public List<Transactions> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Retrieving transactions between {} and {}", startDate, endDate);
        
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        try {
            List<Transactions> transactions = transactionsDAO.getTransactionsByDateRange(startDate, endDate);
            
            return transactions.stream()
                .filter(this::isValidTransaction)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error retrieving transactions by date range: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve transactions by date range", e);
        }
    }
    
    /**
     * Get recent transactions (last 30 days)
     * @return List of recent transactions
     */
    public List<Transactions> getRecentTransactions() {
        logger.info("Retrieving recent transactions (last 30 days)");
        
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        
        return getTransactionsByDateRange(startDate, endDate);
    }
    
    /**
     * Get recent transactions for a specific user (last 30 days)
     * @param userId User ID
     * @return List of recent user transactions
     */
    public List<Transactions> getRecentUserTransactions(int userId) {
        logger.info("Retrieving recent transactions for user {} (last 30 days)", userId);
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        
        try {
            List<Transactions> allUserTransactions = viewUserAll(userId);
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            
            return allUserTransactions.stream()
                .filter(t -> t.getTransactionDate().isAfter(thirtyDaysAgo))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            logger.error("Error retrieving recent user transactions: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve recent user transactions", e);
        }
    }
    
    /**
     * Get total count of all transactions
     * @return Total number of transactions
     */
    public long getTotalTransactionCount() {
        logger.info("Getting total transaction count");
        
        try {
            return transactionsDAO.getTotalTransactionCount();
        } catch (Exception e) {
            logger.error("Error getting total transaction count: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get total transaction count", e);
        }
    }
    
    /**
     * Get total count of transactions for a specific user
     * @param userId User ID
     * @return Total number of user transactions
     */
    public long getUserTransactionCount(int userId) {
        logger.info("Getting transaction count for user: {}", userId);
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive integer");
        }
        
        try {
            return transactionsDAO.getUserTransactionCount(userId);
        } catch (Exception e) {
            logger.error("Error getting user transaction count: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user transaction count", e);
        }
    }
    
    // Private helper methods
    
    /**
     * Validate transaction business rules
     * @param transaction Transaction to validate
     * @return true if transaction is valid
     */
    private boolean isValidTransaction(Transactions transaction) {
        if (transaction == null) {
            logger.debug("Transaction is null");
            return false;
        }
        
        // Check if transaction has valid ID
        if (transaction.getTransactionId() <= 0) {
            logger.debug("Transaction has invalid ID: {}", transaction.getTransactionId());
            return false;
        }
        
        // Check if transaction has valid user ID
        if (transaction.getUserId() <= 0) {
            logger.debug("Transaction has invalid user ID: {}", transaction.getUserId());
            return false;
        }
        
        // Check if transaction has valid amount (positive)
        if (transaction.getTransactionAmount() == null || 
            transaction.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            logger.debug("Transaction has invalid amount: {}", transaction.getTransactionAmount());
            return false;
        }
        
        // Check if transaction has valid date
        if (transaction.getTransactionDate() == null) {
            logger.debug("Transaction has null date");
            return false;
        }
        
        // Check if transaction name is not empty
        if (transaction.getTransactionName() == null || 
            transaction.getTransactionName().trim().isEmpty()) {
            logger.debug("Transaction has empty or null name");
            return false;
        }
        
        return true;
    }
    
    /**
     * Generate statistics for a list of transactions
     * @param transactions List of transactions
     * @param label Label for the statistics
     * @return TransactionStatistics object
     */
    private TransactionStatistics generateStatistics(List<Transactions> transactions, String label) {
        logger.debug("Generating statistics for: {}", label);
        
        if (transactions == null || transactions.isEmpty()) {
            return new TransactionStatistics(label, 0, BigDecimal.ZERO, BigDecimal.ZERO, 
                                           BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, 0);
        }
        
        int totalCount = transactions.size();
        
        BigDecimal totalAmount = transactions.stream()
            .map(Transactions::getTransactionAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageAmount = totalAmount.divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal maxAmount = transactions.stream()
            .map(Transactions::getTransactionAmount)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        
        BigDecimal minAmount = transactions.stream()
            .map(Transactions::getTransactionAmount)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
        
        // Count transaction types
        long transferCount = transactions.stream().mapToLong(t -> t.isTransfer() ? 1 : 0).sum();
        long cashInCount = transactions.stream().mapToLong(t -> t.isCashIn() ? 1 : 0).sum();
        long cashOutCount = transactions.stream().mapToLong(t -> t.isCashOut() ? 1 : 0).sum();
        long otherCount = totalCount - transferCount - cashInCount - cashOutCount;
        
        return new TransactionStatistics(
            label,
            totalCount,
            totalAmount,
            averageAmount,
            maxAmount,
            minAmount,
            (int) transferCount,
            (int) cashInCount,
            (int) cashOutCount,
            (int) otherCount
        );
    }
    
    /**
     * Inner class for transaction statistics
     */
    public static class TransactionStatistics {
        private final String label;
        private final int totalCount;
        private final BigDecimal totalAmount;
        private final BigDecimal averageAmount;
        private final BigDecimal maxAmount;
        private final BigDecimal minAmount;
        private final int transferCount;
        private final int cashInCount;
        private final int cashOutCount;
        private final int otherCount;
        
        public TransactionStatistics(String label, int totalCount, BigDecimal totalAmount, 
                                   BigDecimal averageAmount, BigDecimal maxAmount, BigDecimal minAmount,
                                   int transferCount, int cashInCount, int cashOutCount, int otherCount) {
            this.label = label;
            this.totalCount = totalCount;
            this.totalAmount = totalAmount;
            this.averageAmount = averageAmount;
            this.maxAmount = maxAmount;
            this.minAmount = minAmount;
            this.transferCount = transferCount;
            this.cashInCount = cashInCount;
            this.cashOutCount = cashOutCount;
            this.otherCount = otherCount;
        }
        
        // Getters
        public String getLabel() { return label; }
        public int getTotalCount() { return totalCount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public BigDecimal getAverageAmount() { return averageAmount; }
        public BigDecimal getMaxAmount() { return maxAmount; }
        public BigDecimal getMinAmount() { return minAmount; }
        public int getTransferCount() { return transferCount; }
        public int getCashInCount() { return cashInCount; }
        public int getCashOutCount() { return cashOutCount; }
        public int getOtherCount() { return otherCount; }
        
        public String getFormattedTotalAmount() {
            return "₱" + String.format("%,.2f", totalAmount);
        }
        
        public String getFormattedAverageAmount() {
            return "₱" + String.format("%,.2f", averageAmount);
        }
        
        public String getFormattedMaxAmount() {
            return "₱" + String.format("%,.2f", maxAmount);
        }
        
        public String getFormattedMinAmount() {
            return "₱" + String.format("%,.2f", minAmount);
        }
        
        @Override
        public String toString() {
            return String.format(
                "%s: Total: %d transactions, Amount: %s, Avg: %s, Max: %s, Min: %s, " +
                "Types - Transfers: %d, Cash-In: %d, Cash-Out: %d, Other: %d",
                label, totalCount, getFormattedTotalAmount(), getFormattedAverageAmount(),
                getFormattedMaxAmount(), getFormattedMinAmount(),
                transferCount, cashInCount, cashOutCount, otherCount
            );
        }
    }
}
     