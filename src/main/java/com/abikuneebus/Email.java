package com.abikuneebus;

import java.util.Optional;
import java.util.Random;

import org.mindrot.jbcrypt.BCrypt;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class Email extends GridPane {
  // * declaring class-level variables
  private String firstName;
  private String lastName;
  private String fullName;
  private String hashedPassword;
  private String department;
  private int mailboxCapacity = 1500;
  private String emailAddress;
  private int defaultPasswordLength = 16;
  private String companyName = "thesoftwarefarm";
  private String employeeDomain;
  private String employeeUsername;

  public Email(String firstName, String lastName, String department) {
    getChildren().clear();
    setPrefSize(400, 300);
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));
    // initializing class-level variables
    this.department = department;

    // initializing first name
    if (!(isNameValid(firstName) == null)) {
      showNameErrorDialog(isNameValid(firstName));
    } else {
      this.firstName = firstName;
    }

    // initializing last name
    if (!(isNameValid(lastName) == null)) {
      showNameErrorDialog(isNameValid(lastName));
    } else {
      this.lastName = lastName;
    }

    // - initializing full name
    this.fullName = this.firstName + " " + this.lastName;

    // - generating random password
    this.hashedPassword = generatePassword(defaultPasswordLength);

    // - forbidden password substrings
    Constants.FORBIDDEN_SUBSTRINGS.clear();
    Constants.initializeForbiddenSubstrings(this.firstName, this.lastName, this.department, this.companyName);

    // - parameterizing
    String proposedUsername = String.format("%s%s", this.firstName.toLowerCase(),
        capitalizeFirstLetter(this.lastName.toLowerCase()));

    if (isUsernameTaken(proposedUsername)) {
      showUsernameTakenDialog();
    } else {
      this.employeeUsername = proposedUsername;
    }

    // - parameterizing employee domain
    this.employeeDomain = (String.format("@%s%s.com",
        (this.department.equals("N/A") ? "" : String.format("%s.", this.department)), this.companyName))
        .toLowerCase();

    // - combine elements to create email
    this.emailAddress = String.format("%s%s", this.employeeUsername, this.employeeDomain);
  }

  // * initialization utility functions

  private String capitalizeFirstLetter(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }
    return input.substring(0, 1).toUpperCase() + input.substring(1);
  }

  private String generatePassword(int length) {
    Random rnd = new Random();
    char[] password = new char[length];

    boolean genPasswordValid = false;
    while (!genPasswordValid) {
      for (int i = 0; i < length; i++) {
        password[i] = Constants.PASSWORD_CHARS.charAt(rnd.nextInt(Constants.PASSWORD_CHARS.length()));
      }
      String passwordValid = isPasswordValid(password);
      if (passwordValid == null) {
        genPasswordValid = true;
      }
    }

    return BCrypt.hashpw(new String(password), BCrypt.gensalt());
  }

  // * setters & setter utility functions

  public void setMailboxCapacity(int mailboxCapacity) {
    if (mailboxCapacity < 250 || mailboxCapacity > 1500) {
      throw new IllegalArgumentException("Mailbox capacity must be between 250 & 1500.");
    } else {
      this.mailboxCapacity = mailboxCapacity;
    }
  }

  // name validation
  public static String isNameValid(String name) {
    if (!name.matches("(?!.*\\s\\s)^[A-Za-z'\\s-]+$")) {
      return "Name can only contain alphabetical characters, hyphens, apostrophes, and (1) whitespace.";
    }
    return null;
  }

  // password validator
  public String isPasswordValid(char[] password) {
    if (password.length < 12 || password.length > 32) {
      return "Password must be between 12 and 32 characters. ";
    }
    // required characters
    String passwordString = new String(password);
    if (!passwordString.matches(".*[!@#$%^*].*")) {
      return "Password must contain at least one of the following special characters: !, @, #, $, %, ^, or *. ";
    }
    // uppercase
    if (!passwordString.matches(".*[A-Z].*")) {
      return "Password must contain at least one uppercase letter. ";
    }
    // lowercase
    if (!passwordString.matches(".*[a-z].*")) {
      return "Password must contain at least one lowercase letter. ";
    }
    // forbidden substrings (defined in 'Constants')
    for (String substring : Constants.FORBIDDEN_SUBSTRINGS) {
      if (passwordString.contains(substring)) {
        return "Password contains a forbidden group of characters. Please avoid using easily guessable words (\"password\", \"qwerty\", etc.), easily guessable numbers (123, 111, etc.), or any public personal information in your password. ";
      }
    }
    return null;
  }

  public boolean isUsernameTaken(String username) {
    DatabaseManager dbManager = new DatabaseManager();
    dbManager.connect();
    boolean isTaken = dbManager.isUsernameTaken(username);
    dbManager.disconnect();
    return isTaken;
  }

  public String isEmailValid(String username) {
    if (!(username.matches("^(?!.*\\.\\.)(?!.*\\.$)(?!\\.)[a-zA-Z0-9.]{4,32}$"))) {
      return "Invalid email address, please try again using the following guidelines:\n- Between 4 and 32 characters\n- Contains only letters, numbers, and non-consecutive periods\n- Does not start or end with a period\n\nPlease enter a valid email:\n";
    }
    return null;
  }

  private void showNameErrorDialog(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Name Error");
    alert.setHeaderText(null);
    alert.setContentText(errorMessage);
    alert.showAndWait();
  }

  // duplicate username: pop-up dialog
  public void showUsernameTakenDialog() {
    boolean usernameIsValid = false;

    while (!usernameIsValid) {
      // create dialog
      Dialog<String> dialog = new Dialog<>();
      dialog.setTitle("Username Already Taken");
      dialog.setHeaderText(
          "An account with this username already exists. Please enter an alternate username using some combination of your first, middle, and last name or initials.\nExamples: john.bruce.wayne, john.b.wayne, j.b.wayne, etc.");

      // setting close operation
      ButtonType cancelButtonType = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, cancelButtonType);

      // laying out controls
      GridPane popupGrid = new GridPane();
      popupGrid.setAlignment(Pos.CENTER);
      popupGrid.setHgap(10);
      popupGrid.setVgap(10);
      popupGrid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

      TextField altUsernameField = new TextField();
      altUsernameField.setPromptText("Alternate Username");

      popupGrid.add(new Label("Please enter an alternate username:"), 0, 0);
      popupGrid.add(altUsernameField, 1, 0);
      dialog.getDialogPane().setContent(popupGrid);

      // adding validation process to OK button
      Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
      okButton.setDisable(true);

      altUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
        okButton.setDisable(newValue.trim().isEmpty());
      });

      // capturing & handling input
      dialog.setResultConverter(dialogButton -> {
        if (dialogButton == ButtonType.OK) {
          return altUsernameField.getText();
        }
        return null;
      });

      Optional<String> result = dialog.showAndWait();
      if (result.isPresent()) {
        try {
          createAltUsername(result.get());
          usernameIsValid = true;
        } catch (IllegalArgumentException ex) {
          // display error dialog
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText(null);
          alert.setContentText(ex.getMessage());
          alert.showAndWait();
        }
      } else {
        // user chooses to cancel
        usernameIsValid = true;
        // user cancel clears text
        altUsernameField.clear();
      }

    }
  }

  // TODO test alternate username creation
  // create alternate username (if username is taken)
  public void createAltUsername(String username) {
    String emailValid = isEmailValid(username);
    if (emailValid != null) {
      throw new IllegalArgumentException(emailValid); // prints reason for invalidity
    }
    if (isUsernameTaken(username)) {
      throw new IllegalArgumentException("Sorry, this username is already in use.");
    }
    this.employeeUsername = username;
    this.emailAddress = String.format("%s%s", this.employeeUsername, this.employeeDomain); // update email
  }

  // * getters
  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return emailAddress;
  }

  public String getDepartment() {
    return department;
  }

  public int getMailCapacity() {
    return mailboxCapacity;
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  public String getUsername() {
    return employeeUsername;
  }

}