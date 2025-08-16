// package com.tesdaciicc.ui;

// import com.tesdaciicc.model.UserAuthentication;
// import com.tesdaciicc.service.UserAuthenticationService;
// import java.util.Scanner;

// public class Login {

// private static UserAuthenticationService userAuthService;

// public static void login() {
// try (Scanner scanner = new Scanner(System.in)) {
// System.out.print("Mobile Number: ");
// String number = scanner.nextLine();
// System.out.print("4-digit PIN: ");
// String pin = scanner.nextLine();

// UserAuthentication userAuth = userAuthService.loginUser(number, pin);
// if (userAuth != null) {
// System.out.println("Login successful. Welcome, " + userAuth.getName());
// } else {
// System.out.println("Wrong Mobile Number or PIN.");
// }
// }

// }
// }

// for app.java
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

// System.out.println("\n=== Testing Complete ===");

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