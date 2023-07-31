package com.abikuneebus;

import java.util.ArrayList;
import java.util.List;

public class Constants {
  // random password generation characters
  public static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^*";

  // alternate email allowed characters
  public static final String EMAIL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  // forbidden password substrings
  public static final List<String> FORBIDDEN_SUBSTRINGS = new ArrayList<>();
  static {
    FORBIDDEN_SUBSTRINGS.add("password");
    FORBIDDEN_SUBSTRINGS.add("Password");
    FORBIDDEN_SUBSTRINGS.add("PASSWORD");
    FORBIDDEN_SUBSTRINGS.add("qwerty");
    FORBIDDEN_SUBSTRINGS.add("1q2w3e");
    FORBIDDEN_SUBSTRINGS.add("0000");
    FORBIDDEN_SUBSTRINGS.add("1111");
    FORBIDDEN_SUBSTRINGS.add("2222");
    FORBIDDEN_SUBSTRINGS.add("3333");
    FORBIDDEN_SUBSTRINGS.add("4444");
    FORBIDDEN_SUBSTRINGS.add("5555");
    FORBIDDEN_SUBSTRINGS.add("6666");
    FORBIDDEN_SUBSTRINGS.add("7777");
    FORBIDDEN_SUBSTRINGS.add("8888");
    FORBIDDEN_SUBSTRINGS.add("9999");
    FORBIDDEN_SUBSTRINGS.add("1234567890");
    FORBIDDEN_SUBSTRINGS.add("0987654321");
    FORBIDDEN_SUBSTRINGS.add("0987654321");
    FORBIDDEN_SUBSTRINGS.add("0123456789");

  }

  public static void initializeForbiddenSubstrings(String firstName, String lastName, String department,
      String companyName) {
    FORBIDDEN_SUBSTRINGS.add(String.format("%s", firstName));
    FORBIDDEN_SUBSTRINGS.add(String.format("%s", lastName));
    FORBIDDEN_SUBSTRINGS.add(String.format("%s", department));
    FORBIDDEN_SUBSTRINGS.add(String.format("%s", companyName));
  }
}