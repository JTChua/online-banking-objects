package com.tesdaciicc.ui;

import com.tesdaciicc.ui.Registration.RegistrationResult;

import java.util.Scanner;

public class RegistrationPage {

    private static Scanner scanner = new Scanner(System.in);

    private RegistrationPage() {
        // Private constructor to prevent instantiation
    }


    public static void registration() {

        while (true) {
            System.out.println("\n>>>>> REGISTRATION PAGE <<<<<");
            System.out.println("Create your account by filling out the registration form:");
            
            // Registration form
            System.out.print("Full Name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("✗ Name cannot be empty!");
                continue;
            }
            
            System.out.print("Email Address: ");
            String email = scanner.nextLine().trim();
            
            if (email.isEmpty()) {
                System.out.println("✗ Email cannot be empty!");
                continue;
            }
            
            System.out.print("Phone Number (11 digits): ");
            String number = scanner.nextLine().trim();
            
            if (number.isEmpty()) {
                System.out.println("✗ Phone number cannot be empty!");
                continue;
            }
            
            System.out.print("4-Digit PIN: ");
            String pin = scanner.nextLine().trim();
            
            if (pin.isEmpty()) {
                System.out.println("✗ PIN cannot be empty!");
                continue;
            }
            
            System.out.print("Confirm PIN: ");
            String confirmPin = scanner.nextLine().trim();
            
            if (!pin.equals(confirmPin)) {
                System.out.println("✗ PINs do not match!");
                continue;
            }

            // Initialize Registration class
            Registration registration = new Registration();

            // Save - Attempt Registration
            System.out.println("\nRegistering user...");
            RegistrationResult result = registration.registerUser(name, email, number, pin);
            
            if (result.isSuccess()) {
                // If success - Display success message
                System.out.println("\n✓ Registration successful!");
                System.out.println("Welcome, " + result.getUser().getName() + "!");
                System.out.println("Your account has been created successfully.");
                System.out.println("You can now login with your credentials.");
                
                // Exit - Return to Login page
                System.out.print("\nPress Enter to return to Login page...");
                scanner.nextLine();
                return; // Return to Login page
                
            } else {
                // If failed - Display error message
                System.out.println("\n✗ Registration failed!");
                System.out.println("Error: " + result.getMessage());

                // Exit - Return to Login page
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
