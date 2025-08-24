package com.tesdaciicc;

import java.util.Scanner;

import com.tesdaciicc.ui.LoginPage;
import com.tesdaciicc.ui.RegistrationPage;


public class App {

    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("    Welcome to GCash App!       ");
        System.out.println("=================================");

        App.showMainMenu();

    }

    private static void showMainMenu() {

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
                case 2 -> RegistrationPage.registration();
                case 3 -> {
                    System.out.println("Thank you for using GCash!");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }
}