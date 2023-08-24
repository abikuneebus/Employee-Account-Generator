package com.abikuneebus;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PasswordChangeMenu extends GridPane {
  // declare instance variables
  private EmailAccount account;
  private EmailApp emailApp;

  // showChangePasswordMenu()
  private PasswordField changePWNew;
  private PasswordField changePWConfirmNew;

  public PasswordChangeMenu(EmailApp emailApp, EmailAccount account) {
    this.account = account;
    this.emailApp = emailApp;

    // initializing UI
    showChangePasswordMenu();
  }

  // update account from outside of class
  public void setAccount(EmailAccount account) {
    this.account = account;
    showChangePasswordMenu();
  }

  // ~ Password Change Menu
  void showChangePasswordMenu() {

    if (changePWNew != null) {
      changePWNew.setText("");
    }
    if (changePWConfirmNew != null) {
      changePWConfirmNew.setText("");
    }

    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // - labels
    Label changePWAccountIntroText = new Label("Enter New Password");
    changePWAccountIntroText.getStyleClass().add("menu-intro-text");
    add(changePWAccountIntroText, 0, 0, 2, 1);
    setHalignment(changePWAccountIntroText, HPos.CENTER);

    add(new Label("Password"), 0, 1);
    add(new Label("Confirm Password"), 0, 2);

    // - defining text fields
    // new password
    changePWNew = new PasswordField();
    // new password confirmation
    changePWConfirmNew = new PasswordField();

    // - all fields listen for Enter
    EventHandler<KeyEvent> enterKeyListener = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        changePassword(account);
      }
    };
    changePWNew.setOnKeyPressed(enterKeyListener);
    changePWConfirmNew.setOnKeyPressed(enterKeyListener);

    // - adding text fields
    add(changePWNew, 1, 1);
    add(changePWConfirmNew, 1, 2);

    // - buttons
    HBox buttonsBox = new HBox();

    // 'OK'
    Button passwordChangeBtn = new Button("Change Password");

    // disable if any fields are empty
    passwordChangeBtn.disableProperty()
        .bind((changePWNew.textProperty().isEmpty()).or(changePWConfirmNew.textProperty().isEmpty()));

    // 'cancel'
    Button cancelChangeBtn = new Button("Cancel");

    buttonsBox.getChildren().addAll(passwordChangeBtn, cancelChangeBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(passwordChangeBtn, Priority.ALWAYS);
    HBox.setHgrow(cancelChangeBtn, Priority.ALWAYS);

    // - set actions
    passwordChangeBtn.setOnAction(e -> changePassword(account));
    cancelChangeBtn.setOnAction(e -> returnToUpdateDelete(account));

    add(buttonsBox, 0, 4, 2, 1);
  }

  // ~ Utility
  // * RETURN TO UPDATE/DELETE MENU
  private void returnToUpdateDelete(EmailAccount account) {
    emailApp.showUpdateDeleteMenu(account);
  }

  // * CHANGE PASSWORD
  private void changePassword(EmailAccount account) {
    String newPasswordInput = changePWNew.getText();
    String confirmNewPasswordInput = changePWConfirmNew.getText();

    // - confirm intent
    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");
    Optional<ButtonType> result = AlertUtils.showCustomConfirmAlert("Change Password", "Confirmation",
        "Are you sure you want to change your password?", btnYes, btnNo);

    // user confirms intent
    if (result.isPresent() && result.get() == btnYes) {

      // check for empty fields
      if (newPasswordInput.isEmpty()
          || confirmNewPasswordInput.isEmpty()) {

        AlertUtils.showEmptyAlert();
        clearForm();
        return;
      }

      // - verify new password input matches
      // if not identical
      if (!(newPasswordInput.equals(confirmNewPasswordInput))) {
        // show alert
        AlertUtils.showAlert(AlertType.ERROR, "Validation", newPasswordInput, "Please ensure both passwords match!");
        clearForm();
        return;
      }

      // - new password input validation
      // validating password
      char[] charNewPassword = confirmNewPasswordInput.toCharArray();
      String passwordValidationResult = Email.isPasswordValid(charNewPassword);

      // if invalid, display alert with explanation
      if (passwordValidationResult != null) {
        AlertUtils.showAlert(AlertType.ERROR, "Validation", "Invalid Password", passwordValidationResult);
        clearForm();
        return;
      }

      // - update password
      // encrypting new password
      String hashedPassword = BCrypt.hashpw(new String(confirmNewPasswordInput), BCrypt.gensalt());
      // creating EmailAccount object with updated password
      EmailAccount updatedAccount = new EmailAccount(account.getFirstName(), account.getLastName(),
          account.getEmail(), account.getMailCapacity(), account.getDepartment(), account.getUsername(),
          hashedPassword);

      // updating database
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.connect();
      dbManager.updatePassword(updatedAccount);
      dbManager.disconnect();
      returnToUpdateDelete(updatedAccount);
      clearForm();

      // if user selects 'No', close dialog
    } else {
      AlertUtils.showInfoAlert("Password Change", "Cancelled", "Password change cancelled!");
      clearForm();
      return;
    }
  }

  // clearing state
  void clearForm() {
    if (changePWNew != null) {
      changePWNew.setText("");
    }
    if (changePWConfirmNew != null) {
      changePWConfirmNew.setText("");
    }
  }

}