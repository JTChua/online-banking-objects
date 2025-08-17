package com.tesdaciicc;

import com.tesdaciicc.data.util.DatabaseUtil;
import com.tesdaciicc.data.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


public class EnhancedDatabaseDebugTest {
    
    public static void main(String[] args) {
        System.out.println("=== Enhanced Database Debug Test ===");
        
        // 1. Check SQL file content
        checkSqlFileContent();
        
        // 2. Test SQL parsing
        testSqlParsing();
        
        // 3. Test manual insertion
        testManualInsertion();
    }
    
    private static void checkSqlFileContent() {
        System.out.println("\n1. Checking 003_data.sql content:");
        
        try (InputStream in = EnhancedDatabaseDebugTest.class.getResourceAsStream("/sql/003_data.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            
            if (in == null) {
                System.out.println("❌ Could not find 003_data.sql");
                return;
            }
            
            String content = reader.lines().collect(Collectors.joining("\n"));
            System.out.println("📄 File content:");
            System.out.println("Length: " + content.length() + " characters");
            System.out.println("First 200 characters:");
            System.out.println(content.substring(0, Math.min(200, content.length())));
            System.out.println("...");
            
        } catch (Exception e) {
            System.out.println("❌ Error reading file: " + e.getMessage());
        }
    }
    
    private static void testSqlParsing() {
        System.out.println("\n2. Testing SQL parsing:");
        
        try (InputStream in = EnhancedDatabaseDebugTest.class.getResourceAsStream("/sql/003_data.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            
            String sql = reader.lines().collect(Collectors.joining("\n"));
            
            // Use the same regex as DatabaseUtil
            String[] statements = sql.split(";(?=(?:[^']*'[^']*')*[^']*$)");
            
            System.out.println("📊 Found " + statements.length + " statements after split:");
            
            for (int i = 0; i < statements.length; i++) {
                String trimmed = statements[i].trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                    System.out.println("Statement " + (i+1) + " (length=" + trimmed.length() + "):");
                    System.out.println(trimmed.substring(0, Math.min(100, trimmed.length())) + "...");
                    System.out.println("---");
                } else {
                    System.out.println("Statement " + (i+1) + ": SKIPPED (empty or comment)");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error parsing SQL: " + e.getMessage());
        }
    }
    
    private static void testManualInsertion() {
        System.out.println("\n3. Testing manual insertion:");
        
        // Drop and recreate tables
        DatabaseUtil.dropAllTables();
        DatabaseUtil.initializeDatabase();
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Test a simple manual insert
            System.out.println("🧪 Testing single user insert...");
            
            String testInsert = "INSERT INTO users (id, name, email, number, pin) VALUES (999, 'Test User', 'test@test.com', '09999999999', '0000')";
            
            int result = stmt.executeUpdate(testInsert);
            System.out.println("Insert result: " + result + " row(s) affected");
            
            // Check if it worked
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("Users count after manual insert: " + rs.getInt(1));
            }
            rs.close();
            
            // Test the exact INSERT from your SQL file
            System.out.println("🧪 Testing exact SQL from file...");
            
            String exactSql = """
                INSERT OR IGNORE INTO users (id, name, email, number, pin) VALUES
                    (1, 'John Doe', 'john.doe@email.com', '09123456789', '1234'),
                    (2, 'Jane Smith', 'jane.smith@email.com', '09987654321', '5678')
                """;
            
            int result2 = stmt.executeUpdate(exactSql);
            System.out.println("Bulk insert result: " + result2 + " row(s) affected");
            
            // Final count
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("Final users count: " + rs.getInt(1));
            }
            rs.close();
            
        } catch (Exception e) {
            System.out.println("❌ Error in manual insertion test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}