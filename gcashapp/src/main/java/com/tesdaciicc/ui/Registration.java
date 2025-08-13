// package com.tesdaciicc.ui;

// import com.tesdaciicc.model.UserAuthentication;
// import com.tesdaciicc.service.UserAuthenticationService;
// import java.util.Scanner;

// public class Registration {

// private UserAuthenticationService userAuthService;

// public Registration(UserAuthenticationService userAuthService) {
// this.userAuthService = userAuthService;
// }

// public void register() {
// try (Scanner scanner = new Scanner(System.in)) {
// System.out.print("Name: ");
// String name = scanner.nextLine();
// System.out.print("Email: ");
// String email = scanner.nextLine();
// System.out.print("Mobile Number: ");
// String number = scanner.nextLine();
// System.out.print("4-digit PIN: ");
// String pin = scanner.nextLine();

// UserAuthentication userAuth = new UserAuthentication(name, email, number,
// pin);

// if (userAuthService.registerUser(userAuth)) {
// System.out.println("Registration successful!");
// } else {
// System.out.println("Registration failed.");
// }

// }
// }

// }
