package com.abikuneebus;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

// public class EmailAppGUI extends Application { //?
public class EmailAppGUI {
  private TextField firstNameInput;
  private TextField lastNameInput;
  private ComboBox<String> departmentComboBox;
  private Text accountInfo;
  private Email currentEmail;
  private GridPane grid;
  private EmailApp emailApp;

  public EmailAppGUI(EmailApp emailApp) {
    this.emailApp = emailApp;

    // START PAGE
    this.grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 10, 10, 10));

    Text firstNameText = new Text("First Name:");
    firstNameInput = new TextField();
    grid.add(firstNameText, 0, 0);
    grid.add(firstNameInput, 1, 0);

    Text lastNameText = new Text("Last Name:");
    lastNameInput = new TextField();
    grid.add(lastNameText, 0, 1);
    grid.add(lastNameInput, 1, 1);

    Text departmentText = new Text("Department:");
    departmentComboBox = new ComboBox<>();
    departmentComboBox.getItems().addAll(
        "Accounting",
        "Admin",
        "IT",
        "Development",
        "Sales",
        "N/A");
    departmentComboBox.getSelectionModel().select("N/A");
    grid.add(departmentText, 0, 2);
    grid.add(departmentComboBox, 1, 2);

    Button createButton = new Button("Generate Account");
    createButton.setOnAction(e -> createNewAccount());
    grid.add(createButton, 0, 3);

    accountInfo = new Text();
    grid.add(accountInfo, 0, 4);
    GridPane.setColumnSpan(accountInfo, 2);

    EmailApp.accounts = EmailApp.getAccountsFromJson();
  }

  public GridPane getGridPane() {
    return grid;
  }

  public void stop() {
    firstNameInput.clear();
    lastNameInput.clear();
    departmentComboBox.getSelectionModel().select("N/A");
  }

  private void createNewAccount() {
    String firstName = firstNameInput.getText();
    String lastName = lastNameInput.getText();
    String department = departmentComboBox.getValue();

    if (firstName.isEmpty() || lastName.isEmpty()) {
      showAlert("Both first name and last name must be entered!");
      return;
    }

    String firstNameValid = Email.isNameValid(firstName);
    if (firstNameValid != null) {
      showAlert(firstNameValid);
      return;
    }

    String lastNameValid = Email.isNameValid(lastName);
    if (lastNameValid != null) {
      showAlert(lastNameValid);
      return;
    }

    currentEmail = new Email(firstName, lastName, department);
    emailApp.addAccount(currentEmail);
    showAlert("Account creation successful!");
    updateAccountInfo();
  }

  private void updateAccountInfo() {
    if (currentEmail == null) {
      accountInfo.setText("");
    } else {
      accountInfo.setText("Name: " + currentEmail.getName() +
          "\nUsername: " + currentEmail.getUsername() +
          "\nDepartment: " + currentEmail.getDepartment() +
          "\nEmail: " + currentEmail.getEmail() +
          "\nMailbox Capacity: " + currentEmail.getMailCapacity());
    }
  }

  private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}