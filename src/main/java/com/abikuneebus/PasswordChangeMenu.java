package com.abikuneebus;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    setDefaultButtonSize(200);
    setPrefSize(800, 300);

    // initializing UI
    showChangePasswordMenu();
  }

  private void setDefaultButtonSize(int width) {
    getChildren().stream().filter(node -> node instanceof Button).forEach(node -> ((Button) node).setMinWidth(width));
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
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // username
    add(new Text("Enter Username:"), 0, 0);
    changePWUsername = new TextField();
    changePWUsername.setPromptText("johnSmith");
    add(changePWUsername, 1, 0);

    // current password
    add(new Text("Enter Password:"), 0, 1);
    changePWExisting = new TextField();
    changePWExisting.setPromptText("Your password..");
    add(changePWExisting, 1, 1);

    // new password
    add(new Text("Enter New Password:"), 0, 3);
    changePWNew = new TextField();
    changePWNew.setPromptText("New password...");
    add(changePWNew, 1, 3);

    // new password confirmation
    add(new Text("Confirm New Password:"), 0, 4);
    changePWConfirmNew = new TextField();
    changePWConfirmNew.setPromptText("New password again...");
    add(changePWConfirmNew, 1, 4);

    String confirmUserInput = changePWUsername.getText();
    String passwordInput = changePWExisting.getText();
    String newPasswordInput = changePWNew.getText();
    String confirmNewPasswordInput = changePWConfirmNew.getText();

    // OK button
    Button passwordChangeBtn = new Button("Change Password");
    passwordChangeBtn.setMinWidth(200);
    passwordChangeBtn.setOnAction(e -> changePassword(account, confirmUserInput, passwordInput,
        newPasswordInput, confirmNewPasswordInput));
    add(passwordChangeBtn, 0, 5);

    // cancel button
    Button cancelChangeBtn = new Button("Cancel");
    cancelChangeBtn.setMinWidth(200);
    cancelChangeBtn.setOnAction(e -> modifyAccountMenu.showUpdateDeleteMenu(account));
    add(cancelChangeBtn, 1, 5);
  }

  // ~ Utility

  private void changePassword(EmailAccount account, String confirmUserInput, String passwordInput,
      String newPasswordInput, String confirmNewPasswordInput) {

    // - confirm intent
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirmation");
    confirmAlert.setHeaderText("Password Change");
    confirmAlert.setContentText("Are you sure you want to change your password?");

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");

    confirmAlert.getButtonTypes().setAll(btnYes, btnNo);

    Optional<ButtonType> result = confirmAlert.showAndWait();

    // user confirms intent
    if (result.get() == btnYes) {

      // check for empty fields
      if (confirmUserInput.isEmpty() || passwordInput.isEmpty() || newPasswordInput.isEmpty()
          || confirmNewPasswordInput.isEmpty()) {
        Alert emptyAlert = new Alert(AlertType.WARNING);
        emptyAlert.setTitle("Input Error");
        emptyAlert.setHeaderText("Missing Information");
        emptyAlert.setContentText("All fields required!");
        emptyAlert.showAndWait();
      }

      // - verify new password input matches
      // if not identical
      if (!(newPasswordInput.equals(confirmNewPasswordInput))) {
        // show alert
        Alert mismatchAlert = new Alert(Alert.AlertType.ERROR);
        mismatchAlert.setTitle("Input Error");
        mismatchAlert.setContentText(
            "Entered new password value and new password confirmation value do not match! Please try again, entering the same value for both fields.");
        mismatchAlert.showAndWait();
      }

      // - verify existing username & password entered correctly
      String usernameOG = this.account.getUsername();
      String hashedOGPassword = this.account.getHashedPassword();
      boolean passwordMatches = BCrypt.checkpw(passwordInput, hashedOGPassword);

      if (!((confirmUserInput.equals(usernameOG)) && (passwordMatches == true))) {
        attempts++;
        Alert credAlert = new Alert(Alert.AlertType.ERROR);
        credAlert.setTitle("Credential Verification Error");
        credAlert.setContentText("Incorrect username or password! Please try again.");
        credAlert.showAndWait();
        // terminate app upon 10 incorrect username/password entries
        if (attempts >= 10) {
          Alert limitAlert = new Alert(Alert.AlertType.ERROR);
          limitAlert.setTitle("Over Limit Error");
          limitAlert.setContentText(
              "Maximum attempts reached! Account access temporarily restricted, please contact your IT administrator.");
          limitAlert.showAndWait();
          // terminates upon acknowledgment
          System.exit(0);
        }
      } else {
        attempts = 0;

        // - new password input validation
        // validating password
        char[] charNewPassword = confirmNewPasswordInput.toCharArray();
        Email email = new Email(null, null, null);
        String passwordValidationResult = email.isPasswordValid(charNewPassword);

        // if invalid, display alert with explanation
        if (passwordValidationResult != null) {
          Alert invalidAlert = new Alert(Alert.AlertType.ERROR);
          invalidAlert.setTitle("Invalid Password");
          invalidAlert.setContentText(passwordValidationResult);
          invalidAlert.showAndWait();
        }

        // - update password
        // encrypting new password
        String hashedPassword = BCrypt.hashpw(new String(confirmNewPasswordInput), BCrypt.gensalt());
        // creating EmailAccount object with updated password
        EmailAccount updatedAccount = new EmailAccount(account.getFirstName(), account.getLastName(),
            account.getEmail(), account.getMailCapacity(), account.getDepartment(), account.getUsername(),
            hashedPassword);

        // updating database
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();
        dbManager.updatePassword(updatedAccount);
        dbManager.disconnect();

      }
      // if user selects 'No', close dialog
    } else {
      confirmAlert.getDialogPane().getScene().getWindow().hide();
    }

  }

}