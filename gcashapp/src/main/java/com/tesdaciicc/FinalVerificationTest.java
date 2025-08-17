package com.tesdaciicc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.tesdaciicc.data.util.ConnectionFactory;
import com.tesdaciicc.data.util.DatabaseUtil;

public class FinalVerificationTest {

    public static void main(String[] args) {
        System.out.println("=== Final Verification Test ===");
        
        // Fresh start
        DatabaseUtil.dropAllTables();
        boolean initResult = DatabaseUtil.initializeDatabase();
        
        System.out.println("Database initialization result: " + initResult);
        
        if (initResult) {
            checkFinalResults();
        }
    }
    
    private static void checkFinalResults() {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Check users
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            int userCount = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            
            // Check balance
            rs = stmt.executeQuery("SELECT COUNT(*) FROM balance");
            int balanceCount = rs.next() ? rs.getInt(1) : 0;
            rs.close();
            
            System.out.println("\n📊 Final Results:");
            System.out.println("Users: " + userCount + " (expected: 10)");
            System.out.println("Balance records: " + balanceCount + " (expected: 10)");
            
            // Show sample data
            System.out.println("\n👥 Sample Users:");
            rs = stmt.executeQuery("SELECT id, name, email FROM users LIMIT 3");
            while (rs.next()) {
                System.out.println("  " + rs.getInt("id") + ": " + rs.getString("name") + " (" + rs.getString("email") + ")");
            }
            rs.close();
            
            System.out.println("\n💰 Sample Balances:");
            rs = stmt.executeQuery("SELECT user_ID, amount FROM balance LIMIT 3");
            while (rs.next()) {
                System.out.println("  User " + rs.getInt("user_ID") + ": ₱" + rs.getDouble("amount"));
            }
            rs.close();
            
            // Final verdict
            if (userCount == 10 && balanceCount == 10) {
                System.out.println("\n✅ SUCCESS: All test data inserted correctly from SQL files!");
            } else {
                System.out.println("\n❌ Issue: Expected 10 users and 10 balance records");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error checking results: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
