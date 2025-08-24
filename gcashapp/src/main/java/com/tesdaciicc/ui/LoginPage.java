package com.tesdaciicc.ui;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.ui.Login;
import com.tesdaciicc.ui.Login.LoginResult;
import com.tesdaciicc.ui.Registration;

import java.util.Scanner;

public class LoginPage {

    private static Scanner scanner = new Scanner(System.in);

    private LoginPage() {
    }

    public static void showMainMenu() {
        while (true) {
            System.out.println("\n=== GCash App ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> LoginPage.login();
                case 2 -> Registration.register();
                case 3 -> {
                    System.out.println("Thank you for using GCash!");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private static void login() {
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
                
                // If success - User proceed to Home page
                UserAuthentication user = result.getUser();
                System.out.println("\n✓ Login successful!");
                System.out.println("Welcome, " + user.getName());
                //HomePage.showHomePage(user); proceed to users home page
                return; // After home page, return to login page
            } else {
                // If failed - Error displayed to user
                System.out.println("\n✗ Login failed!");
                System.out.println("Invalid Email or Number or PIN");
                System.out.println("Error: " + result.getMessage());

                // 1.1 Exit - Return to Login page
                System.out.println("\nOptions:");
                System.out.println("1. Try again");
                System.out.println("2. Back to Login page");
                System.out.print("Choose an option: ");
                
                String retryChoice = scanner.nextLine().trim();
                if (retryChoice.equals("2")) {
                    return; // Return to Login page
                }
                // If "1" or any other input, continue the loop (try again)
            }
        }
    }
}
