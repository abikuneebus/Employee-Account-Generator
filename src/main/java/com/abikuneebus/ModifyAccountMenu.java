package com.abikuneebus;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

// * search for existing account & choice to delete/modify

public class ModifyAccountMenu extends GridPane {

  private EmailApp emailApp; // reference to main app class
  private TextField firstNameField;
  private TextField lastNameField;
  private TextField usernameField;
  private TextField departmentField;
  private TextField mailCapacityField;

  public ModifyAccountMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  private void initUI() {
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // username input
    Label userLbl = new Label("Username:");
    TextField userInput = new TextField();
    add(userLbl, 0, 0);
    add(userInput, 0, 1);

    // TODO 'Back to Main Menu' button

    // username search
    Button userSearchBtn = new Button("Search");
    userSearchBtn.setOnAction(e -> {
      String username = userInput.getText();
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

      DatabaseManager dbManager = new DatabaseManager();
      EmailAccount account = dbManager.getAccountByUsername(username);
      if (account != null) {
        showUpdateDeleteMenu(account);
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Username not found!");
        alert.showAndWait();
      }
    });
  }

  private void showUpdateDeleteMenu(EmailAccount account) {
    firstNameField.setText(account.getFirstName());
    lastNameField.setText(account.getLastName());
    departmentField.setText(account.getDepartment());
    usernameField.setText(account.getUsername());
    mailCapacityField.setText(account.getMailCapacity() + "");
  }

}

// findAccount()
// • calls 'DatabaseManager.selectAccount()' to find account by username

//TODO incorporate 'UpdateDeleteMenu'
// initialize()
  // • sets up UI elements for existing account modification or deletion

  // updateAccount()
  // • collects updated fields
  // • calls 'DatabaseManager.updateAccount()' to apply changes

  // deleteAccount()
  // • calls 'DatabaseManager.deleteAccount()' to remove selected account
