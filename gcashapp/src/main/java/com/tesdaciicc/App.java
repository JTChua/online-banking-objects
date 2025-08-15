package com.tesdaciicc;

import com.tesdaciicc.data.repository.UserAuthenticationDAO;
import com.tesdaciicc.service.UserAuthenticationService;
import com.tesdaciicc.model.UserAuthentication;

public class App {
    public static void main(String[] args) {
        System.out.println("=== GCash App - User Authentication Testing ===\n");

        // Initialize DAO and Service
        UserAuthenticationDAO dao = new UserAuthenticationDAO();
        UserAuthenticationService service = new UserAuthenticationService(dao);

        // Test data
        UserAuthentication testUser = new UserAuthentication();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setNumber("09123456789");
        testUser.setPin("1234");

        // Test 1: User Registration
        System.out.println("=== Test 1: User Registration ===");
        boolean registered = service.registerUser(testUser);
        System.out.println("Registration result: " + (registered ? "SUCCESS" : "FAILED"));

        if (registered) {
            System.out.println("User registered with ID: " + testUser.getId());
        }
        System.out.println();

        // Test 2: User Login with Email
        System.out.println("=== Test 2: User Login (Email) ===");
        UserAuthentication loggedInUser = service.loginUser("john.doe@example.com", "1234");

        if (loggedInUser != null) {
            System.out.println("Login SUCCESS!");
            System.out.println("User ID: " + loggedInUser.getId());
            System.out.println("Name: " + loggedInUser.getName());
            System.out.println("Token: " + loggedInUser.getToken());
            System.out.println("Created: " + loggedInUser.getCreatedDate());
        } else {
            System.out.println("Login FAILED!");
        }
        System.out.println();

        System.out.println("\n=== Testing Complete ===");

        // Test 3: User Login with Phone Number
        System.out.println("=== Test 3: User Login (Phone) ===");
        UserAuthentication loggedInUser2 = service.loginUser("09123456789", "1234");

        if (loggedInUser2 != null) {
            System.out.println("Phone Login SUCCESS!");
            System.out.println("Token: " + loggedInUser2.getToken());
        } else {
            System.out.println("Phone Login FAILED!");
        }
        System.out.println();

        // Test 4: Token Validation
        System.out.println("=== Test 4: Token Validation ===");
        if (loggedInUser != null) {
            String token = loggedInUser.getToken();
            UserAuthentication validatedUser = service.validateToken(token);

            if (validatedUser != null) {
                System.out.println("Token validation SUCCESS!");
                System.out.println("Validated user: " + validatedUser.getName());
            } else {
                System.out.println("Token validation FAILED!");
            }
        }
        System.out.println();
    }
}