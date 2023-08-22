package com.abikuneebus;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

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

  // # confirmation with buttons & show and wait
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

}
