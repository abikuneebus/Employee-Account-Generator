package com.abikuneebus;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EmailApp extends Application {
  public static List<EmailAccount> accounts = new ArrayList<>();
  private Stage primaryStage; // reference to primary stage

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage; // save reference to primary stage
    showStartMenu(); // show the start menu
  }

  public void showStartMenu() {
    StartMenu startMenu = new StartMenu(this);
    Scene scene = new Scene(startMenu, 400, 300);
    primaryStage.setTitle("Email App");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showCreateAccountMenu() {
    CreateNewAccountMenu createNewAccountMenu = new CreateNewAccountMenu(this);
    Scene scene = new Scene(createNewAccountMenu, 400, 300);
    primaryStage.setTitle("Create New Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showModifyAccountMenu() {
    // creating with null EmailAccount and ModifyAccountMenu initially
    PasswordChangeMenu passwordChangeMenu = new PasswordChangeMenu(this, null, null);

    ModifyAccountMenu modifyAccountMenu = new ModifyAccountMenu(this, passwordChangeMenu);

    // setting modifyAccountMenu reference in passwordChangeMenu
    passwordChangeMenu.setModifyAccountMenu(modifyAccountMenu);

    Scene scene = new Scene(modifyAccountMenu, 800, 300);
    primaryStage.setTitle("Modify Existing Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

}