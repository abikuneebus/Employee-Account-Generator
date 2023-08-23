package com.abikuneebus;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EmailApp extends Application {
  public static List<EmailAccount> accounts = new ArrayList<>();
  private Stage primaryStage; // reference to primary stage
  boolean isLoggedIn = false;
  private ModifyAccountMenu modifyAccountMenu;
  private Scene updateDeleteScene;
  private Scene passwordChangeScene;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage; // save reference to primary stage
    showStartMenu(); // show the start menu
  }

  public void showStartMenu() {
    if (isLoggedIn) {
      StartMenu startMenu = new StartMenu(this);
      GridPane gridPane = startMenu.addOrModMenu();
      Scene scene = new Scene(gridPane, 550, 350); // # login
      primaryStage.setResizable(false);
      scene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
      primaryStage.setScene(scene);
    } else {
      StartMenu startMenu = new StartMenu(this);
      Scene scene = new Scene(startMenu, 550, 350); // # main
      primaryStage.setResizable(false);
      scene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
      primaryStage.setTitle("Email App");
      primaryStage.setScene(scene);
      primaryStage.show();
    }
  }

  public void showCreateAccountMenu() {
    CreateNewAccountMenu createNewAccountMenu = new CreateNewAccountMenu(this);
    Scene scene = new Scene(createNewAccountMenu, 550, 350); // # create new
    primaryStage.setResizable(false);
    scene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
    primaryStage.setTitle("Create New Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showModifyAccountMenu() {

    ModifyAccountMenu modifyAccountMenu = new ModifyAccountMenu(this);

    Scene scene = new Scene(modifyAccountMenu, 700, 350); // # user search
    primaryStage.setResizable(false);
    scene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
    primaryStage.setTitle("Modify Existing Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showPasswordChangeMenu(PasswordChangeMenu passwordChangeMenu) {
    passwordChangeMenu.clearForm();
    if (passwordChangeScene == null) {
      passwordChangeScene = new Scene(passwordChangeMenu, 550, 350); // # change PW
      passwordChangeScene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
    }
    primaryStage.setResizable(false);
    primaryStage.setTitle("Change Password");
    primaryStage.setScene(passwordChangeScene);
    primaryStage.show();
  }

  public void showUpdateDeleteMenu(EmailAccount account) {
    if (modifyAccountMenu == null) {
      modifyAccountMenu = new ModifyAccountMenu(this);
    }
    modifyAccountMenu.showUpdateDeleteMenu(account);
    if (updateDeleteScene == null) {
      updateDeleteScene = new Scene(modifyAccountMenu, 750, 350); // # update/delete
      updateDeleteScene.getStylesheets().add(getClass().getResource("/styles/stylesheet.css").toExternalForm());
    }
    primaryStage.setResizable(false);
    primaryStage.setTitle("Modify Existing Account");
    primaryStage.setScene(updateDeleteScene);
    primaryStage.show();
  }

  // set login flag
  public void setIsLoggedIn(boolean isLoggedIn) {
    this.isLoggedIn = isLoggedIn;
  }

}