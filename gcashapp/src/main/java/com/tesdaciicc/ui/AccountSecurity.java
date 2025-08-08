package com.tesdaciicc.ui;

import model.UserAuthentication;
import service.UserAuthenticationService;

public class AccountSecurity {

  private static final UserAuthenticationService userAuthService = new UserAuthenticationService();

  public void changePin() {
    Scanner scanner = new Scanner(System.in);

    System.out.print("Mobile Number: ");
    String number = scanner.nextLine();
    System.out.print("Old PIN: ");
    String oldPin = scanner.nextLine();
    System.out.print("New PIN: ");
    String newPin = scanner.nextLine();

    if (userAuthService.changePin(number, oldPin, newPin)) {
      System.out.println("PIN updated successfully.");
    } else {
      System.out.println("PIN update failed.");
    }
  }

}
