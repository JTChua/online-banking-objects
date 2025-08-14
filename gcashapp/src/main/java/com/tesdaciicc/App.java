package com.tesdaciicc;

import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.data.util.DatabaseUtil;
import com.tesdaciicc.service.UserAuthenticationService;
import com.tesdaciicc.model.UserAuthentication;

public class App {

    public static void main(String[] args) {

        System.out.println("=== GCash App - Startup ===");

        // 1️⃣ Initialize DB (create folders + tables)
        try {
            DatabaseUtil.initializeDatabase();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            return; // stop if DB not ready
        }

        // 2️⃣ Initialize DAO & Service
        UserAuthenticationDAO dao = new UserAuthenticationDAO();
        UserAuthenticationService service = new UserAuthenticationService(dao);

        // 3️⃣ Test user registration (for demo)
        UserAuthentication testUser = new UserAuthentication();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setNumber("09123456789");
        testUser.setPin("1234");

        System.out.println("\n=== Test 1: User Registration ===");
        boolean registered = service.registerUser(testUser);
        System.out.println("Registration result: " + (registered ? "SUCCESS" : "FAILED"));

        if (registered) {
            System.out.println("User registered with ID: " + testUser.getId());
        }

        // 4️⃣ Continue with CLI menu (Login -> Check Balance -> Transactions)
        // TODO: Implement your interactive flow here

        // System.out.println("=== GCash App - User Authentication Testing ===\n");

        // // Initialize DAO and Service
        // UserAuthenticationDAO dao = new UserAuthenticationDAO();
        // UserAuthenticationService service = new UserAuthenticationService(dao);

        // // Test data
        // UserAuthentication testUser = new UserAuthentication();
        // testUser.setName("John Doe");
        // testUser.setEmail("john.doe@example.com");
        // testUser.setNumber("09123456789");
        // testUser.setPin("1234");

        // // Test 1: User Registration
        // System.out.println("=== Test 1: User Registration ===");
        // boolean registered = service.registerUser(testUser);
        // System.out.println("Registration result: " + (registered ? "SUCCESS" :
        // "FAILED"));

        // if (registered) {
        // System.out.println("User registered with ID: " + testUser.getId());
        // }
        // System.out.println();

        // // Test 2: User Login with Email
        // System.out.println("=== Test 2: User Login (Email) ===");
        // UserAuthentication loggedInUser = service.loginUser("john.doe@example.com",
        // "1234");

        // if (loggedInUser != null) {
        // System.out.println("Login SUCCESS!");
        // System.out.println("User ID: " + loggedInUser.getId());
        // System.out.println("Name: " + loggedInUser.getName());
        // System.out.println("Token: " + loggedInUser.getToken());
        // System.out.println("Created: " + loggedInUser.getCreatedDate());
        // } else {
        // System.out.println("Login FAILED!");
        // }
        // System.out.println();

        // // Test 3: User Login with Phone Number
        // System.out.println("=== Test 3: User Login (Phone) ===");
        // UserAuthentication loggedInUser2 = service.loginUser("09123456789", "1234");

        // if (loggedInUser2 != null) {
        // System.out.println("Phone Login SUCCESS!");
        // System.out.println("Token: " + loggedInUser2.getToken());
        // } else {
        // System.out.println("Phone Login FAILED!");
        // }
        // System.out.println();

        // // Test 4: Token Validation
        // System.out.println("=== Test 4: Token Validation ===");
        // if (loggedInUser != null) {
        // String token = loggedInUser.getToken();
        // UserAuthentication validatedUser = service.validateToken(token);

        // if (validatedUser != null) {
        // System.out.println("Token validation SUCCESS!");
        // System.out.println("Validated user: " + validatedUser.getName());
        // } else {
        // System.out.println("Token validation FAILED!");
        // }
        // }
        // System.out.println();

        // // Test 5: Get User Profile
        // System.out.println("=== Test 5: Get User Profile ===");
        // if (loggedInUser != null) {
        // UserAuthentication profile = service.getUserProfile((long)
        // loggedInUser.getId());

        // if (profile != null) {
        // System.out.println("Profile retrieved successfully!");
        // System.out.println("Name: " + profile.getName());
        // System.out.println("Email: " + profile.getEmail());
        // System.out.println("Phone: " + profile.getNumber());
        // System.out.println("PIN: " + profile.getPin()); // Should be null for
        // security
        // System.out.println("Token: " + profile.getToken()); // Should be null for
        // security
        // } else {
        // System.out.println("Profile retrieval FAILED!");
        // }
        // }
        // System.out.println();

        // // Test 6: Change PIN
        // System.out.println("=== Test 6: Change PIN ===");
        // boolean pinChanged = service.changePin("john.doe@example.com", "1234",
        // "5678");
        // System.out.println("PIN change result: " + (pinChanged ? "SUCCESS" :
        // "FAILED"));
        // System.out.println();

        // // Test 7: Login with New PIN
        // System.out.println("=== Test 7: Login with New PIN ===");
        // UserAuthentication newPinLogin = service.loginUser("john.doe@example.com",
        // "5678");

        // if (newPinLogin != null) {
        // System.out.println("New PIN Login SUCCESS!");
        // System.out.println("New Token: " + newPinLogin.getToken());
        // } else {
        // System.out.println("New PIN Login FAILED!");
        // }
        // System.out.println();

        // // Test 8: Login with Old PIN (Should Fail)
        // System.out.println("=== Test 8: Login with Old PIN (Should Fail) ===");
        // UserAuthentication oldPinLogin = service.loginUser("john.doe@example.com",
        // "1234");

        // if (oldPinLogin != null) {
        // System.out.println("Old PIN Login SUCCESS (This shouldn't happen!)");
        // } else {
        // System.out.println("Old PIN Login FAILED (Expected!)");
        // }
        // System.out.println();

        // // Test 9: Logout
        // System.out.println("=== Test 9: Logout ===");
        // if (newPinLogin != null) {
        // boolean loggedOut = service.logout(newPinLogin.getToken());
        // System.out.println("Logout result: " + (loggedOut ? "SUCCESS" : "FAILED"));

        // // Test token after logout (should be invalid)
        // UserAuthentication invalidToken =
        // service.validateToken(newPinLogin.getToken());
        // System.out.println("Token validation after logout: " +
        // (invalidToken == null ? "INVALID (Expected)" : "STILL VALID (Problem!)"));
        // }
        // System.out.println();

        // // Test 10: Error Cases
        // System.out.println("=== Test 10: Error Cases ===");

        // // Test duplicate registration
        // UserAuthentication duplicateUser = new UserAuthentication();
        // duplicateUser.setName("Jane Doe");
        // duplicateUser.setEmail("john.doe@example.com"); // Same email
        // duplicateUser.setNumber("09987654321");
        // duplicateUser.setPin("9999");

        // boolean duplicateResult = service.registerUser(duplicateUser);
        // System.out.println(
        // "Duplicate email registration: " + (duplicateResult ? "ALLOWED (Problem!)" :
        // "BLOCKED (Expected)"));

        // // Test invalid login
        // UserAuthentication invalidLogin = service.loginUser("nonexistent@email.com",
        // "0000");
        // System.out
        // .println("Invalid user login: " + (invalidLogin == null ? "FAILED (Expected)"
        // : "SUCCESS (Problem!)"));

        // // Test invalid PIN format
        // boolean invalidPin = service.changePin("john.doe@example.com", "5678",
        // "12345"); // 5 digits
        // System.out.println("Invalid PIN format: " + (invalidPin ? "ALLOWED
        // (Problem!)" : "BLOCKED (Expected)"));

        // System.out.println("\n=== Testing Complete ===");
    }

}
