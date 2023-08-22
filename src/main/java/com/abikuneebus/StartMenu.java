package com.abikuneebus;

import org.mindrot.jbcrypt.BCrypt;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

public class StartMenu extends GridPane {
  private TextField loginUsername;
  private PasswordField loginPassword;
  private int attempts = 0;

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

    Text loginText = new Text("Please Log In");
    loginText.getStyleClass().add("menu-intro-text");
    add(loginText, 0, 0, 2, 1);

    // - labels
    add(new Label("Username:"), 0, 1);
    add(new Label("Password:"), 0, 2);

    // - text fields
    loginUsername = new TextField();
    add(loginUsername, 1, 1);

    loginPassword = new PasswordField();
    add(loginPassword, 1, 2);

    // - buttons
    Button loginBtn = new Button("Login");
    add(loginBtn, 0, 3, 2, 1);

    // - set action
    loginBtn.setOnAction(e -> {
      String userInput = loginUsername.getText();
      String passwordInput = loginPassword.getText();
      checkPassword(userInput, passwordInput);
    });

  }

  void addOrModMenu() {
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
  }
  // ~ UTILITY

  private void checkPassword(String userInput, String passwordInput) {
    String hashedOGPassword = DatabaseManager.getInstance().getHashedPassword(userInput);
    if (hashedOGPassword != null && BCrypt.checkpw(passwordInput, hashedOGPassword)) {
      attempts = 0;
      // TODO successful login
      addOrModMenu(); // show main menu
    } else {
      attempts++;
      // incorrect login error
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
        // terminate upon acknowledgment
        System.exit(0);
      }
    }
  }

}
