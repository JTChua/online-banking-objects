package com.tesdaciicc.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class UserAuthentication {

  private int id;
  private String name;
  private String email;
  private String number;
  private String pin;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Default constructor
  public UserAuthentication() {
  }

  // Constructor without ID (for registration)
  public UserAuthentication(String name, String email, String number, String pin) {
    setName(name);
    setEmail(email);
    setNumber(number);
    setPin(pin);
  }

  // Full constructor (for reading from DB)
  public UserAuthentication(int id, String name, String email, String number, String pin, LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    setId(id);
    setName(name);
    setEmail(email);
    setNumber(number);
    setPin(pin);
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "UserAuthentication{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", number='" + number + '\'' +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserAuthentication that = (UserAuthentication) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
