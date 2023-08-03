package com.abikuneebus;

public class EmailAccount {
  private String name;
  private String email;
  private String altEmail;
  private int mailCapacity;
  private String department;
  private String username;
  private String hashedPassword;

  public EmailAccount(String name, String email, int mailCapacity, String department, String hashedPassword) {
    this.name = name;
    this.email = email;
    this.mailCapacity = mailCapacity;
    this.department = department;
    this.hashedPassword = hashedPassword;
  }

  // getters & setters

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email + ", " + altEmail;
  }

  public int getMailCapacity() {
    return mailCapacity;
  }

  public String getDepartment() {
    return department;
  }

  public String getPassword() {
    return hashedPassword;
  }
}