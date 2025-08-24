package com.tesdaciicc.ui;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.ui.Login.LoginResult;

import java.util.Scanner;

public class LoginPage {

    private static Scanner scanner = new Scanner(System.in);

    private LoginPage() {
        // Private constructor to prevent instantiation
    }

    /**
     * Login method that returns UserAuthentication object if successful
     * 
     * @return UserAuthentication object if login successful, null if failed or user chose to go back
     */
    public static UserAuthentication login() {
        while (true) {
            System.out.println("\n>>>>>   Login   <<<<<");
            System.out.print("Enter email or mobile number (start with 0 and 11 digits): ");
            String emailOrNumber = scanner.nextLine().trim();

            if (emailOrNumber.isEmpty()) {
                System.out.println("✗ Email or mobile number cannot be empty!");
                continue;
            }

            if (!emailOrNumber.contains("@") && !emailOrNumber.matches("\\d+")) {
                System.out.println("✗ Invalid email or mobile number format!");
                continue;
            }
    
            System.out.print("Enter PIN (4 digits): ");
            String pin = scanner.nextLine().trim();
            
            if (pin.isEmpty()) {
                System.out.println("✗ PIN cannot be empty!");
                continue;
            }

            if (pin.length() != 4 || !pin.matches("\\d{4}")) {
                System.out.println("✗ PIN must be exactly 4 digits!");
                continue;
            }

            // Initialize Login class
            Login login = new Login();

            // Attempt login
            System.out.println("Authenticating...");

            LoginResult result = login.authenticate(emailOrNumber, pin);
            if (result.isSuccess()) {
                
                // If success - Return user object
                UserAuthentication user = result.getUser();
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, " + user.getName());
                return user; // Return the authenticated user
            } else {
                // If failed - Error displayed to user
                System.out.println("\n✗ Login failed!");
                System.out.println("Invalid Credentials, please try again.");
                System.out.println("Error: " + result.getMessage());

                // Exit - Return to Login page
                System.out.println("\nOptions:");
                System.out.println("1. Try again");
                System.out.println("2. Back to main menu");
                System.out.print("Choose an option: ");
                
                String retryChoice = scanner.nextLine().trim();
                if (retryChoice.equals("2")) {
                    return null; // Return null to indicate user chose to go back
                }
                // If "1" or any other input, continue the loop (try again)
            }
        }
    }
}