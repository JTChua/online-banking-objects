package com.tesdaciicc.data.repository;

import com.tesdaciicc.model.Balance;
import com.tesdaciicc.data.util.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BalanceDAO {

  private static final Logger logger = LoggerFactory.getLogger(BalanceDAO.class);

  // SQL queries matching your existing Balance table schema
  private static final String SELECT_BALANCE_BY_USER_ID = "SELECT balanceId, userId, balanceAmount, createdDate, updatedDate FROM balance WHERE userId = ?";

  private static final String SELECT_ALL_BALANCES = "SELECT balanceId, userId, balanceAmount, createdDate, updatedDate FROM balance";

  private static final String INSERT_BALANCE = "INSERT INTO balance (balanceAmount, userId) VALUES (?, ?)";

  private static final String UPDATE_BALANCE = "UPDATE balance SET balanceAmount = ?, updatedDate = datetime('now') WHERE userId = ?";

  private static final String DELETE_BALANCE = "DELETE FROM balance WHERE userId = ?";

  /**
   * Find balance by user ID
   * 
   * @param userId The user ID to search for
   * @return Optional containing Balance if found, empty otherwise
   */
  public Optional<Balance> findByUserId(int userId) {
    logger.debug("Finding balance for userId: {}", userId);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(SELECT_BALANCE_BY_USER_ID)) {

      stmt.setInt(1, userId);

      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          Balance balance = mapResultSetToBalance(rs);
          logger.debug("Balance found for userId {}: {}", userId, balance.getAmount());
          return Optional.of(balance);
        }
      }
    } catch (SQLException e) {
      logger.error("Error finding balance for userId {}: {}", userId, e.getMessage(), e);
    }

    logger.debug("No balance found for userId: {}", userId);
    return Optional.empty();
  }

  /**
   * Get all balances
   * 
   * @return List of all balances
   */
  public List<Balance> findAll() {
    logger.debug("Fetching all balances");
    List<Balance> balances = new ArrayList<>();

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_BALANCES);
        ResultSet rs = stmt.executeQuery()) {

      while (rs.next()) {
        balances.add(mapResultSetToBalance(rs));
      }

      logger.debug("Found {} balances", balances.size());
    } catch (SQLException e) {
      logger.error("Error fetching all balances: {}", e.getMessage(), e);
    }

    return balances;
  }

  /**
   * Create a new balance record
   * 
   * @param balance The balance to create
   * @return true if successful, false otherwise
   */
  public boolean create(Balance balance) {
    logger.debug("Creating balance for userId: {}", balance.getUserId());

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(INSERT_BALANCE, Statement.RETURN_GENERATED_KEYS)) {

      stmt.setBigDecimal(1, balance.getAmount());
      stmt.setInt(2, balance.getUserId());

      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            balance.setId(generatedKeys.getInt(1));
          }
        }
        logger.info("Balance created successfully for userId: {}", balance.getUserId());
        return true;
      }
    } catch (SQLException e) {
      logger.error("Error creating balance for userId {}: {}", balance.getUserId(), e.getMessage(), e);
    }

    return false;
  }

  /**
   * Update balance amount for a user
   * 
   * @param userId    The user ID
   * @param newAmount The new balance amount
   * @return true if successful, false otherwise
   */
  public boolean updateBalance(int userId, BigDecimal newAmount) {
    logger.debug("Updating balance for userId {} to amount: {}", userId, newAmount);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(UPDATE_BALANCE)) {

      stmt.setBigDecimal(1, newAmount);
      stmt.setInt(2, userId);

      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        logger.info("Balance updated successfully for userId: {}", userId);
        return true;
      }
    } catch (SQLException e) {
      logger.error("Error updating balance for userId {}: {}", userId, e.getMessage(), e);
    }

    return false;
  }

  /**
   * Update balance amount for a user (double overload)
   * 
   * @param userId    The user ID
   * @param newAmount The new balance amount
   * @return true if successful, false otherwise
   */
  public boolean updateBalance(int userId, double newAmount) {
    return updateBalance(userId, BigDecimal.valueOf(newAmount));
  }

  /**
   * Delete balance record for a user
   * 
   * @param userId The user ID
   * @return true if successful, false otherwise
   */
  public boolean delete(int userId) {
    logger.debug("Deleting balance for userId: {}", userId);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(DELETE_BALANCE)) {

      stmt.setInt(1, userId);

      int rowsAffected = stmt.executeUpdate();

      if (rowsAffected > 0) {
        logger.info("Balance deleted successfully for userId: {}", userId);
        return true;
      }
    } catch (SQLException e) {
      logger.error("Error deleting balance for userId {}: {}", userId, e.getMessage(), e);
    }

    return false;
  }

  /**
   * Helper method to map ResultSet to Balance object
   */
  private Balance mapResultSetToBalance(ResultSet rs) throws SQLException {
    return new Balance(
        rs.getInt("balanceId"), // Your existing 'id' column
        rs.getInt("userId"), // Your existing 'userId' column
        rs.getBigDecimal("balanceAmount"), // Your existing 'amount' column
        rs.getString("createdDate"), // Your existing 'createdDate' column
        rs.getString("updatedDate") // Your existing 'updatedDate' column
    );
  }
}