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

  private void performLogin() {
    String userInput = loginUsername.getText();
    String passwordInput = loginPassword.getText();
    checkPassword(userInput, passwordInput);
  }

  private void checkPassword(String userInput, String passwordInput) {
    DatabaseManager.getInstance().connect();
    String hashedOGPassword = DatabaseManager.getInstance().getHashedPassword(userInput);
    if (hashedOGPassword != null && BCrypt.checkpw(passwordInput, hashedOGPassword)) {
      // reset attempt counter
      loginTries = 0;
      // set login flag to true
      emailApp.setIsLoggedIn(true);
      // show main menu
      addOrModMenu();

    } else {
      loginTries++;
      int remainingTries = 10 - loginTries;

      // incorrect login error
      AlertUtils.showAlert(AlertType.ERROR, "Credential Verification Error", remainingTries + " attempts remaining.",
          "Incorrect username or password! Please try again.");

      // terminate app upon 10 incorrect username/password entries
      if (loginTries >= 10) {
        AlertUtils.showAlert(AlertType.WARNING, "Over Limit Error", "Access Restricted",
            "Maximum attempts reached! Account access temporarily restricted, please contact your IT administrator.");

        // terminate upon acknowledgment
        System.exit(0);
      }
    }
    DatabaseManager.getInstance().disconnect();
  }
}