package com.abikuneebus;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailApp extends Application { // * run app, manage inter-menu navigation
  private DatabaseManager dbManager;
  public static List<EmailAccount> accounts = new ArrayList<>();
  private Stage primaryStage; // reference to primary stage

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage; // save reference to primary stage
    showStartMenu(); // show the start menu
  }

  public void showStartMenu() {
    StartMenu startMenu = new StartMenu(this);
    Scene scene = new Scene(startMenu, 400, 300);
    primaryStage.setTitle("Email App");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showCreateAccountMenu() {
    // Logic to show the create account menu
    // This will be filled in later when you create the CreateAccountMenu class
  }

  public void showModifyAccountMenu() {
    // Logic to show the modify account menu
    // This will be filled in later when you create the ModifyAccountMenu class
  }

  // start EmailAppGUI
  EmailAppGUI emailAppGUI = new EmailAppGUI(this);

  // load existing accounts from JSON file
  // accounts = getAccountsFromJson(); // ! -> SQL

  public void addAccount(Email email) {
    EmailAccount account = new EmailAccount(email.getFirstName(), email.getLastName(), email.getEmail(),
        email.getMailCapacity(), email.getDepartment(), email.getHashedPassword());

    dbManager = new DatabaseManager();
    dbManager.connect();
    dbManager.insertEmailAccount(account);
    dbManager.disconnect();

    accounts.add(account);
  }
}

// ! writeAccountsToJson() wrote account info to JSON file

// ! getAccountsFromJson() returned list of all accounts in storage

// // test database connection
// DatabaseManager.testDatabaseConnection();
