package com.abikuneebus;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

// * search for existing account & choice to delete/modify

public class ModifyAccountMenu extends GridPane { // - class declaration
  // - instance variables
  private EmailApp emailApp; // reference to main app class
  private PasswordChangeMenu passwordChangeMenu;

  // update/delete menu
  private TextField userInput;
  private TextField firstNameField;
  private TextField lastNameField;
  private Text departmentText;
  private TextField mailCapacityField;
  private Text emailText;
  private String accountDepartment;
  private String accountEmail;
  private String accountHashPass;

  // * MENUS

  public ModifyAccountMenu(EmailApp emailApp, PasswordChangeMenu passwordChangeMenu) { // - constructor
    // - instance variable assignments

    this.emailApp = emailApp;
    this.passwordChangeMenu = passwordChangeMenu;
    showSearchMenu();
  }

  // * search menu
  private void showSearchMenu() {
    getChildren().clear();
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // username input
    TextField userInput = new TextField();
    userInput.setPromptText("Enter Username");
    add(userInput, 0, 1);

    // find account button
    Button userSearchBtn = new Button("Find Account");
    userSearchBtn.setOnAction(e -> findAccount());
    add(userSearchBtn, 0, 1);

    // back to main menu button
    Button backToMainMenuBtn = new Button("Back to Main Menu");
    backToMainMenuBtn.setOnAction(e -> emailApp.showStartMenu());
    add(backToMainMenuBtn, 0, 2);

  }

  // * allows modification of account details or deletion of account
  void showUpdateDeleteMenu(EmailAccount account) {
    getChildren().clear();
    String accountDepartment = account.getDepartment();
    String accountEmail = account.getEmail();

    firstNameField = new TextField(account.getFirstName()); // modifiable
    add(firstNameField, 0, 0);

    lastNameField = new TextField(account.getLastName()); // modifiable
    add(lastNameField, 0, 1);

    departmentText = new Text(accountDepartment); // display only
    add(departmentText, 0, 2);

    emailText = new Text(accountEmail); // display only
    add(emailText, 1, 0);

    Button changePasswordBtn = new Button("Change Password"); // to change password menu
    changePasswordBtn.setOnAction(e -> {
      passwordChangeMenu.showChangePasswordMenu();
    });
    add(changePasswordBtn, 1, 1);

    mailCapacityField = new TextField(String.valueOf(account.getMailCapacity())); // modifiable
    add(mailCapacityField, 1, 2);

    Button updateAccountBtn = new Button("Update Account"); // updates all values in database
    updateAccountBtn.setOnAction(e -> updateAccount());
    add(updateAccountBtn, 2, 0);

    Button deleteAccountBtn = new Button("Delete Account"); // deletes account from database
    deleteAccountBtn.setOnAction(e -> deleteAccount(account));
    add(deleteAccountBtn, 2, 1);

    Button homeBtn = new Button("Back to Main Menu"); // returns to start menu
    homeBtn.setOnAction(e -> emailApp.showStartMenu());
    add(homeBtn, 2, 2);

  }

  // * UTILITY FUNCTIONS

  // * FIND ACCOUNT
  private void findAccount() {
    String username = userInput.getText();

    // validation
    String validationMsg = Email.isNameValid(username);
    if (username.isEmpty()) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Input Error");
      alert.setHeaderText("Missing Information");
      alert.setContentText("Username required!");
      alert.showAndWait();
      return;
    }

    if (validationMsg != null) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Input Error");
      alert.setHeaderText("Invalid Input");
      alert.setContentText(validationMsg);
      alert.showAndWait();
      return;
    }

    // populating form with account details if account exists
    DatabaseManager dbManager = new DatabaseManager();
    EmailAccount account = dbManager.getAccountByUsername(username);
    if (account != null) {
      passwordChangeMenu.setAccount(account);
      showUpdateDeleteMenu(account);
    } else {
      // throws alert if not
      Alert alert = new Alert(Alert.AlertType.ERROR, "Username not found!");
      alert.showAndWait();
    }
  }

  // * UPDATE ACCOUNT DETAILS
  private void updateAccount() {

    // confirm intent
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirmation");
    confirmAlert.setHeaderText("Update Account");
    confirmAlert.setContentText("Update all fields with form input values?");

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");

    confirmAlert.getButtonTypes().setAll(btnYes, btnNo);

    Optional<ButtonType> result = confirmAlert.showAndWait();
    if (result.get() == btnYes) {

      // get updated values from text fields
      String updatedFirstName = firstNameField.getText();
      String updatedLastName = lastNameField.getText();
      int updatedMailCapacity = Integer.parseInt(mailCapacityField.getText());

      // create EmailAccount object with updated values
      EmailAccount updatedAccount = new EmailAccount(updatedFirstName, updatedLastName, accountEmail,
          updatedMailCapacity,
          accountDepartment, accountHashPass);

      // update account in database
      DatabaseManager dbManager = new DatabaseManager();
      dbManager.updateAccount(updatedAccount);

      // return to home menu
      emailApp.showStartMenu();

    } else {
      // close dialog
      confirmAlert.getDialogPane().getScene().getWindow().hide();

    }

  }

  // * DELETE ACCOUNT
  private void deleteAccount(EmailAccount account) {
    // confirm intent
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirmation");
    confirmAlert.setHeaderText("Delete Account");
    confirmAlert.setContentText("Are you sure you want to delete this account?");

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");

    confirmAlert.getButtonTypes().setAll(btnYes, btnNo);

    Optional<ButtonType> result = confirmAlert.showAndWait();
    // if user confirms account deletion request ("Yes")
    if (result.get() == btnYes) {
      // deleting account
      DatabaseManager dbManager = new DatabaseManager();

      boolean successfulDelete = dbManager.deleteAccount(account.getEmail());

      if (successfulDelete) {

        // showing successful deletion confirmation
        Alert successAlert = new Alert(AlertType.INFORMATION, "Account deleted.");
        successAlert.showAndWait();

        // returning to home menu
        emailApp.showStartMenu();

      } else {
        // showing error message
        Alert deleteErrorAlert = new Alert(AlertType.ERROR, "Error deleting account, returning to home menu.");
        deleteErrorAlert.showAndWait();

        // returning to home menu
        emailApp.showStartMenu();
      }
    } else {
      // if user does not want to proceed with account deletion ("No")
      showUpdateDeleteMenu(account);
    }
  }

}