package com.tesdaciicc.model;

public class UserAuthentication {

  private int id;
  private String name;
  private String email;
  private String number;
  private String pin;

  // Constructor without ID (for registration)
  public UserAuthentication(String name, String email, String number, String pin) {
    setName(name);
    setEmail(email);
    setNumber(number);
    setPin(pin);
  }

  // Full constructor (for reading from DB)
  public UserAuthentication(int id, String name, String email, String number, String pin) {
    setId(id);
    setName(name);
    setEmail(email);
    setNumber(number);
    setPin(pin);
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

  @Override
  public String toString() {
    return "UserAuthentication [id=" + id + ", name=" + name + ", email=" + email +
        ", number=" + number + ", pin=" + pin + "]";
  }

}
