package com.abikuneebus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class StartMenu extends GridPane {

  private EmailApp emailApp; // reference to main app class

  public StartMenu(EmailApp emailApp) {
    this.emailApp = emailApp;
    initUI();
  }

  // TODO Replace Start Menu with Admin Login

  // initializing UI elements for start menu
  private void initUI() {
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));

    // ↓ after username & password
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
