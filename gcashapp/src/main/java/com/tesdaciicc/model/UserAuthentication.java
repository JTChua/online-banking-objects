package com.tesdaciicc.model;

public class UserAuthentication {

  private int id;
  private String name;
  private String email;
  private String number;
  private String pin;

  public UserAuthentication() {
  }

  public UserAuthentication(int id, String name, String email, String number, int pin) {
    super();
    this.id = id;
    this.name = name;
    this.email = email;
    this.number = number;
    this.pin = pin;
  }

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

  public int getPin() {
    return pin;
  }

  public void setPin(int pin) {
    this.pin = pin;
  }

  @Override
  public String toString() {
    return "UserAuthentication [id=" + id + ", name=" + name + ", email=" + email + ", number=" + number + ", pin="
        + pin + "]";
  }

}
