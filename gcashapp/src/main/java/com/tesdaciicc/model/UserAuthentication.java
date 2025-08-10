package com.tesdaciicc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserAuthentication {

  private int id;
  private String name;
  private String email;
  private String number;
  private String pin;
  private LocalDateTime createdDate;
  private LocalDateTime lastLogin;

  public UserAuthentication() {
    // Default: createdDate is now
    this.createdDate = LocalDateTime.now();
  }

  // Constructor without ID (for registration)
  public UserAuthentication(String name, String email, String number, String pin) {
    this.name = name;
    this.email = email;
    this.number = number;
    this.pin = pin;
    this.createdDate = LocalDateTime.now(); // Auto-set
    this.lastLogin = null; // Will be updated upon login
  }

  // Full constructor (for reading from DB)
  public UserAuthentication(int id, String name, String email, String number, String pin,
      LocalDateTime createdDate, LocalDateTime lastLogin) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.number = number;
    this.pin = pin;
    this.createdDate = createdDate;
    this.lastLogin = lastLogin;
  }

  // Getters & Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getPin() {
    return pin;
  }

  public void setPin(String pin) {
    this.pin = pin;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(LocalDateTime createdDate) {
    this.createdDate = createdDate;
  }

  public LocalDateTime getLastLogin() {
    return lastLogin;
  }

  // public void setLastLogin(LocalDateTime lastLogin) {
  // this.lastLogin = lastLogin;
  // }

  public void setLastLogin(String lastLogin) {
    this.lastLogin = LocalDateTime.parse(lastLogin, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  // Helper: Format createdDate
  public String getFormattedCreatedDate() {
    return createdDate != null ? createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
  }

  // Helper: Format lastLogin
  public String getFormattedLastLogin() {
    return lastLogin != null ? lastLogin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
  }

  @Override
  public String toString() {
    return "UserAuthentication [id=" + id + ", name=" + name + ", email=" + email +
        ", number=" + number + ", pin=" + pin +
        ", createdDate=" + getFormattedCreatedDate() +
        ", lastLogin=" + getFormattedLastLogin() + "]";
  }

}
