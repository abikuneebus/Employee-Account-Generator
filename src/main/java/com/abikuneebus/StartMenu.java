package com.abikuneebus;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

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
}
