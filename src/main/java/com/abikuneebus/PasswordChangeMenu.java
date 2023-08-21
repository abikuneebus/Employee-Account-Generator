package com.abikuneebus;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class PasswordChangeMenu extends GridPane {
  // declare instance variables
  private EmailAccount account;
  private ModifyAccountMenu modifyAccountMenu;

  // showChangePasswordMenu()
  private TextField changePWUsername;
  private TextField changePWNew;
  private TextField changePWConfirmNew;

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
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    String accountUsername = (account != null) ? account.getUsername() : "unknown";
    Text changePWAccountIntroText = new Text("Changing password of " + accountUsername + ".");

    changePWAccountIntroText.getStyleClass().add("menu-intro-text");
    add(changePWAccountIntroText, 0, 0, 2, 1);
    setHalignment(changePWAccountIntroText, HPos.CENTER);

    // - labels
    add(new Label("Username:"), 0, 1);
    add(new Label("Password"), 0, 2);
    add(new Label("Confirm Password"), 0, 3);

    // - text fields
    // username
    changePWUsername = new TextField();
    add(changePWUsername, 1, 1);

    // new password
    changePWNew = new PasswordField();
    add(changePWNew, 1, 2);

    // new password confirmation
    changePWConfirmNew = new PasswordField();
    add(changePWConfirmNew, 1, 3);

    // - buttons

    HBox buttonsBox = new HBox();

    // OK button
    Button passwordChangeBtn = new Button("Change Password");

    // cancel button
    Button cancelChangeBtn = new Button("Cancel");

    buttonsBox.getChildren().addAll(passwordChangeBtn, cancelChangeBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(passwordChangeBtn, Priority.ALWAYS);
    HBox.setHgrow(cancelChangeBtn, Priority.ALWAYS);

    // - set actions
    passwordChangeBtn.setOnAction(e -> changePassword(account));
    cancelChangeBtn.setOnAction(e -> modifyAccountMenu.showUpdateDeleteMenu(account));

    add(buttonsBox, 0, 4, 2, 1);
  }

  // ~ Utility

  private void changePassword(EmailAccount account) {
    String confirmUserInput = changePWUsername.getText();
    String newPasswordInput = changePWNew.getText();
    String confirmNewPasswordInput = changePWConfirmNew.getText();

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
      if (confirmUserInput.isEmpty() || newPasswordInput.isEmpty()
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

      // if user selects 'No', close dialog
    } else {
      confirmAlert.getDialogPane().getScene().getWindow().hide();
    }

  }

}