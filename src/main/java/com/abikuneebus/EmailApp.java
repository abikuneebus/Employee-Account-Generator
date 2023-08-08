package com.abikuneebus;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailApp extends Application {
  private static final String JSON_PATH = "emailAccounts.json"; // ! -> SQL
  public static List<EmailAccount> accounts = new ArrayList<>();

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Email App");

    // test database connection
    DatabaseManager.testDatabaseConnection();

    // start EmailAppGUI
    EmailAppGUI emailAppGUI = new EmailAppGUI(this);
    Scene scene = new Scene(emailAppGUI.getGridPane(), 400, 300);
    primaryStage.setScene(scene);
    primaryStage.show();

    // load existing accounts from JSON file
    accounts = getAccountsFromJson(); // ! -> SQL
  }

  // add account to the list and update JSON
  public void addAccount(Email email) {
    accounts.add(new EmailAccount(email.getName(), email.getEmail(), email.getMailCapacity(),
        email.getDepartment(), email.getHashedPassword()));
    writeAccountsToJson(accounts); // ! -> SQL
  }

  // get existing accounts from JSON
  public static List<EmailAccount> getAccountsFromJson() { // ! -> SQL
    Gson gson = new Gson(); // ! -> SQL
    try (FileReader reader = new FileReader(JSON_PATH)) { // ! -> SQL
      Type listType = new TypeToken<ArrayList<EmailAccount>>() {
      }.getType();
      List<EmailAccount> accounts = gson.fromJson(reader, listType); // ! -> SQL
      return accounts != null ? accounts : new ArrayList<>();
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // write accounts to JSON
  private static void writeAccountsToJson(List<EmailAccount> accounts) { // ! -> SQL
    Gson gson = new Gson(); // ! -> SQL
    try (FileWriter writer = new FileWriter(JSON_PATH)) { // ! -> SQL
      gson.toJson(accounts, writer); // ! -> SQL
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}