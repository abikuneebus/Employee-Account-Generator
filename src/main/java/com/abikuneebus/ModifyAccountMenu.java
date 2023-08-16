package com.abikuneebus;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

// * search for existing account & choice to delete/modify

public class ModifyAccountMenu extends GridPane {

  private EmailApp emailApp; // reference to main app class
  private TextField userInput;

  // update/delete menu
  private TextField firstNameField;
  private TextField lastNameField;
  private Text departmentText;
  private TextField mailCapacityField;
  private Text emailText;
  private String accountDepartment;
  private String accountEmail;

  // change password menu
  private TextField confirmUserTextField;
  private TextField passwordTextField;
  private TextField confirmPasswordTextField;
  private TextField newPasswordTextField;
  private TextField confirmNewPasswordTextField;

  // 'changePassword()'
  private String accountHashPass;

  // * MENUS

  public ModifyAccountMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
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
  private void showUpdateDeleteMenu(EmailAccount account) {
    getChildren().clear();
    String accountDepartment = account.getDepartment();
    String accountEmail = account.getEmail();

    // populating with retrieved account details & buttons to modify or delete them
    firstNameField = new TextField(account.getFirstName());
    add(firstNameField, 0, 0);

    lastNameField = new TextField(account.getLastName());
    add(lastNameField, 0, 1);

    departmentText = new Text(accountDepartment); // display only
    add(departmentText, 0, 2);

    emailText = new Text(accountEmail); // display only
    add(emailText, 1, 0);

    Button changePasswordBtn = new Button("Change Password");
    changePasswordBtn.setOnAction(e -> showPasswordMenu(account));
    add(changePasswordBtn, 1, 1);

    mailCapacityField = new TextField(String.valueOf(account.getMailCapacity()));
    add(mailCapacityField, 1, 2);

    Button updateAccountBtn = new Button("Update Account");
    updateAccountBtn.setOnAction(e -> updateAccount());
    add(updateAccountBtn, 2, 0);

    Button deleteAccountBtn = new Button("Delete Account");
    deleteAccountBtn.setOnAction(e -> deleteAccount(account));
    add(deleteAccountBtn, 2, 1);

    Button homeBtn = new Button("Back to Main Menu");
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
      showUpdateDeleteMenu(account);
    } else {
      // throws alert if not
      Alert alert = new Alert(Alert.AlertType.ERROR, "Username not found!");
      alert.showAndWait();
    }
  }

  // * UPDATE ACCOUNT DETAILS
  private void updateAccount() {
    // getting updated values from text fields
    String updatedFirstName = firstNameField.getText();
    String updatedLastName = lastNameField.getText();
    int updatedMailCapacity = Integer.parseInt(mailCapacityField.getText());

    // creating EmailAccount object with updated values
    EmailAccount updatedAccount = new EmailAccount(updatedFirstName, updatedLastName, accountEmail, updatedMailCapacity,
        accountDepartment, accountHashPass);

    // creating instance
    DatabaseManager dbManager = new DatabaseManager();

    // updating account in database
    dbManager.updateAccount(updatedAccount);
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

  // * password change menu
  private void showPasswordMenu(EmailAccount account) {
    getChildren().clear();

    String confirmUserInput = confirmUserTextField.getText();
    String passwordInput = passwordTextField.getText();
    String confirmPasswordInput = confirmPasswordTextField.getText();
    String newPasswordInput = newPasswordTextField.getText();
    String confirmNewPasswordInput = confirmNewPasswordTextField.getText();

    // "username" Text 0, 0
    add(new Text("Enter Username:"), 0, 0);

    // username TextField 0, 1
    TextField confirmUserTextField = new TextField();
    confirmUserTextField.setPromptText("johnSmith");
    add(confirmUserTextField, 0, 1);

    // 'Password' label
    add(new Text("Enter Password:"), 1, 0);

    // 'Password' input
    TextField passwordTextField = new TextField();
    passwordTextField.setPromptText("Your password..");
    add(passwordTextField, 1, 1);

    // 'Confirm Password' label
    add(new Text("Confirm Password:"), 2, 0);

    // 'Confirm Password' Input
    TextField confirmPasswordTextField = new TextField();
    confirmPasswordTextField.setPromptText("Your password again...");
    add(confirmPasswordTextField, 2, 1);

    // 'New Password' label
    add(new Text("Enter New Password:"), 3, 0);

    // 'New Password' Input
    TextField newPasswordTextField = new TextField();
    newPasswordTextField.setPromptText("New password...");
    add(newPasswordTextField, 3, 1);

    // 'Confirm New Password' label
    add(new Text("Confirm New Password:"), 4, 0);

    // 'Confirm New Password' Input
    TextField confirmNewPasswordTextField = new TextField();
    confirmNewPasswordTextField.setPromptText("New password again...");
    add(confirmNewPasswordTextField, 4, 1);

    // 'OK' button
    Button passwordChangeBtn = new Button("Change Password");
    passwordChangeBtn.setOnAction(e -> changePassword(account, confirmUserInput, passwordInput, confirmPasswordInput,
        newPasswordInput, confirmNewPasswordInput));
    add(passwordChangeBtn, 5, 0);

    // 'Cancel' button
    Button cancelChangeBtn = new Button("Cancel");
    cancelChangeBtn.setOnAction(e -> showUpdateDeleteMenu(account));
    add(cancelChangeBtn, 5, 1);
  }

  // * CHANGE PASSWORD

  private void changePassword(EmailAccount account, String confirmUserInput, String passwordInput,
      String confirmPasswordInput, String newPasswordInput, String confirmNewPasswordInput) {

    // verify old & new passwords match
    String confirmedOGPassword = (passwordInput == confirmPasswordInput ? passwordInput : null);
    String confirmedNewPassword = (newPasswordInput == confirmNewPasswordInput ? newPasswordInput : null);

    // old passwords, hashed
    String hashedOGPassword = account.getHashedPassword();
    String hashedOGPasswordTry = BCrypt.hashpw(new String(confirmedOGPassword), BCrypt.gensalt());

    // new passwords, hashed

    // new password, char[]
    char[] charNewPassword = confirmedNewPassword.toCharArray();

    Email email = new Email(null, null, null);

    if (confirmedOGPassword == null || confirmedNewPassword == null) {
      Alert mismatchAlert = new Alert(Alert.AlertType.ERROR, "Passwords do not match!");
      mismatchAlert.showAndWait();
      showPasswordMenu(account);

    } else if (hashedOGPassword != hashedOGPasswordTry) {
      Alert incorrectAlert = new Alert(Alert.AlertType.ERROR, "Incorrect password!");
      incorrectAlert.showAndWait();
      showPasswordMenu(account);

    } else {
      String passwordValidationResult = email.isPasswordValid(charNewPassword);
      if (passwordValidationResult != null) {
        Alert invalidAlert = new Alert(Alert.AlertType.ERROR);
        invalidAlert.setTitle("Invalid Password");
        invalidAlert.setContentText(passwordValidationResult);
        invalidAlert.showAndWait();

      } else {
        String hashedNewPassword = BCrypt.hashpw(new String(confirmedNewPassword), BCrypt.gensalt());

        // TODO replace 'hashedPassword' with 'hashedNewPassword' in database
      }
    }

  }

}