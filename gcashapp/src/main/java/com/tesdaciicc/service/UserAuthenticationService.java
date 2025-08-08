package com.tesdaciicc.service;

import repository.UserAuthenticationDAO;
import model.UserAuthentication;

public class UserAuthenticationService {

  private UserAuthenticationDAO dao = new UserAuthenticationDAO();

  public boolean registerUser(UserAuthentication userAuthentication) {
    if (!validateUser(userAuthentication)) {
      System.out.println("Validation failed.");
      return false;
    }

    try {
      dao.register(userAuthentication);
      return true;
    } catch (Exception e) {
      System.out.println("Registration error: " + e.getMessage());
      return false;
    }
  }

  public UserAuthentication loginUser(String number, String pin) {
    try {
      return dao.login(number, pin);
    } catch (Exception e) {
      System.out.println("Login failed: " + e.getMessage());
      return null;
    }
  }

  public boolean changePin(String number, String oldPin, String newPin) {
    try {
      return dao.updatePin(number, oldPin, newPin);
    } catch (Exception e) {
      System.out.println("PIN change failed: " + e.getMessage());
      return false;
    }
  }

  private boolean validateUser(UserAuthentication userAuthentication) {
    return userAuthentication.getName() != null && !userAuthentication.getName().isEmpty()
        && userAuthentication.getEmail() != null && userAuthentication.getEmail().contains("@")
        && userAuthentication.getNumber() != null && userAuthentication.getNumber().matches("\\d{11}")
        && userAuthentication.getPin() != null && userAuthentication.getPin().matches("\\d{4}");
  }

}
