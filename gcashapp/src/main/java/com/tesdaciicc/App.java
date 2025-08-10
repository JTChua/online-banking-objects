package com.tesdaciicc;

import com.tesdaciicc.ui.AccountSecurity;
import com.tesdaciicc.ui.Login;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.UserAuthenticationService;
import com.tesdaciicc.ui.Registration;

import com.tesdaciicc.ui.CheckBalance;
import com.tesdaciicc.service.CheckBalanceService;
import com.tesdaciicc.data.repository.UserAuthenticationDAO;

import java.util.Scanner;
import java.time.LocalDateTime;

import com.tesdaciicc.data.util.DatabaseUtil;

public class App {

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();

        // UserAuthenticationService userAuthService = new UserAuthenticationService();
        // Registration registration = new Registration(userAuthService);

        // Scanner scanner = new Scanner(System.in);

        // while (true) {
        // System.out.println("\n--- GCash App ---");
        // System.out.println("1. Register");
        // System.out.println("2. Login");
        // System.out.println("3. Change PIN");
        // System.out.println("4. Logout");
        // System.out.println("5. Exit");
        // System.out.print("Select option: ");

        // int choice = scanner.nextInt();
        // scanner.nextLine(); // clear buffer

        // switch (choice) {
        // case 1 -> registration.register();
        // case 2 -> Login.login();
        // case 3 -> AccountSecurity.pinChange();
        // case 4 -> System.out.println("You are logged out.");
        // case 5 -> {
        // System.out.println("Thank You!");
        // scanner.close();
        // System.exit(0);
        // }
        // default -> System.out.println("Invalid option.");
        // }
        // }

        // hardcoded from CheckBalance class
        // CheckBalance.getCheckBalance();

        // from CheckBalanceService
        // CheckBalanceService checkBalance = new CheckBalanceService();
        // System.out.println("User 1 Balance: ₱" + checkBalance.checkBalance(1));
        // System.out.println("User 2 Balance: ₱" + checkBalance.checkBalance(2));

        // 1️⃣ Initialize Database
        DatabaseUtil.initializeDatabase();

        // 2️⃣ Create DAO & Service
        UserAuthenticationDAO userDAO = new UserAuthenticationDAO();
        UserAuthenticationService userService = new UserAuthenticationService(userDAO);

        // // 3️⃣ Test Registration
        // System.out.println("=== Registering a new user ===");
        // UserAuthentication newUser = new UserAuthentication();
        // newUser.setName("John Doe");
        // newUser.setEmail("john@example.com");
        // newUser.setNumber("09171234567");
        // newUser.setPin("1234");

        // boolean isRegistered = userService.registerUser(newUser);
        // if (isRegistered) {
        // System.out.println("✅ User registered successfully.");
        // } else {
        // System.out.println("❌ Registration failed (email or number may already
        // exist).");
        // }

        // 4️⃣ Test Login
        System.out.println("\n=== Logging in ===");
        UserAuthentication loggedInUser = userService.loginUser("john@example.com", "1234");

        if (loggedInUser != null) {
            System.out.println("✅ Login successful!");
            System.out.println("Name: " + loggedInUser.getName());
            System.out.println("Email: " + loggedInUser.getEmail());
            System.out.println("Created At: " + loggedInUser.getCreatedDate());
            System.out.println("Last Login: " + loggedInUser.getLastLogin());
        } else {
            System.out.println("❌ Invalid credentials.");
        }

        // // 5️⃣ Test Duplicate Registration
        // System.out.println("\n=== Testing duplicate registration ===");
        // boolean duplicate = userService.registerUser(newUser);
        // if (!duplicate) {
        // System.out.println("✅ Duplicate detected — registration prevented.");
        // }

        // 6️⃣ Close DB Connection (optional if your DAO handles it)
        System.out.println("\n=== Test complete ===");

    }
}
