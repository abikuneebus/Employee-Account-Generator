package com.abikuneebus;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class StartMenu extends GridPane {

  private EmailApp emailApp; // reference to main app class

  public StartMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  // initializing UI elements for start menu
  private void initUI() {

    // new account menu
    Button btnCreateAccount = new Button("Create New Account");
    btnCreateAccount.setOnAction(e -> emailApp.showCreateAccountMenu());

    // modify account menu
    Button btnModifyAccount = new Button("Modify Existing Account");
    btnModifyAccount.setOnAction(e -> emailApp.showModifyAccountMenu());

    // adding buttons to layout
    add(btnCreateAccount, 0, 0);
    add(btnModifyAccount, 0, 1);
  }
}
