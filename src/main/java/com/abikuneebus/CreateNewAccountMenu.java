package com.abikuneebus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CreateNewAccountMenu extends GridPane {
  private DatabaseManager dbManager;

  private EmailApp emailApp; // reference to main app class

  public CreateNewAccountMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  private void initUI() {
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // first name
    Label firstNameLbl = new Label("First Name:");
    TextField firstNameInput = new TextField();
    add(firstNameLbl, 0, 0);
    add(firstNameInput, 1, 0);

    // last name
    Label lastNameLbl = new Label("Last Name:");
    TextField lastNameInput = new TextField();
    add(lastNameLbl, 0, 1);
    add(lastNameInput, 1, 1);

    // department
    Label departmentLbl = new Label("Department:");
    ComboBox<String> departmentCmbo = new ComboBox<>();
    departmentCmbo.getItems().addAll(
        "Accounting",
        "Admin",
        "IT",
        "Development",
        "Sales",
        "N/A");
    departmentCmbo.getSelectionModel().select("N/A");
    add(departmentLbl, 0, 2);
    add(departmentCmbo, 1, 2);

    // create account button
    Button createAccountBtn = new Button("Create Account");
    createAccountBtn.setOnAction(e -> {
      String firstName = firstNameInput.getText();
      String lastName = lastNameInput.getText();
      String department = departmentCmbo.getSelectionModel().getSelectedItem().toString();

      if (firstName.isEmpty() || lastName.isEmpty() || department == null || department.isEmpty()) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText("Missing Information");
        alert.setContentText("All fields required!");
        alert.showAndWait();
        return;
      }

      // creating Email object
      Email email = new Email(firstName, lastName, department);

      // calling addAccount() in EmailApp with Email as argument
      addAccount(email);

      // returning to start menu
      emailApp.showStartMenu();
    });

    add(createAccountBtn, 1, 3);

  }

  public void addAccount(Email email) {
    EmailAccount account = new EmailAccount(email.getFirstName(), email.getLastName(), email.getEmail(),
        email.getMailCapacity(), email.getDepartment(), email.getUsername(), email.getHashedPassword());

    dbManager = new DatabaseManager();
    dbManager.connect();
    dbManager.insertEmailAccount(account);
    dbManager.disconnect();

    EmailApp.accounts.add(account);
  }

}