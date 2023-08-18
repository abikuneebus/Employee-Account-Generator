package com.abikuneebus;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class PasswordChangeMenu extends GridPane {
  // declare instance variables
  private EmailAccount account;
  private ModifyAccountMenu modifyAccountMenu;

  // showChangePasswordMenu()
  private TextField changePWUsername;
  private TextField changePWExisting;
  private TextField changePWNew;
  private TextField changePWConfirmNew;
  private int attempts = 0;

  public PasswordChangeMenu(EmailApp emailApp, EmailAccount account, ModifyAccountMenu modifyAccountMenu) {
    this.account = account;
    this.modifyAccountMenu = modifyAccountMenu;

    // initializing UI
    showChangePasswordMenu();

  }

  // update account from outside of class
  public void setAccount(EmailAccount account) {
    this.account = account;
  }

  public void setModifyAccountMenu(ModifyAccountMenu modifyAccountMenu) {
    this.modifyAccountMenu = modifyAccountMenu;
  }

  // ~ Password Change Menu
  void showChangePasswordMenu() {
    getChildren().clear();

    String confirmUserInput = changePWUsername.getText();
    String passwordInput = changePWExisting.getText();
    String newPasswordInput = changePWNew.getText();
    String confirmNewPasswordInput = changePWConfirmNew.getText();

    // "username" Text 0, 0
    add(new Text("Enter Username:"), 0, 0);

    // username TextField 0, 1
    changePWUsername = new TextField();
    changePWUsername.setPromptText("johnSmith");
    add(changePWUsername, 0, 1);

    // 'Password' label
    add(new Text("Enter Password:"), 1, 0);

    // 'Password' input
    changePWExisting = new TextField();
    changePWExisting.setPromptText("Your password..");
    add(changePWExisting, 1, 1);

    // 'New Password' label
    add(new Text("Enter New Password:"), 3, 0);

    // 'New Password' Input
    changePWNew = new TextField();
    changePWNew.setPromptText("New password...");
    add(changePWNew, 3, 1);

    // 'Confirm New Password' label
    add(new Text("Confirm New Password:"), 4, 0);

    // 'Confirm New Password' Input
    changePWConfirmNew = new TextField();
    changePWConfirmNew.setPromptText("New password again...");
    add(changePWConfirmNew, 4, 1);

    // 'OK' button
    Button passwordChangeBtn = new Button("Change Password");
    passwordChangeBtn.setOnAction(e -> changePassword(account, confirmUserInput, passwordInput,
        newPasswordInput, confirmNewPasswordInput));
    add(passwordChangeBtn, 5, 0);

    // 'Cancel' button
    Button cancelChangeBtn = new Button("Cancel");
    cancelChangeBtn.setOnAction(e -> modifyAccountMenu.showUpdateDeleteMenu(account));
    add(cancelChangeBtn, 5, 1);
    // - password menu logic
  }

  // ~ Utility

  private void changePassword(EmailAccount account, String confirmUserInput, String passwordInput,
      String newPasswordInput, String confirmNewPasswordInput) {

    // - 1) confirm intent to change password
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirmation");
    confirmAlert.setHeaderText("Password Change");
    confirmAlert.setContentText("Are you sure you want to change your password?");

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");

    confirmAlert.getButtonTypes().setAll(btnYes, btnNo);

    Optional<ButtonType> result = confirmAlert.showAndWait();

    // if user confirms password change request ("Yes")
    if (result.get() == btnYes) {

      // - 2) check for empty fields
      if (confirmUserInput.isEmpty() || passwordInput.isEmpty() || newPasswordInput.isEmpty()
          || confirmNewPasswordInput.isEmpty()) {
        Alert emptyAlert = new Alert(AlertType.WARNING);
        emptyAlert.setTitle("Input Error");
        emptyAlert.setHeaderText("Missing Information");
        emptyAlert.setContentText("All fields required!");
        emptyAlert.showAndWait();
      }

      // - 3) verify both new password values match
      // if not identical
      if (!(newPasswordInput.equals(confirmNewPasswordInput))) {
        // show alert
        Alert mismatchAlert = new Alert(Alert.AlertType.ERROR);
        mismatchAlert.setTitle("Input Error");
        mismatchAlert.setContentText(
            "Entered new password value and new password confirmation value do not match! Please try again, entering the same value for both fields.");
        mismatchAlert.showAndWait();
      }

      // - 4) verify password is correct
      String usernameOG = this.account.getUsername();
      String hashedOGPassword = this.account.getHashedPassword();
      boolean passwordMatches = BCrypt.checkpw(passwordInput, hashedOGPassword);

      if (!((confirmUserInput.equals(usernameOG)) && (passwordMatches == true))) {
        attempts++;
        Alert credAlert = new Alert(Alert.AlertType.ERROR);
        credAlert.setTitle("Credential Verification Error");
        credAlert.setContentText("Incorrect username or password! Please try again.");
        credAlert.showAndWait();

        if (attempts >= 10) {
          Alert limitAlert = new Alert(Alert.AlertType.ERROR);
          limitAlert.setTitle("Over Limit Error");
          limitAlert.setContentText(
              "Maximum attempts reached! Account access temporarily restricted, please contact your IT administrator.");
          limitAlert.showAndWait();
          // terminate app execution upon acknowledgment
          System.exit(0);
        }
      } else {
        attempts = 0;

        // - 5) new password input validation
        // passing new password into password validator
        char[] charNewPassword = confirmNewPasswordInput.toCharArray();
        Email email = new Email(null, null, null);
        String passwordValidationResult = email.isPasswordValid(charNewPassword);

        // if password is invalid, display alert with explanation
        if (passwordValidationResult != null) {
          Alert invalidAlert = new Alert(Alert.AlertType.ERROR);
          invalidAlert.setTitle("Invalid Password");
          invalidAlert.setContentText(passwordValidationResult);
          invalidAlert.showAndWait();
        }

        // - 6) perform password update
        // encrypting new password
        String hashedPassword = BCrypt.hashpw(new String(confirmNewPasswordInput), BCrypt.gensalt());
        // creating EmailAccount object with updated password
        EmailAccount updatedAccount = new EmailAccount(account.getFirstName(), account.getLastName(),
            account.getEmail(), account.getMailCapacity(), account.getDepartment(), hashedPassword);

        // updating database
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.updatePassword(updatedAccount);

      }
      // if user selects 'No', close dialog
    } else {
      confirmAlert.getDialogPane().getScene().getWindow().hide();
    }

  }

}