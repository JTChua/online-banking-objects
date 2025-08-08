package com.tesdaciicc.ui;

import model.UserAuthentication;
import service.UserAuthenticationService;

public class Login {

  private static final UserAuthenticationService userAuthService = new UserAuthenticationService();

  public void login() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Mobile Number: ");
    String number = scanner.nextLine();
    System.out.print("4-digit PIN: ");
    String pin = scanner.nextLine();

    UserAuthentication userAuth = userAuthService.loginUser(number, pin);
    if (userAuth != null) {
      System.out.println("Login successful. Welcome, " + userAuth.getName());
    } else {
      System.out.println("Wrong Mobile Number or PIN.");
    }
  }

}
