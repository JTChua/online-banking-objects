package com.tesdaciicc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.data.util.DatabaseUtil;

import java.io.InputStream;

public class DatabaseDebugTest {

    public static void main(String[] args) {
        System.out.println("=== Database Debug Test ===");
        
        // 1. Check if SQL files are accessible
        checkSqlFiles();
        
        // 2. Test database initialization
        testDatabaseInit();
        
        // 3. Check actual data
        checkInsertedData();
    }
    
    private static void checkSqlFiles() {
        System.out.println("\n1. Checking SQL files accessibility:");
        
        String[] files = {
            "/sql/001_init.sql",
            "/sql/002_indexes.sql", 
            "/sql/003_data.sql"
        };
        
        for (String file : files) {
            InputStream stream = DatabaseDebugTest.class.getResourceAsStream(file);
            if (stream != null) {
                System.out.println("✓ Found: " + file);
                try {
                    stream.close();
                } catch (Exception e) {
                    // ignore
                }
            } else {
                System.out.println("✗ Missing: " + file);
            }
        }
    }
    
    private static void testDatabaseInit() {
        System.out.println("\n2. Testing database initialization:");
        
        // Drop tables first for clean test
        DatabaseUtil.dropAllTables();
        
        boolean result = DatabaseUtil.initializeDatabase();
        System.out.println("Initialization result: " + result);
        
        // Check if tables exist
        System.out.println("Users table exists: " + DatabaseUtil.tableExists("users"));
        System.out.println("Balance table exists: " + DatabaseUtil.tableExists("balance"));
    }
    
    private static void checkInsertedData() {
        System.out.println("\n3. Checking inserted data:");
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check users count
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
            if (rs.next()) {
                System.out.println("Users count: " + rs.getInt("count"));
            }
            rs.close();
            
            // Check balance count  
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM balance");
            if (rs.next()) {
                System.out.println("Balance records count: " + rs.getInt("count"));
            }
            rs.close();
            
            // Show first few users
            System.out.println("\nFirst 3 users:");
            rs = stmt.executeQuery("SELECT id, name, email FROM users LIMIT 3");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 ", Name: " + rs.getString("name") + 
                                 ", Email: " + rs.getString("email"));
            }
            rs.close();
            
        } catch (Exception e) {
            System.out.println("Error checking data: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
