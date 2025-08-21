package com.tesdaciicc;

import com.tesdaciicc.data.util.DatabaseUtil;

public class DatabaseUtilTest {

    public static void main(String[] args) {
        System.out.println("=== DatabaseUtil Test Runner ===");

        // 1. Initialize database
        boolean init = DatabaseUtil.initializeDatabase();
        System.out.println("Initialize Database: " + init);

        // 2. Check tables
        System.out.println("Users table exists: " + DatabaseUtil.tableExists("users"));
        System.out.println("Balance table exists: " + DatabaseUtil.tableExists("balance"));
        System.out.println("Transaction table exists: " + DatabaseUtil.tableExists("transactions"));

        // 3. Database readiness
        System.out.println("Database ready: " + DatabaseUtil.isDatabaseReady());

        // 4. Verify initialization (tables + indexes)
        try {
            DatabaseUtil.verifyInitialization();
            System.out.println("verifyInitialization: PASSED");
        } catch (Exception e) {
            System.out.println("verifyInitialization: FAILED -> " + e.getMessage());
        }

        // 5. Run a fake SQL resource
        // boolean badSql = DatabaseUtil.runSqlFromResource("/nonexistent.sql");
        // System.out.println("Run SQL from nonexistent file: " + badSql);

        // 6. Drop tables
        // boolean dropped = DatabaseUtil.dropAllTables();
        // System.out.println("Drop all tables: " + dropped);

        // Check again after drop
        // System.out.println("Users table exists after drop: " + DatabaseUtil.tableExists("users"));
        // System.out.println("Balance table exists after drop: " + DatabaseUtil.tableExists("balance"));
        // System.out.println("Transactions table exists after drop: " + DatabaseUtil.tableExists("transactions"));

        System.out.println("=== Test Runner Finished ===");
    }

}
