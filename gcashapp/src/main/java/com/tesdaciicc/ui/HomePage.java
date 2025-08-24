package com.tesdaciicc.ui;

import com.tesdaciicc.model.UserAuthentication;
import java.util.Scanner;

public class HomePage {

    private static Scanner scanner = new Scanner(System.in);

    private HomePage() {
        // Private constructor to prevent instantiation
    }

    public static void showHomePage(UserAuthentication user) {
        System.out.println("\n>>>>>     Home Page     <<<<<");
        System.out.println("Welcome, " + user.getName());
       // Display user account information and options
       homePageMainMenu(user);
    }

    private static void homePageMainMenu(UserAuthentication user) {
        boolean homePageMainMenu = true;

        while (homePageMainMenu) {
            System.out.println("\n>>>     JCash Transaction Menu     <<<");
            System.out.println("1.   Check Balance");
            System.out.println("2.   Cash In");
            System.out.println("3.   Cash Transfer");
            System.out.println("4.   View All Transaction");
            System.out.println("5.   Logout");
            System.out.print("Choose option:   ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> {
                        //checkBalance(user);
                        // Placeholder for CheckBalance functionality
                        // CheckBalance.checkBalance(user);    
                        // for successful balance check, should also have an option to return to home page menu    
                        returnToHomePageMenu();
                    }
                    case 2 -> {
                        //cashIn(user);
                        // Placeholder for CashIn functionality
                        // CashIn.cashIn(user);
                        // for successful cash in, should also have an option to return to home page menu
                        returnToHomePageMenu();
                    }
                    case 3 -> {
                        //cashTransfer(user);
                        // Placeholder for CashTransfer functionality
                        // CashTransfer.cashTransfer(user);
                        // for successful cash transfer, should also have an option to return to home page menu
                        returnToHomePageMenu();
                    }
                    case 4 -> {
                        //viewAllTransactions(user);
                        // Placeholder for ViewAllTransactions functionality
                        // ViewAllTransactions.viewAllTransactions(user);
                        // for successful transaction viewing, should also have an option to return to home page menu
                        returnToHomePageMenu();
                    }
                    case 5 -> {
                        homePageMainMenu = logout();
                        // This will break the loop and return to main menu
                    }
                    default -> System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Helper method to show return to menu option
    private static void returnToHomePageMenu() {
        System.out.println("\nPress Enter to return to home page main menu...");
        scanner.nextLine();
    }

    // Logout method implementation (case 5)
    private static boolean logout() {
        System.out.println("\n>>>   Logout   <<<");
        System.out.println("Are you sure you want to logout? (y/n): ");
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("y") || confirmation.equals("yes")) {
            System.out.println("\nThank you for using JCash Transactions!");
            System.out.println("Logged out successfully.");
            System.out.println("Returning to Login Page...\n");
            return false; // This will break the while loop and return to main menu
        } else {
            System.out.println("Logout cancelled. Returning to home page menu.");
            return true; // Continue showing the home page menu
        }
    }

}
