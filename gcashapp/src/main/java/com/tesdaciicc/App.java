package com.tesdaciicc;

import java.util.Scanner;

import com.tesdaciicc.ui.LoginPage;
import com.tesdaciicc.ui.RegistrationPage;
import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.ui.HomePage;


public class App {

    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("        Welcome to JCash App!        ");
        System.out.println("=====================================");

        App.showMainMenu();

    }

    private static void showMainMenu() {

        while (true) {
            System.out.println("\n>>>        JCash App        <<<");
            System.out.println("1.   Login");
            System.out.println("2.   Register");
            System.out.println("3.   Exit");
            System.out.print("Choose option:  ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    UserAuthentication authenticatedUser = LoginPage.login();
                        if (authenticatedUser != null) {
                            // User successfully logged in, show homepage
                            HomePage.showHomePage(authenticatedUser);
                            // After logout from homepage, control returns here and continues the main menu loop
                        }
                        // If login failed or user chose to go back, continue to main menu
                }
                case 2 -> {
                    RegistrationPage.registration();
                }
                case 3 -> {
                    System.out.println("Thank you for using JCash!");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }
}