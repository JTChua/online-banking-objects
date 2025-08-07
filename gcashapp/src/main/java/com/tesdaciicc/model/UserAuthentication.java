package com.tesdaciicc.model;

public class UserAuthentication {

  private int id;
  private String name;
  private String email;
  private String number;
  private int pin;

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

  @Override
  public String toString() {
    return "UserAuthentication [id=" + id + ", name=" + name + ", email=" + email + ", number=" + number + ", pin="
        + pin + "]";
  }

}
