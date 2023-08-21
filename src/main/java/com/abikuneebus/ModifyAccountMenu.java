package com.abikuneebus;

import java.util.Optional;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

// * search for existing account & choice to delete/modify

public class ModifyAccountMenu extends GridPane {
  private EmailApp emailApp;
  private PasswordChangeMenu passwordChangeMenu;

  // update/delete menu
  private TextField userInputField;
  private TextField firstNameField;
  private TextField lastNameField;
  private TextField departmentText;
  private TextField mailCapacityField;
  private TextField emailText;

  // * MENUS

  public ModifyAccountMenu(EmailApp emailApp, PasswordChangeMenu passwordChangeMenu) {

    this.emailApp = emailApp;
    this.passwordChangeMenu = passwordChangeMenu;

    showSearchMenu();
  }

  // * search menu
  private void showSearchMenu() {
    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    Text findAccountIntroText = new Text("User Search");
    findAccountIntroText.getStyleClass().add("main-intro-text");
    add(findAccountIntroText, 0, 0, 2, 1);
    setHalignment(findAccountIntroText, HPos.CENTER);

    // - labels
    add(new Label("Account's username:"), 0, 1);

    // - text fields
    userInputField = new TextField();
    add(userInputField, 1, 1);
    // enter key listener
    userInputField.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        findAccount();
      }
    });

    // - buttons
    HBox buttonsBox = new HBox();

    // find account button
    Button userSearchBtn = new Button("Find Account");

    // back to main menu button
    Button backToMainMenuBtn = new Button("Back to Main Menu");

    buttonsBox.getChildren().addAll(userSearchBtn, backToMainMenuBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(userSearchBtn, Priority.ALWAYS);
    HBox.setHgrow(backToMainMenuBtn, Priority.ALWAYS);

    // - set actions
    userSearchBtn.setOnAction(e -> findAccount());
    backToMainMenuBtn.setOnAction(e -> emailApp.showStartMenu());

    add(buttonsBox, 0, 2, 2, 1);
  }

  // * allows modification of account details or deletion of account

  void showUpdateDeleteMenu(EmailAccount existingAccount) {
    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // - labels
    add(new Label("First Name:"), 0, 1);
    add(new Label("Last Name:"), 0, 2);
    add(new Label("Department:"), 0, 3);
    add(new Label("Email Address:"), 0, 4);
    add(new Label("Mailbox Capacity:"), 0, 5);

    // - text fields
    Text modAccountIntroText = new Text("Modifying account of " + existingAccount.getUsername() + ".");
    modAccountIntroText.getStyleClass().add("main-intro-text");
    add(modAccountIntroText, 0, 0, 2, 1);
    setHalignment(modAccountIntroText, HPos.CENTER);

    firstNameField = new TextField(existingAccount.getFirstName()); // modifiable
    add(firstNameField, 1, 1);

    lastNameField = new TextField(existingAccount.getLastName()); // modifiable
    add(lastNameField, 1, 2);

    departmentText = new TextField(existingAccount.getDepartment()); // display only
    departmentText.setEditable(false);
    departmentText.getStyleClass().add("display-only-textfield");
    add(departmentText, 1, 3);

    emailText = new TextField(existingAccount.getEmail()); // display only
    emailText.setEditable(false);
    emailText.getStyleClass().add("display-only-textfield");
    add(emailText, 1, 4);

    mailCapacityField = new TextField(String.valueOf(existingAccount.getMailCapacity())); // modifiable
    add(mailCapacityField, 1, 5);

    // - buttons
    HBox buttonsBox = new HBox();

    // updates all values in database
    Button updateAccountBtn = new Button("Update Account");

    // to change password menu
    Button changePasswordBtn = new Button("Change Password");

    // deletes account from database
    Button deleteAccountBtn = new Button("Delete Account");

    // returns to start menu
    Button homeBtn = new Button("Back to Main Menu");

    buttonsBox.getChildren().addAll(updateAccountBtn, changePasswordBtn, deleteAccountBtn, homeBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(updateAccountBtn, Priority.ALWAYS);
    HBox.setHgrow(changePasswordBtn, Priority.ALWAYS);
    HBox.setHgrow(deleteAccountBtn, Priority.ALWAYS);
    HBox.setHgrow(homeBtn, Priority.ALWAYS);

    // - set actions
    updateAccountBtn.setOnAction(e -> updateAccount(existingAccount));

    changePasswordBtn.setOnAction(e -> {
      passwordChangeMenu.showChangePasswordMenu();
      emailApp.showPasswordChangeMenu(passwordChangeMenu);
    });

    deleteAccountBtn.setOnAction(e -> deleteAccount(existingAccount));

    homeBtn.setOnAction(e -> emailApp.showStartMenu());

    add(buttonsBox, 0, 6, 2, 1);
  }

  // * UTILITY FUNCTIONS

  // * FIND ACCOUNT
  private void findAccount() {
    String usernameInput = userInputField.getText();

    // validation
    String validationMsg = Email.isNameValid(usernameInput);

    if (usernameInput.isEmpty()) {
      showErrorAlert("Required Field", "Missing Information", "Username Required!");
      return;
    }

    if (validationMsg != null) {
      showErrorAlert("Input Error", "Invalid Entry", validationMsg);
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

      // verify entered mail capacity is integer
      Optional<Integer> updatedMailCapacityOpt = parseAndValidateMailCapacity(mailCapacityField.getText());
      if (!updatedMailCapacityOpt.isPresent()) {
        showUpdateDeleteMenu(existingAccount);
        return;
      }
      int updatedMailCapacity = updatedMailCapacityOpt.get();

      String invalidFirstName = Email.isNameValid(updatedFirstName);
      String invalidLastName = Email.isNameValid(updatedLastName);
      String invalidMailCapacity = Email.isMailCapacityValid(updatedMailCapacity);

      if (!(invalidFirstName == null)) {
        showErrorAlert("Input Error", "Invalid Entry", invalidFirstName);

      } else if (!(invalidLastName == null)) {
        showErrorAlert("Input Error", "Invalid Entry", invalidLastName);

      } else if (!(invalidMailCapacity == null)) {
        showErrorAlert("Input Error", "Invalid Entry", invalidMailCapacity);

      } else {

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
      }
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

  // * PARSE/VALIDATE MAIL CAPACITY INPUT
  private Optional<Integer> parseAndValidateMailCapacity(String mailCapacityText) {
    try {
      int mailCapacity = Integer.parseInt(mailCapacityText);
      String validationMsg = Email.isMailCapacityValid(mailCapacity);
      if (validationMsg == null) {
        return Optional.of(mailCapacity);
      }
      showErrorAlert("Input Error", "Invalid Entry", validationMsg);
    } catch (NumberFormatException e) {
      showErrorAlert("Input Error", "Invalid Entry", "Mailbox capacity must be a whole number.");
    }
    return Optional.empty();
  }

  // * CREATE ALERTS
  private void showErrorAlert(String title, String header, String content) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }

}