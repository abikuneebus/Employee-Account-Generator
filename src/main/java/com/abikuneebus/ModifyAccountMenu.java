package com.abikuneebus;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

// * search for existing account & choice to delete/modify

public class ModifyAccountMenu extends GridPane { // - class declaration
  // - instance variables
  private EmailApp emailApp; // reference to main app class
  private PasswordChangeMenu passwordChangeMenu;

  // update/delete menu
  private TextField userInputField;
  private TextField firstNameField;
  private TextField lastNameField;
  private Text departmentText;
  private TextField mailCapacityField;
  private Text emailText;

  // * MENUS

  public ModifyAccountMenu(EmailApp emailApp, PasswordChangeMenu passwordChangeMenu) {

    this.emailApp = emailApp;
    this.passwordChangeMenu = passwordChangeMenu;

    setPrefSize(800, 300);
    setDefaultButtonSize(200);

    showSearchMenu();
  }

  private void setDefaultButtonSize(int width) {
    getChildren().stream().filter(node -> node instanceof Button).forEach(node -> ((Button) node).setMinWidth(width));
  }

  // * search menu
  private void showSearchMenu() {
    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // username input
    Text enterUsername = new Text("Enter account's username:");
    add(enterUsername, 0, 0);

    userInputField = new TextField();
    userInputField.setPromptText("Enter Username");
    add(userInputField, 1, 0);

    // find account button
    Button userSearchBtn = new Button("Find Account");
    userSearchBtn.setOnAction(e -> findAccount());
    add(userSearchBtn, 0, 1);

    userInputField.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        findAccount();
      }
    });

    // back to main menu button
    Button backToMainMenuBtn = new Button("Back to Main Menu");
    backToMainMenuBtn.setOnAction(e -> emailApp.showStartMenu());
    add(backToMainMenuBtn, 1, 1);

  }

  // * allows modification of account details or deletion of account

  void showUpdateDeleteMenu(EmailAccount existingAccount) {
    getChildren().clear();

    Text userWelcome = new Text("Welcome, " + existingAccount.getUsername() + "!");
    add(userWelcome, 1, 0, 2, 1);

    firstNameField = new TextField(existingAccount.getFirstName()); // modifiable
    add(firstNameField, 0, 1);

    lastNameField = new TextField(existingAccount.getLastName()); // modifiable
    add(lastNameField, 1, 1);

    departmentText = new Text(existingAccount.getDepartment()); // display only
    add(departmentText, 2, 1);

    emailText = new Text(existingAccount.getEmail()); // display only
    add(emailText, 3, 1);

    Button changePasswordBtn = new Button("Change Password"); // to change password menu
    changePasswordBtn.setOnAction(e -> {
      passwordChangeMenu.showChangePasswordMenu(); // TODO button not doing anything (no exceptions)
    });
    add(changePasswordBtn, 2, 1);

    mailCapacityField = new TextField(String.valueOf(existingAccount.getMailCapacity())); // modifiable
    add(mailCapacityField, 4, 1);
    // TODO add mailboxCapacity validation (range, character input)

    Button updateAccountBtn = new Button("Update Account"); // updates all values in database
    changePasswordBtn.setMinWidth(200);
    updateAccountBtn.setOnAction(e -> updateAccount(existingAccount));
    add(updateAccountBtn, 1, 2);

    Button deleteAccountBtn = new Button("Delete Account"); // deletes account from database
    deleteAccountBtn.setMinWidth(200);
    deleteAccountBtn.setOnAction(e -> deleteAccount(existingAccount));
    add(deleteAccountBtn, 3, 2);

    Button homeBtn = new Button("Back to Main Menu"); // returns to start menu
    homeBtn.setMinWidth(200);
    homeBtn.setOnAction(e -> emailApp.showStartMenu());
    add(homeBtn, 2, 2);

  }

  // * UTILITY FUNCTIONS

  // * FIND ACCOUNT
  private void findAccount() {
    String usernameInput = userInputField.getText();

    // validation
    String validationMsg = Email.isNameValid(usernameInput);
    if (usernameInput.isEmpty()) {
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
    dbManager.connect();
    EmailAccount account = dbManager.getAccountByUsername(usernameInput);
    if (account == null) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Username not found!");
      alert.showAndWait();
    }
    passwordChangeMenu.setAccount(account);
    passwordChangeMenu.showChangePasswordMenu();
    showUpdateDeleteMenu(account);
  }

  // * UPDATE ACCOUNT DETAILS
  private void updateAccount(EmailAccount existingAccount) {

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
      EmailAccount updatedAccount = new EmailAccount(updatedFirstName, updatedLastName, existingAccount.getEmail(),
          updatedMailCapacity, existingAccount.getDepartment(), existingAccount.getUsername(),
          existingAccount.getHashedPassword());

      // update account in database
      DatabaseManager dbManager = new DatabaseManager();
      dbManager.connect();
      dbManager.updateAccount(updatedAccount);
      dbManager.disconnect();

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
      dbManager.connect();
      boolean successfulDelete = dbManager.deleteAccount(account.getEmail());
      dbManager.disconnect();

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