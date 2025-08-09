package com.tesdaciicc;

import com.tesdaciicc.ui.AccountSecurity;
import com.tesdaciicc.ui.Login;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.UserAuthenticationService;
import com.tesdaciicc.ui.Registration;

import java.util.Scanner;

import com.tesdaciicc.data.util.DatabaseUtil;

public class App {

    public static void main(String[] args) {
        UserAuthenticationService userAuthService = new UserAuthenticationService();
        Registration registration = new Registration(userAuthService);

        DatabaseUtil.initializeDatabase();
        while (true) {
            System.out.println("\n--- GCash App ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Change PIN");
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            try (Scanner scanner = new Scanner(System.in)) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> registration.register();
                    case 2 -> Login.login();
                    case 3 -> AccountSecurity.pinChange();
                    case 4 -> System.out.println("You are  Logged out.");
                    case 5 -> System.exit(0);
                    default -> System.out.println("Invalid option.");
                }
            }
        }
    }
}
