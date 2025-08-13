// package com.tesdaciicc.service;

// import com.tesdaciicc.data.repository.UserAuthenticationDAO;
// import com.tesdaciicc.model.UserAuthentication;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// public class UserAuthenticationService {

// private final UserAuthenticationDAO dao;

// public UserAuthenticationService(UserAuthenticationDAO dao) {
// this.dao = dao;
// }

// public boolean registerUser(UserAuthentication userAuthentication) {
// if (!validateUser(userAuthentication)) {
// System.out.println("Validation failed.");
// return false;
// }

// try {
// // Generate custom incremental userId
// int nextId = dao.getNextUserId(); // DAO will find last userId and increment
// userAuthentication.setId(nextId);

// // Set createdDate in yyyy-MM-dd HH:mm:ss format
// userAuthentication.setCreatedDate(LocalDateTime.now());

// // Initially, lastLogin will be null until first successful login
// userAuthentication.setLastLogin(null);

// dao.register(userAuthentication);
// return true;
// } catch (Exception e) {
// System.out.println("Registration error: " + e.getMessage());
// return false;
// }
// }

// public UserAuthentication loginUser(String number, String pin) {
// try {
// UserAuthentication user = dao.login(number, pin);

// if (user != null) {
// // Update lastLogin timestamp only on successful login
// String lastLogin =
// LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd
// HH:mm:ss"));
// dao.updateLastLogin(user.getId(), lastLogin);
// user.setLastLogin(lastLogin);
// }

// return user;
// } catch (Exception e) {
// System.out.println("Login failed: " + e.getMessage());
// return null;
// }
// }

// public boolean changePin(String number, String oldPin, String newPin) {
// try {
// return dao.updatePin(number, oldPin, newPin);
// } catch (Exception e) {
// System.out.println("PIN change failed: " + e.getMessage());
// return false;
// }
// }

// private boolean validateUser(UserAuthentication userAuthentication) {
// return userAuthentication.getName() != null &&
// !userAuthentication.getName().isEmpty()
// && userAuthentication.getEmail() != null &&
// userAuthentication.getEmail().contains("@")
// && userAuthentication.getNumber() != null &&
// userAuthentication.getNumber().matches("\\d{11}")
// && userAuthentication.getPin() != null &&
// userAuthentication.getPin().matches("\\d{4}");
// }

// }
