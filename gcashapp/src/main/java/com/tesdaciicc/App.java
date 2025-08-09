package com.tesdaciicc;

import com.tesdaciicc.ui.AccountSecurity;
import com.tesdaciicc.ui.Login;

import model.UserAuthentication;
import service.UserAuthenticationService;
import ui.Registration;

public class App {

    public static void main(String[] args) {
        UserAuthenticationService userAuthService = new UserAuthenticationService();
        Registration registration = new Registration(userAuthService);
        Login login = new Login(userAuthService);
        AccountSecurity accntSecurity = new AccountSecurity(userAuthService);

        DatabaseUtil.initializeDatabase();
        while (true) {
            System.out.println("\n--- GCash App ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Change PIN");
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            System.out.print("Select option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registration.register();
                case 2 -> login.login();
                case 3 -> accntSecurity.changePin();
                case 4 -> System.out.println("You are  Logged out.");
                case 5 -> System.exit(0);
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
