package com.abikuneebus;

import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Duration;

public class AlertUtils {
  // private constructor to prevent instantiation
  private AlertUtils() {
    throw new AssertionError();
  }

  // # confirmation
  public static void showConfirmAlert(String alertTitle, String alertHeader, String alertContent) {
    showAlert(AlertType.CONFIRMATION, alertTitle, alertHeader, alertContent);
  }

  // # error
  public static void showErrAlert(String alertTitle, String alertHeader, String alertContent) {
    showAlert(AlertType.ERROR, alertTitle, alertHeader, alertContent);
  }

  // # information
  public static void showInfoAlert(String alertTitle, String alertHeader, String alertContent) {
    showAlert(AlertType.INFORMATION, alertTitle, alertHeader, alertContent);
  }

  // # warning
  public static void showWarnAlert(String alertTitle, String alertHeader, String alertContent) {
    showAlert(AlertType.WARNING, alertTitle, alertHeader, alertContent);
  }

  // # empty field
  public static void showEmptyAlert() {
    showAlert(AlertType.ERROR, "Input Error", "Input Error", "All fields required!");
  }

  // # confirmation—w/ custom buttons and show & wait
  public static Optional<ButtonType> showCustomConfirmAlert(String alertTitle, String alertHeader, String alertContent,
      ButtonType... buttons) {
    Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
    confirmAlert.setTitle(alertTitle);
    confirmAlert.setHeaderText(alertHeader);
    confirmAlert.setContentText(alertContent);

    // custom buttons
    confirmAlert.getButtonTypes().setAll(buttons);

    // set first button as default
    if (buttons.length > 0) {
      Button defaultButton = (Button) confirmAlert.getDialogPane().lookupButton(buttons[0]);
      defaultButton.setDefaultButton(true);
    }

    // show alert, await response
    return confirmAlert.showAndWait();
  }

  // # universal with show and wait
  public static void showAlert(AlertType alertType, String alertTitle, String alertHeader, String alertContent) {
    Alert alert = new Alert(alertType);
    alert.setTitle(alertTitle);
    alert.setHeaderText(alertHeader);
    alert.setContentText(alertContent);
    alert.showAndWait();
  }

  // # shows dialog w/ login crendentials upon startup
  // ! for demo purposes only — this probably isn't the best security practice
  public static void showDemoLoginAlert(String title, String headerText, String contentTextUser,
      String contentTextPassword) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.initModality(Modality.NONE);

    TextField textFieldUser = new TextField(contentTextUser);
    textFieldUser.setEditable(false);
    textFieldUser.setPrefHeight(15);
    textFieldUser.setMaxHeight(15);
    textFieldUser.getStyleClass().add("read-only-field");

    TextField textFieldPassword = new TextField(contentTextPassword);
    textFieldPassword.setEditable(false);
    textFieldPassword.setPrefHeight(15);
    textFieldPassword.setMaxHeight(15);
    textFieldPassword.getStyleClass().add("read-only-field");

    GridPane content = new GridPane();
    content.setHgap(10);
    content.setVgap(10);
    content.setMinWidth(300);
    content.setMaxHeight(100);
    content.setMinHeight(100);

    content.add(new Label("Username:"), 0, 0);
    content.add(textFieldUser, 1, 0);
    content.add(new Label("Password:"), 0, 1);
    content.add(textFieldPassword, 1, 1);

    PauseTransition delay = new PauseTransition(Duration.millis(250));
    delay.setOnFinished(e -> {
      alert.show();
      alert.setX(alert.getX() + 250); // adjusting X position
      alert.setY(alert.getY() + 220); // adjusting Y position
    });
    delay.play();

    alert.getDialogPane().setContent(content);
  }

}