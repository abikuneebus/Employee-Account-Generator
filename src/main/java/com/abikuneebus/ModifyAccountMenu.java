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

public class ModifyAccountMenu extends GridPane {
  private EmailApp emailApp;

  // update/delete menu
  private TextField userInputField;
  private TextField firstNameField;
  private TextField lastNameField;
  private TextField departmentText;
  private TextField mailCapacityField;
  private TextField emailText;

  // * MENUS

  public ModifyAccountMenu(EmailApp emailApp) {

    this.emailApp = emailApp;

    showSearchMenu();
  }

  // * search menu
  private void showSearchMenu() {
    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    Label findAccountIntroText = new Label("User Search");
    findAccountIntroText.getStyleClass().add("menu-intro-text");
    add(findAccountIntroText, 0, 0, 2, 1);
    setHalignment(findAccountIntroText, HPos.CENTER);

    // - labels
    add(new Label("Username:"), 0, 1);

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
    backToMainMenuBtn.setOnAction(e -> {
      System.out.println("Back to Main Menu button clicked.");
      if (emailApp != null) {
        emailApp.showStartMenu();
      } else
        System.out.println("emailApp is null!");
    });

    add(buttonsBox, 0, 2, 2, 1);
  }

  // - allows modification of account details or deletion of account

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

    Label modAccountIntroText = new Label("Modifying Account of " + existingAccount.getUsername());
    modAccountIntroText.getStyleClass().add("menu-intro-text");
    add(modAccountIntroText, 0, 0, 2, 1);
    setHalignment(modAccountIntroText, HPos.CENTER);

    // - text fields
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
      PasswordChangeMenu passwordChangeMenu = new PasswordChangeMenu(emailApp, existingAccount);
      passwordChangeMenu.showChangePasswordMenu();
      emailApp.showPasswordChangeMenu(passwordChangeMenu);

      // 'Enter' = 'Update Account' button press
      this.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.ENTER) {
          updateAccount(existingAccount);
        }
      });

    });

    deleteAccountBtn.setOnAction(e -> deleteAccount(existingAccount));

    homeBtn.setOnAction(e -> emailApp.showStartMenu());

    add(buttonsBox, 0, 6, 2, 1);
  }

  // * UTILITY FUNCTIONS

  // • find account via username
  private void findAccount() {
    String usernameInput = userInputField.getText();

    // validation
    String validationMsg = Email.isNameValid(usernameInput);

    if (usernameInput.isEmpty()) {
      AlertUtils.showEmptyAlert();
      return;
    }

    if (validationMsg != null) {
      AlertUtils.showWarnAlert("Input Error", "Invalid Entry", validationMsg);
      return;
    }

    // populating form with account details if account exists
    DatabaseManager dbManager = DatabaseManager.getInstance();
    dbManager.connect();
    EmailAccount account = dbManager.getAccountByUsername(usernameInput);
    dbManager.disconnect();
    if (account == null) {
      AlertUtils.showAlert(Alert.AlertType.ERROR, "User Search", "Not Found",
          "Sorry, " + usernameInput + " not found.");
      return;
    }

    showUpdateDeleteMenu(account);
  }

  // • updates first name, last name &/or mailbox capacity
  private void updateAccount(EmailAccount existingAccount) {

    if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()
        || mailCapacityField.getText().isEmpty()) {
      AlertUtils.showEmptyAlert();
      return;
    }

    String alertMsgUsername = existingAccount.getUsername();

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");
    Optional<ButtonType> result = AlertUtils.showCustomConfirmAlert("Update Account", "Confirmation",
        "Update account of " + alertMsgUsername + "?", btnYes, btnNo);

    if (result.isPresent() && result.get() == btnYes) {

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
        AlertUtils.showErrAlert("Input Error", "Invalid Entry", invalidFirstName);

      } else if (!(invalidLastName == null)) {
        AlertUtils.showErrAlert("Input Error", "Invalid Entry", invalidLastName);

      } else if (!(invalidMailCapacity == null)) {
        AlertUtils.showErrAlert("Input Error", "Invalid Entry", invalidMailCapacity);

      } else {

        // create EmailAccount object with updated values
        EmailAccount updatedAccount = new EmailAccount(updatedFirstName, updatedLastName, existingAccount.getEmail(),
            updatedMailCapacity, existingAccount.getDepartment(), existingAccount.getUsername(),
            existingAccount.getHashedPassword());

        // update account in database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.connect();
        dbManager.updateAccount(updatedAccount);
        dbManager.disconnect();

        // return to home menu
        emailApp.showStartMenu();
      }
    } else
      AlertUtils.showInfoAlert("Update Account", "Cancelled", "Account update cancelled!");
    showUpdateDeleteMenu(existingAccount);

  }

  // • deletes account
  private void deleteAccount(EmailAccount account) {
    String alertMsgUsername = account.getUsername();

    ButtonType btnYes = new ButtonType("Yes");
    ButtonType btnNo = new ButtonType("No");
    Optional<ButtonType> result = AlertUtils.showCustomConfirmAlert("Delete Account", "Confirmation",
        "Delete account of " + alertMsgUsername + "?", btnYes, btnNo);

    if (result.isPresent() && result.get() == btnYes) {

      // deleting account
      DatabaseManager dbManager = DatabaseManager.getInstance();
      dbManager.connect();
      boolean successfulDelete = dbManager.deleteAccount(account.getEmail());
      dbManager.disconnect();

      if (successfulDelete) {

        // showing successful deletion confirmation
        AlertUtils.showAlert(AlertType.INFORMATION, "Delete Account", "Success",
            "Account of " + alertMsgUsername + " deleted.");

        // returning to home menu
        emailApp.showStartMenu();

      } else {
        // showing error message
        AlertUtils.showAlert(AlertType.ERROR, "Delete Account", "Failure",
            "Error deleting account of " + alertMsgUsername + ", please try again.");

        // returning to home menu
        emailApp.showStartMenu();
      }
    } else {
      // if user does not want to proceed with account deletion ("No")
      AlertUtils.showInfoAlert("Delete Account", "Cancelled", "Account deletion cancelled!");
      showUpdateDeleteMenu(account);
    }
  }

  // • validates mailbox capacity update input
  private Optional<Integer> parseAndValidateMailCapacity(String mailCapacityText) {
    try {
      int mailCapacity = Integer.parseInt(mailCapacityText);
      String validationMsg = Email.isMailCapacityValid(mailCapacity);
      if (validationMsg == null) {
        return Optional.of(mailCapacity);
      }
      AlertUtils.showErrAlert("Input Error", "Invalid Entry", validationMsg);
    } catch (NumberFormatException e) {
      AlertUtils.showErrAlert("Input Error", "Invalid Entry", "Mailbox capacity must be a whole number.");
    }
    return Optional.empty();
  }

}