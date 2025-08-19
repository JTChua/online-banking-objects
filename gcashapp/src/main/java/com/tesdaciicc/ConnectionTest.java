package com.tesdaciicc;

import com.tesdaciicc.data.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionTest {

    public static void main(String[] args) {
        ConnectionFactory factory = ConnectionFactory.getInstance();
        
        System.out.println("Connection Info: " + factory.getConnectionInfo());
        
        if (ConnectionFactory.testConnection()) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
        }
        
        // Test getting a connection
        try (Connection conn = ConnectionFactory.getConnection()) {
            System.out.println("✅ Connection obtained successfully");
            System.out.println("Auto-commit: " + conn.getAutoCommit());
            System.out.println("Is valid: " + conn.isValid(2));
        } catch (SQLException e) {
            System.out.println("❌ Failed to get connection: " + e.getMessage());
        }
    }

}
