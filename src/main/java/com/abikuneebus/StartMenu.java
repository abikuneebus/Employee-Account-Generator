package com.abikuneebus;

import org.mindrot.jbcrypt.BCrypt;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class StartMenu extends GridPane {
  private TextField loginUsername;
  private PasswordField loginPassword;
  private int loginTries = 0;

  private EmailApp emailApp; // reference to main app class

  public StartMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  // initializing UI elements for start menu
  private void initUI() {
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    Text loginText = new Text("Log In");
    loginText.getStyleClass().add("menu-intro-text");
    add(loginText, 0, 0, 2, 1);
    setHalignment(loginText, HPos.CENTER);

    // - labels
    add(new Label("Username:"), 0, 1);
    add(new Label("Password:"), 0, 2);

    // - defining text fields
    loginUsername = new TextField();
    loginPassword = new PasswordField();

    // - all fields listen for enter
    EventHandler<KeyEvent> enterKeyListener = e -> {
      if (e.getCode() == KeyCode.ENTER) {
        performLogin();
      }
    };

    loginUsername.setOnKeyPressed(enterKeyListener);
    loginPassword.setOnKeyPressed(enterKeyListener);

    // - adding text fields
    add(loginUsername, 1, 1);
    add(loginPassword, 1, 2);

    // - buttons
    Button loginBtn = new Button("Login");
    HBox loginBtnBox = new HBox(loginBtn);
    loginBtnBox.setAlignment(Pos.CENTER);
    add(loginBtnBox, 0, 3, 2, 1);

    // - set action
    loginBtn.setOnAction(e -> {
      performLogin();
    });

  }

  GridPane addOrModMenu() {
    getChildren().clear();
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    Text mainIntroText = new Text("Please Select an Action");
    mainIntroText.getStyleClass().add("menu-intro-text");
    add(mainIntroText, 0, 0, 2, 1);

    setHalignment(mainIntroText, HPos.CENTER);

    // - buttons
    HBox buttonsBox = new HBox();

    // TODO remove functionality: Enter key navigating to 'Create New Account'
    // new account menu
    Button createAccountBtn = new Button("Create New Account");

    // modify account menu
    Button modifyAccountBtn = new Button("Modify Existing Account");

    buttonsBox.getChildren().addAll(createAccountBtn, modifyAccountBtn);
    buttonsBox.setSpacing(10);
    buttonsBox.setAlignment(Pos.CENTER);
    HBox.setHgrow(createAccountBtn, Priority.ALWAYS);
    HBox.setHgrow(modifyAccountBtn, Priority.ALWAYS);

    // - set actions
    createAccountBtn.setOnAction(e -> emailApp.showCreateAccountMenu());
    modifyAccountBtn.setOnAction(e -> emailApp.showModifyAccountMenu());

    add(buttonsBox, 0, 2, 2, 1);

    return this;
  }
  // ~ UTILITY

  // • login
  private void performLogin() {
    String userInput = loginUsername.getText();
    String passwordInput = loginPassword.getText();
    checkCredentials(userInput, passwordInput);
  }

  // • verify password & department
  private void checkCredentials(String userInput, String passwordInput) {
    DatabaseManager.getInstance().connect();
    Pair<String, String> loginCredentials = DatabaseManager.getInstance().getLoginCredentials(userInput);
    if (loginCredentials != null) {
      String hashedOGPassword = loginCredentials.getKey();
      String department = loginCredentials.getValue();

      // if password is correct
      if (BCrypt.checkpw(passwordInput, hashedOGPassword)) {

        // if department is correct
        if ("IT".equals(department)) {
          loginTries = 0;
          emailApp.setIsLoggedIn(true);
          addOrModMenu();
        } else {
          handleAccessError();
        }
        // if password is not correct
      } else {
        handleLoginError();
      }
      // if account does not exist
    } else {
      handleLoginError();
    }
    DatabaseManager.getInstance().disconnect();
  }

  // • alerts
  // incorrect department
  private void handleAccessError() {
    // clear fields
    loginUsername.setText("");
    loginPassword.setText("");

    AlertUtils.showAlert(AlertType.WARNING, "Credential Validation Error", "Access Restricted",
        "Account Management System access is restricted to IT department personnel only. Please contact your IT department for assistance.");
    // terminate upon acknowledgment
    System.exit(0);
  }

  // incorrect password
  private void handleLoginError() {
    loginTries++;
    int remainingTries = 10 - loginTries;

    // clear fields
    loginPassword.setText("");

    // incorrect login error
    AlertUtils.showAlert(AlertType.ERROR, "Credential Validation Error", remainingTries + " attempts remaining.",
        "Incorrect username or password! Please try again.");

    // terminate app upon 10 incorrect username/password entries
    if (loginTries >= 10) {
      AlertUtils.showAlert(AlertType.WARNING, "Over Limit Error", "Temporary Lockout",
          "Maximum attempts reached! Account access temporarily restricted, please contact your IT administrator.");

      // terminate upon acknowledgment
      System.exit(0);
    }
  }

}