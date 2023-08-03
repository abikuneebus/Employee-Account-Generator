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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
// import javafx.scene.control.PasswordField; // * future
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EmailApp extends Application {
  private static final String JSON_PATH = "emailAccounts.json";
  private static List<EmailAccount> accounts = new ArrayList<>();
  private TextField firstNameInput;
  private TextField lastNameInput;
  // private PasswordField passwordInput; // * future
  // private PasswordField newPasswordInput; // * future
  private ComboBox<String> departmentComboBox;
  private Text accountInfo;
  private Email currentEmail;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Email App");

    // * START PAGE
    GridPane grid = new GridPane();

    Text firstNameText = new Text("First Name:");
    firstNameInput = new TextField();
    grid.add(firstNameText, 0, 0);
    grid.add(firstNameInput, 1, 0);

    Text lastNameText = new Text("Last Name:");
    lastNameInput = new TextField();
    grid.add(lastNameText, 0, 1);
    grid.add(lastNameInput, 1, 1);

    Text departmentText = new Text("Department:");
    departmentComboBox = new ComboBox<>();
    departmentComboBox.getItems().addAll(
        "Accounting",
        "Admin",
        "IT",
        "Development",
        "Sales",
        "N/A");
    departmentComboBox.getSelectionModel().select("N/A");
    grid.add(departmentText, 0, 2);
    grid.add(departmentText, 1, 2);

    Button createButton = new Button("Generate Account");
    createButton.setOnAction(e -> createNewAccount());
    grid.add(createButton, 0, 3);

    // * future
    // Button createButton = new Button(text: "Search Account");
    // createButton.setOnAction(e -> searchAccount());
    // grid.add(createButton, 1, 3);

    accountInfo = new Text();
    grid.add(accountInfo, 0, 4);
    GridPane.setColumnSpan(accountInfo, 2);

    // * future
    // Text passwordText = new Text("Password:");
    // PasswordField passwordInput = new PasswordField();
    // grid.add(passwordText, 0, 4);
    // grid.add(passwordInput, 1, 4);

    // Button changePasswordButton = new Button("Change Password");
    // changePasswordButton.setOnAction(e -> changePassword());
    // grid.add(changePasswordButton, 1, 5);

    Scene scene = new Scene(grid, 400, 300);
    primaryStage.setScene(scene);
    primaryStage.show();

    accounts = getAccountsFromJson();
  }

  private void createNewAccount() {
    String firstName = firstNameInput.getText();
    String lastName = lastNameInput.getText();
    String department = departmentComboBox.getValue();

    if (firstName.isEmpty() || lastName.isEmpty()) {
      showAlert("Both first name and last name must be entered!");
      return;
    }
    currentEmail = new Email(firstName, lastName, department);
    addAccountToJson(currentEmail);
    showAlert("Account creation successful!");
    updateAccountInfo();
  }
  // * future
  // private void changePassword() {
  // if (currentEmail == null) {
  // showAlert("No email account exists; please create an account first.");
  // return;
  // }
  //
  // String newPassword = passwordInput.getText();
  // String passwordValid =
  // currentEmail.isPasswordValid(newPassword.toCharArray());
  // if (passwordValid != null) {
  // showAlert(passwordValid); // shows reason for invalidity
  // return;
  // }
  //
  // currentEmail.changePassword((newPassword.toCharArray()));
  // showAlert("Password changed successfully!");
  // updateAccountInfo();
  // }

  private void updateAccountInfo() {
    if (currentEmail == null) {
      accountInfo.setText("");
    } else {
      accountInfo.setText("Name: " + currentEmail.getName() +
          "\nUsername: " + currentEmail.getUsername() +
          "\nDepartment: " + currentEmail.getDepartment() +
          "\nEmail: " + currentEmail.getEmail() +
          "\nMailbox Capacity: " + currentEmail.getMailCapacity());
    }
  }

  private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  private static void addAccountToJson(Email email) {
    accounts.add(new EmailAccount(email.getName(), email.getEmail(), email.getMailCapacity(), email.getDepartment(),
        email.getHashedPassword()));
    writeAccountsToJson(accounts);
  }

  public static List<EmailAccount> getAccountsFromJson() {
    Gson gson = new Gson();
    try (FileReader reader = new FileReader(JSON_PATH)) {
      Type listType = new TypeToken<ArrayList<EmailAccount>>() {
      }.getType();
      List<EmailAccount> accounts = gson.fromJson(reader, listType);
      return accounts != null ? accounts : new ArrayList<>();
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  private static void writeAccountsToJson(List<EmailAccount> accounts) {
    Gson gson = new Gson();
    try (FileWriter writer = new FileWriter(JSON_PATH)) {
      gson.toJson(accounts, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}