package com.tesdaciicc.data.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for database operations and initialization
 */
public class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Call this once on app startup
     * 
     * @return true if initialization is successful
     */
    public static boolean initializeDatabase() {
        try {
            logger.info("Starting database initialization");
            ensureDatabaseFolderExists();

            // Execute in strict order
            if (!executeTableCreation()) return false;
            if (!executeIndexCreation()) return false;
            if (!executeDataInsertion()) return false;

            logger.info("Database initialized successfully");
            return true;
        } catch (Exception e) {
            logger.error("Database initialization failed", e);
            return false;
        }
    }

    private static boolean executeTableCreation() {
        logger.debug("Creating tables from {}", Config.INIT_SQL_FILE);
        return runSqlFromResource(Config.INIT_SQL_FILE);
    }

    private static boolean executeIndexCreation() {
        logger.debug("Creating indexes from {}", Config.INDEX_SQL_FILE);
        
        // Verify tables exist first
        if (!tableExists("users") || !tableExists("balance")) {
            logger.error("Cannot create indexes - tables not found");
            return false;
        }
        
        return runSqlFromResource(Config.INDEX_SQL_FILE);
    }

    private static boolean executeDataInsertion() {
        logger.debug("Inserting initial data");
        
        // Verify tables exist first
        if (!tableExists("users")) {
            logger.error("Cannot insert data - users table not found");
            return false;
        }
        
        if (!tableExists("balance")) {
            logger.error("Cannot insert data - balance table not found");
            return false;
        }
        
        // Check if data already exists
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) > 0) {
                logger.info("Users table already contains data, skipping insertion");
                return true;
            }
            rs.close();
            
        } catch (SQLException e) {
            logger.error("Error checking existing data", e);
            return false;
        }
        
        // Try SQL file first, fall back to programmatic if it fails
        boolean sqlFileResult = runSqlFromResource(Config.DATA_SQL_FILE);
        
        if (sqlFileResult) {
            // Verify data was actually inserted
            try (Connection conn = ConnectionFactory.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next() && rs.getInt(1) > 0) {
                    logger.info("SQL file insertion successful - {} users inserted", rs.getInt(1));
                    rs.close();
                    return true;
                }
                rs.close();
            } catch (SQLException e) {
                logger.error("Error verifying SQL file insertion", e);
            }
        }
        
        // If SQL file didn't work, fall back to programmatic insertion
        logger.warn("SQL file insertion failed or produced no results, trying programmatic approach");
        return insertTestDataProgrammatically();
    }

    /**
     * Insert test data programmatically - reliable fallback method
     */
    private static boolean insertTestDataProgrammatically() {
        logger.info("Inserting test data programmatically...");
        
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insert users first
            String userSql = "INSERT INTO users (id, name, email, number, pin) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                
                Object[][] users = {
                    {1, "John Doe", "john.doe@email.com", "09123456789", "1234"},
                    {2, "Jane Smith", "jane.smith@email.com", "09987654321", "5678"},
                    {3, "Bob Johnson", "bob.johnson@email.com", "09111222333", "9876"},
                    {4, "Alice Brown", "alice.brown@email.com", "09444555666", "4321"},
                    {5, "Charlie Wilson", "charlie.wilson@email.com", "09777888999", "1111"},
                    {6, "Diana Lee", "diana.lee@email.com", "09222333444", "2222"},
                    {7, "Edward Davis", "edward.davis@email.com", "09555666777", "3333"},
                    {8, "Fiona Martinez", "fiona.martinez@email.com", "09888999000", "4444"},
                    {9, "George Taylor", "george.taylor@email.com", "09333444555", "5555"},
                    {10, "Helen Clark", "helen.clark@email.com", "09666777888", "6666"}
                };
                
                int userCount = 0;
                for (Object[] user : users) {
                    userStmt.setInt(1, (Integer) user[0]);
                    userStmt.setString(2, (String) user[1]);
                    userStmt.setString(3, (String) user[2]);
                    userStmt.setString(4, (String) user[3]);
                    userStmt.setString(5, (String) user[4]);
                    int result = userStmt.executeUpdate();
                    if (result > 0) userCount++;
                }
                
                logger.info("Inserted {} users", userCount);
            }
            
            // Insert balance data
            String balanceSql = "INSERT INTO balance (user_ID, amount) VALUES (?, ?)";
            try (PreparedStatement balanceStmt = conn.prepareStatement(balanceSql)) {
                
                Object[][] balances = {
                    {1, 15000.50}, {2, 8750.25}, {3, 25000.00}, {4, 500.75}, {5, 12345.60},
                    {6, 0.00}, {7, 99999.99}, {8, 3250.40}, {9, 7800.80}, {10, 18500.30}
                };
                
                int balanceCount = 0;
                for (Object[] balance : balances) {
                    balanceStmt.setInt(1, (Integer) balance[0]);
                    balanceStmt.setDouble(2, (Double) balance[1]);
                    int result = balanceStmt.executeUpdate();
                    if (result > 0) balanceCount++;
                }
                
                logger.info("Inserted {} balance records", balanceCount);
            }
            
            conn.commit();
            logger.info("Test data insertion completed successfully");
            return true;
            
        } catch (SQLException e) {
            logger.error("Failed to insert test data", e);
            return false;
        }
    }

    public static void verifyInitialization() throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            // Verify tables
            String[] requiredTables = {"users", "balance"};
            for (String table : requiredTables) {
                if (!tableExists(table)) {
                    throw new SQLException("Table missing: " + table);
                }
            }
            
            // Verify indexes
            String[] requiredIndexes = {
                "idx_users_email",
                "idx_users_number", 
                "idx_balance_user_id"
            };
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='index'")) {
                
                List<String> existingIndexes = new ArrayList<>();
                while (rs.next()) {
                    existingIndexes.add(rs.getString("name"));
                }
                
                for (String index : requiredIndexes) {
                    if (!existingIndexes.contains(index)) {
                        throw new SQLException("Index missing: " + index);
                    }
                }
            }
        }
    }

    /**
     * If db.url points to ./database/gcashapp.db, make sure ./database exists
     */
    private static void ensureDatabaseFolderExists() {
        String url = Config.DATABASE_URL;
        if (url == null) {
            logger.warn("Database URL is null");
            return;
        }

        if (!url.startsWith("jdbc:sqlite:")) {
            logger.debug("Not an SQLite URL, skipping folder creation");
            return;
        }

        String pathPart = url.substring("jdbc:sqlite:".length());
        Path dbPath = Paths.get(pathPart).normalize();

        Path parent = dbPath.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
                logger.debug("Created database directory: {}", parent);
            } catch (Exception e) {
                logger.error("Failed to create database directory: {}", parent, e);
                throw new RuntimeException("Failed to create database directory: " + parent, e);
            }
        }
    }

    /**
     * Executes a SQL file from resources (classpath)
     * 
     * @param resourcePath Path to SQL resource file
     * @return true if execution was successful
     */
    public static boolean runSqlFromResource(String resourcePath) {
        logger.debug("Running SQL from resource: {}", resourcePath);

        try (InputStream in = DatabaseUtil.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                logger.error("SQL resource not found: {}", resourcePath);
                return false;
            }

            String sql = new BufferedReader(new InputStreamReader(in))
                .lines()
                .collect(Collectors.joining("\n"));

            return executeMultipleStatements(sql);

        } catch (Exception e) {
            logger.error("Failed to run SQL resource: {}", resourcePath, e);
            return false;
        }
    }

    /**
     * Executes multiple SQL statements separated by semicolons
     * 
     * @param sql Multiple SQL statements
     * @return true if all statements executed successfully
     */
    private static boolean executeMultipleStatements(String sql) {
        // Clean the SQL content first
        String cleanedSql = cleanSqlContent(sql);
        logger.debug("Executing cleaned SQL content (length: {})", cleanedSql.length());
        
        // Split by semicolon more carefully
        String[] statements = cleanedSql.split(";\\s*(?=\\n|$)");
        
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement statement = connection.createStatement()) {
                int executedCount = 0;
                
                for (int i = 0; i < statements.length; i++) {
                    String stmt = statements[i].trim();
                    
                    if (stmt.isEmpty()) {
                        continue;
                    }
                    
                    if (stmt.startsWith("--") || stmt.startsWith("#")) {
                        continue;
                    }
                    
                    try {
                        logger.debug("Executing statement {}: {}", i + 1, stmt.substring(0, Math.min(100, stmt.length())));
                        boolean hasResults = statement.execute(stmt);
                        
                        if (!hasResults) {
                            int updateCount = statement.getUpdateCount();
                            logger.debug("Rows affected: {}", updateCount);
                        }
                        
                        executedCount++;
                    } catch (SQLException e) {
                        logger.error("Failed to execute: {}", stmt.substring(0, Math.min(200, stmt.length())), e);
                        connection.rollback();
                        return false;
                    }
                }
                
                connection.commit();
                logger.info("Successfully executed {} SQL statements", executedCount);
                return executedCount > 0;
                
            }
        } catch (SQLException e) {
            logger.error("Database operation failed", e);
            return false;
        }
    }

    /**
     * Clean SQL content by removing comments and empty lines
     */
    private static String cleanSqlContent(String sql) {
        if (sql == null) return "";
        
        StringBuilder cleaned = new StringBuilder();
        String[] lines = sql.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Skip empty lines and comment lines
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                continue;
            }
            
            cleaned.append(line).append("\n");
        }
        
        return cleaned.toString().trim();
    }

    /**
     * Checks if the database exists and is accessible
     * 
     * @return true if database is ready
     */
    public static boolean isDatabaseReady() {
        try (Connection connection = ConnectionFactory.getConnection()) {
            connection.createStatement().executeQuery("SELECT COUNT(*) FROM users").close();
            logger.debug("Database is ready");
            return true;
        } catch (SQLException e) {
            logger.warn("Database not ready: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a table exists in the database
     * 
     * @param tableName Table name to check
     * @return true if table exists
     */
    public static boolean tableExists(String tableName) {
        String sql = "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?";
        
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, tableName.toLowerCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.error("Error checking table existence", e);
            return false;
        }
    }

    /**
     * Verify tables before creating indexes
     */
    public static void verifyTablesExist() throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            String[] requiredTables = {"users", "balance"};
            
            for (String table : requiredTables) {
                if (!tableExists(table)) {
                    throw new SQLException("Critical table missing: " + table);
                }
            }
        }
    }

    /**
     * Drops all tables (for testing purposes)
     * 
     * @return true if successful
     */
    public static boolean dropAllTables() {
        logger.warn("Dropping all database tables");

        try (Connection connection = ConnectionFactory.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("DROP TABLE IF EXISTS balance");
            statement.execute("DROP TABLE IF EXISTS users");
            logger.info("All tables dropped successfully");
            return true;

        } catch (SQLException e) {
            logger.error("Failed to drop tables", e);
            return false;
        }
    }
}