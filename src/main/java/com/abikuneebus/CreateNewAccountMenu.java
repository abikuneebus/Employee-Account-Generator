package com.abikuneebus;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class CreateNewAccountMenu extends GridPane {
  private DatabaseManager dbManager;

  private EmailApp emailApp; // reference to main app class

  public CreateNewAccountMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  private void initUI() {
    getChildren().clear();
    // setPrefSize(750, 350);
    // setMinSize(750, 350);
    // setMaxSize(750, 350);
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    Text newAccountIntroText = new Text("Please enter user's information.");
    newAccountIntroText.getStyleClass().add("main-intro-text");
    add(newAccountIntroText, 0, 0, 2, 1);
    setHalignment(newAccountIntroText, HPos.CENTER);

    // - labels
    add(new Label("First Name:"), 0, 1);
    add(new Label("Last Name:"), 0, 2);
    add(new Label("Department:"), 0, 3);

    // - text fields
    TextField firstNameInput = new TextField();
    add(firstNameInput, 1, 1);

    TextField lastNameInput = new TextField();
    add(lastNameInput, 1, 2);

    ComboBox<String> departmentCmbo = new ComboBox<>();
    departmentCmbo.getItems().addAll(
        "Accounting",
        "Admin",
        "IT",
        "Development",
        "Sales",
        "N/A");
    departmentCmbo.getSelectionModel().select("N/A");
    add(departmentCmbo, 1, 3);

    // - buttons

    HBox buttonsBox = new HBox();

    // create account
    Button createAccountBtn = new Button("Create Account");

    // cancel
    Button cancelBtn = new Button("Cancel");

    buttonsBox.getChildren().addAll(createAccountBtn, cancelBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(createAccountBtn, Priority.ALWAYS);
    HBox.setHgrow(cancelBtn, Priority.ALWAYS);

    // - set actions

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

    cancelBtn.setOnAction(e -> emailApp.showStartMenu());

    add(buttonsBox, 0, 4, 2, 1);
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