package com.tesdaciicc.data.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.model.UserAuthentication;

public class UserDAO {

  private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

  // SQL Queries - Updated with new column names
  private static final String INSERT_USER = "INSERT INTO users (name, email, number, pin) VALUES (?, ?, ?, ?)";

  private static final String SELECT_USER_BY_ID = "SELECT id, name, email, number, pin, createdDate, updatedDate FROM users WHERE id = ?";

  private static final String SELECT_USER_BY_EMAIL = "SELECT id, name, email, number, pin, createdDate, updatedDate FROM users WHERE email = ?";

  private static final String SELECT_USER_BY_NUMBER = "SELECT id, name, email, number, pin, createdDate, updatedDate FROM users WHERE number = ?";

  private static final String SELECT_USER_BY_EMAIL_OR_NUMBER = "SELECT id, name, email, number, pin, createdDate, updatedDate FROM users WHERE email = ? OR number = ?";

  private static final String UPDATE_USER = "UPDATE users SET name = ?, email = ?, number = ?, updatedDate = datetime('now') WHERE id = ?";

  private static final String UPDATE_PIN = "UPDATE users SET pin = ?, updatedDate = datetime('now') WHERE id = ?";

  private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

  private static final String SELECT_ALL_USERS = "SELECT id, name, email, number, pin, createdDate, updatedDate FROM users ORDER BY createdDate DESC";

  private static final String COUNT_USERS = "SELECT COUNT(*) FROM users";

  /**
   * Saves a new user to the database (Registration)
   * 
   * @param user User object to save
   * @return Optional containing the saved user with generated ID, or empty if
   *         failed
   */
  public Optional<UserAuthentication> save(UserAuthentication user) {
    logger.debug("Saving user: {}", user.getEmail());

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, user.getName());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getNumber());
      statement.setString(4, user.getPin());

      int affectedRows = statement.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            user.setId(generatedKeys.getInt(1));

            // Retrieve the auto-generated dates
            Optional<UserAuthentication> savedUser = findById((int) user.getId());
            if (savedUser.isPresent()) {
              user.setCreatedDate(savedUser.get().getCreatedDate());
              user.setUpdatedDate(savedUser.get().getUpdatedDate());
            }

            logger.info("User saved successfully with ID: {}", user.getId());
            return Optional.of(user);
          }
        }
      }

    } catch (SQLException e) {
      logger.error("Error saving user: {}", user.getEmail(), e);
    }

    return Optional.empty();
  }

  /**
   * Finds a user by ID
   * 
   * @param id User ID
   * @return Optional containing the user, or empty if not found
   */
  public Optional<UserAuthentication> findById(int id) {
    logger.debug("Finding user by ID: {}", id);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID)) {

      statement.setLong(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          UserAuthentication user = mapResultSetToUser(resultSet);
          logger.debug("User found: {}", user.getEmail());
          return Optional.of(user);
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding user by ID: {}", id, e);
    }

    logger.debug("User not found with ID: {}", id);
    return Optional.empty();
  }

  /**
   * Finds a user by email
   * 
   * @param email User email
   * @return Optional containing the user, or empty if not found
   */
  public Optional<UserAuthentication> findByEmail(String email) {
    logger.debug("Finding user by email: {}", email);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_EMAIL)) {

      statement.setString(1, email);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          UserAuthentication user = mapResultSetToUser(resultSet);
          logger.debug("User found: {}", user.getEmail());
          return Optional.of(user);
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding user by email: {}", email, e);
    }

    logger.debug("User not found with email: {}", email);
    return Optional.empty();
  }

  /**
   * Finds a user by phone number
   * 
   * @param number User phone number
   * @return Optional containing the user, or empty if not found
   */
  public Optional<UserAuthentication> findByNumber(String number) {
    logger.debug("Finding user by number: {}", number);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_NUMBER)) {

      statement.setString(1, number);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          UserAuthentication user = mapResultSetToUser(resultSet);
          logger.debug("User found: {}", user.getEmail());
          return Optional.of(user);
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding user by number: {}", number, e);
    }

    logger.debug("User not found with number: {}", number);
    return Optional.empty();
  }

  /**
   * Finds a user by email or phone number (for login)
   * 
   * @param emailOrNumber Email or phone number
   * @return Optional containing the user, or empty if not found
   */
  public Optional<UserAuthentication> findByEmailOrNumber(String emailOrNumber) {
    logger.debug("Finding user by email or number: {}", emailOrNumber);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_EMAIL_OR_NUMBER)) {

      statement.setString(1, emailOrNumber);
      statement.setString(2, emailOrNumber);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          UserAuthentication user = mapResultSetToUser(resultSet);
          logger.debug("User found: {}", user.getEmail());
          return Optional.of(user);
        }
      }

    } catch (SQLException e) {
      logger.error("Error finding user by email or number: {}", emailOrNumber, e);
    }

    logger.debug("User not found with email or number: {}", emailOrNumber);
    return Optional.empty();
  }

  /**
   * Updates user information
   * 
   * @param user User with updated information
   * @return true if update was successful
   */
  public boolean update(UserAuthentication user) {
    logger.debug("Updating user: {}", user.getId());

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {

      statement.setString(1, user.getName());
      statement.setString(2, user.getEmail());
      statement.setString(3, user.getNumber());
      statement.setLong(4, user.getId());

      int affectedRows = statement.executeUpdate();

      if (affectedRows > 0) {
        logger.info("User updated successfully: {}", user.getId());
        return true;
      }

    } catch (SQLException e) {
      logger.error("Error updating user: {}", user.getId(), e);
    }

    return false;
  }

  /**
   * Updates user PIN
   * 
   * @param userId User ID
   * @param newPin New PIN
   * @return true if update was successful
   */
  public boolean updatePin(Long userId, String newPin) {
    logger.debug("Updating PIN for user: {}", userId);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_PIN)) {

      statement.setString(1, newPin);
      statement.setLong(2, userId);

      int affectedRows = statement.executeUpdate();

      if (affectedRows > 0) {
        logger.info("PIN updated successfully for user: {}", userId);
        return true;
      }

    } catch (SQLException e) {
      logger.error("Error updating PIN for user: {}", userId, e);
    }

    return false;
  }

  /**
   * Deletes a user
   * 
   * @param id User ID
   * @return true if deletion was successful
   */
  public boolean delete(Long id) {
    logger.debug("Deleting user: {}", id);

    try (Connection connection = ConnectionFactory.getConnection();
        PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {

      statement.setLong(1, id);

      int affectedRows = statement.executeUpdate();

      if (affectedRows > 0) {
        logger.info("User deleted successfully: {}", id);
        return true;
      }

    } catch (SQLException e) {
      logger.error("Error deleting user: {}", id, e);
    }

    return false;
  }

  /**
   * Retrieves all users
   * 
   * @return List of all users
   */
  public List<UserAuthentication> findAll() {
    logger.debug("Finding all users");
    List<UserAuthentication> users = new ArrayList<>();

    try (Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS)) {

      while (resultSet.next()) {
        users.add(mapResultSetToUser(resultSet));
      }

      logger.debug("Found {} users", users.size());

    } catch (SQLException e) {
      logger.error("Error finding all users", e);
    }

    return users;
  }

  /**
   * Counts total number of users
   * 
   * @return User count
   */
  public long count() {
    try (Connection connection = ConnectionFactory.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(COUNT_USERS)) {

      if (resultSet.next()) {
        return resultSet.getLong(1);
      }

    } catch (SQLException e) {
      logger.error("Error counting users", e);
    }

    return 0;
  }

  /**
   * Maps a ResultSet row to User object
   * 
   * @param resultSet ResultSet containing user data
   * @return User object
   * @throws SQLException if mapping fails
   */
  private UserAuthentication mapResultSetToUser(ResultSet resultSet) throws SQLException {
    UserAuthentication user = new UserAuthentication();

    user.setId(resultSet.getInt("id"));
    user.setName(resultSet.getString("name"));
    user.setEmail(resultSet.getString("email"));
    user.setNumber(resultSet.getString("number"));
    user.setPin(resultSet.getString("pin"));
    user.setCreatedDate(resultSet.getString("createdDate"));
    user.setUpdatedDate(resultSet.getString("updatedDate"));

    return user;
  }

}
