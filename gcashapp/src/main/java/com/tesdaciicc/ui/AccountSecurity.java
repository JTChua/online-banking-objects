package com.tesdaciicc.ui;

import com.tesdaciicc.model.UserAuthentication;
import com.tesdaciicc.service.UserAuthenticationService;
import java.util.Scanner;

public class AccountSecurity {

  private static UserAuthenticationService userAuthService;

  public static void pinChange() {

    try (Scanner scanner = new Scanner(System.in)) {
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

}
