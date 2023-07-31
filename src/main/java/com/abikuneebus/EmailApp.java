package com.abikuneebus;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EmailApp {
  private static final String JSON_PATH = "emailAccounts.json";
  private static List<EmailAccount> accounts = new ArrayList<>();

  public static void main(String[] args) {
    Email email = null;
    Scanner scanner = new Scanner(System.in);
    String noEmailMessage = "No email account exists; please create an account first.";

    // load accounts from file when app starts
    accounts = getAccountsFromJson();

    while (true) {
      System.out.println("\n1. Create a new email");
      System.out.println("2. Change password");
      System.out.println("3. Set alternate email");
      System.out.println("4. Display information");
      System.out.println("5. Quit");
      System.out.println("Enter an option:");

      int option = scanner.nextInt();
      scanner.nextLine(); // consume newLine

      switch (option) {

        case 1:
          System.out.println("Enter first name: ");
          String firstName = scanner.nextLine();
          System.out.println("Enter last name: ");
          String lastName = scanner.nextLine();
          email = new Email(firstName, lastName, scanner);
          addAccountToJson(email);
          break;
        case 2:
          if (email == null) {
            System.out.println(noEmailMessage);
            break;
          }

          while (true) {
            System.out.println("Enter new password: ");
            String newPassword = scanner.nextLine();
            String passwordValid = email.isPasswordValid(newPassword.toCharArray());
            // if invalid
            if (passwordValid != null) {
              System.out.println(passwordValid); // prints reason for invalidity
              // if valid
            } else {
              email.changePassword(newPassword.toCharArray());
              break;
            }
          }
          break;
        case 3:
          if (email == null) {
            System.out.println(noEmailMessage);
            break;
          }
          System.out.println("Enter alternate email: ");
          String altEmail = scanner.nextLine();
          email.changeEmail(altEmail);
          break;
        case 4:
          if (email == null) {
            System.out.println(noEmailMessage);
            break;
          }
          System.out.println("Name: " + email.getName());
          System.out.println("Email: " + email.getEmail());
          System.out.println("Mailbox Capacity: " + email.getMailCapacity());
          break;
        case 5:
          System.out.println("Goodbye!");
          scanner.close();
          writeAccountsToJson(accounts);
          System.exit(0);
      }
    }
  }

  // add account to JSON file
  private static void addAccountToJson(Email email) {
    // getting list of existing accounts from JSON file
    List<EmailAccount> accounts = getAccountsFromJson();
    // creating an 'EmailAccount' object from the 'Email' object, add to list
    accounts.add(new EmailAccount(email.getName(), email.getEmail(), email.getMailCapacity(), email.getDepartment()));
    // write updated list back to JSON file
    writeAccountsToJson(accounts);
  }

  // read list of accounts from JSON file
  private static List<EmailAccount> getAccountsFromJson() {
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(JSON_PATH)) {
      // define type of deserialized list using TypeToken
      Type listType = new TypeToken<ArrayList<EmailAccount>>() {
      }.getType();
      // deserialize JSON data into list of EmailAccount objects
      List<EmailAccount> accounts = gson.fromJson(reader, listType);
      // if list is null, return empty list, otherwise return deserialized data
      return accounts != null ? accounts : new ArrayList<>();
    } catch (IOException e) {
      // if error: print stack trace & return empty list
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // write list of accounts to JSON file
  private static void writeAccountsToJson(List<EmailAccount> accounts) {
    Gson gson = new Gson();
    try (FileWriter writer = new FileWriter(JSON_PATH)) {
      // serialize list of EmailAccount objects to JSON, write to file
      gson.toJson(accounts, writer);
    } catch (IOException e) {
      // if error: print stack trace
      e.printStackTrace();
    }
  }
}