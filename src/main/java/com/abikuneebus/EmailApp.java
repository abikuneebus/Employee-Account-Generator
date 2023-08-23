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
  private boolean isLoggedIn = false;
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
    System.out.println("showStartMenu called. isLoggedIn: " + isLoggedIn); // Debug log

    System.out.println("showStartMenu called."); // Debug log
    if (isLoggedIn) {
      System.out.println("isLoggedIn is true."); // Debug log
      StartMenu startMenu = new StartMenu(this);
      GridPane gridPane = startMenu.addOrModMenu();
      Scene scene = new Scene(gridPane, 750, 375);
      primaryStage.setResizable(false);
      scene.getStylesheets()
          .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
      primaryStage.setScene(scene);
    } else {
      System.out.println("isLoggedIn is false."); // Debug log
      StartMenu startMenu = new StartMenu(this);
      Scene scene = new Scene(startMenu, 750, 375);
      primaryStage.setResizable(false);
      scene.getStylesheets()
          .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
      primaryStage.setTitle("Email App");
      primaryStage.setScene(scene);
      primaryStage.show();
    }
  }

  public void showCreateAccountMenu() {
    CreateNewAccountMenu createNewAccountMenu = new CreateNewAccountMenu(this);
    Scene scene = new Scene(createNewAccountMenu, 750, 375);
    primaryStage.setResizable(false);
    scene.getStylesheets()
        .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
    primaryStage.setTitle("Create New Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showModifyAccountMenu() {
    // creating with null EmailAccount and ModifyAccountMenu initially
    PasswordChangeMenu passwordChangeMenu = new PasswordChangeMenu(this, null);

    ModifyAccountMenu modifyAccountMenu = new ModifyAccountMenu(this, passwordChangeMenu);

    Scene scene = new Scene(modifyAccountMenu, 750, 375);
    primaryStage.setResizable(false);
    scene.getStylesheets()
        .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
    primaryStage.setTitle("Modify Existing Account");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void showPasswordChangeMenu(PasswordChangeMenu passwordChangeMenu) {
    if (passwordChangeScene == null) {
      passwordChangeScene = new Scene(passwordChangeMenu, 750, 375);
      passwordChangeScene.getStylesheets()
          .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
    }
    primaryStage.setResizable(false);
    primaryStage.setTitle("Change Password");
    primaryStage.setScene(passwordChangeScene);
    primaryStage.show();
  }

  public void showUpdateDeleteMenu(EmailAccount account) {
    if (modifyAccountMenu == null) {
      PasswordChangeMenu passwordChangeMenu = new PasswordChangeMenu(null, account);
      modifyAccountMenu = new ModifyAccountMenu(this, passwordChangeMenu);
    }
    modifyAccountMenu.showUpdateDeleteMenu(account);
    if (updateDeleteScene == null) {
      updateDeleteScene = new Scene(modifyAccountMenu, 750, 375);
      updateDeleteScene.getStylesheets()
          .add("file:///C:/Projects/Java/accountgenerator/accountgen/src/main/resources/styles/stylesheet.css");
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