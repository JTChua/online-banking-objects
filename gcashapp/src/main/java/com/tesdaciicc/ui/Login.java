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
