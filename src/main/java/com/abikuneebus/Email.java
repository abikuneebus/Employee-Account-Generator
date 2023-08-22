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
    // setPrefSize(400, 300);
    // setMinSize(400, 300);
    // setMaxSize(400, 300);
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(10);
    setPadding(new Insets(20, 10, 10, 10));
    this.department = department;

    if (!(isNameValid(firstName) == null)) {
      showNameErrorDialog(isNameValid(firstName));
    } else {
      this.firstName = firstName;
    }

    if (!(isNameValid(lastName) == null)) {
      showNameErrorDialog(isNameValid(lastName));
    } else {
      this.lastName = lastName;
    }

    this.fullName = this.firstName + " " + this.lastName;

    // - generating random password
    this.hashedPassword = generatePassword(defaultPasswordLength);

    // - forbidden password substrings
    Constants.FORBIDDEN_SUBSTRINGS.clear();
    Constants.initializeForbiddenSubstrings(this.firstName, this.lastName, this.department, this.companyName);

    // - proposed username construction
    // removing whitespace from both names
    String firstNameNoWhitespace = this.firstName.replace(" ", "");
    String lastNameNoWhitespace = this.lastName.replace(" ", "");

    String proposedUsername = String.format("%s%s", firstNameNoWhitespace.toLowerCase(),
        capitalizeFirstLetter(lastNameNoWhitespace.toLowerCase()));

    // • proposed username validation
    String validUserTry = isUsernameValid(proposedUsername);

    if (!(validUserTry == null)) {
      // if invalid, launch alternate username dialog
      invalidUsernameDialog(validUserTry);
      // if valid, initialize
    } else {
      this.employeeUsername = proposedUsername;
    }

    // - employee domain
    this.employeeDomain = (String.format("@%s%s.com",
        (this.department.equals("N/A") ? "" : String.format("%s.", this.department)), this.companyName))
        .toLowerCase();

    // - combining username & domain to create email
    this.emailAddress = String.format("%s%s", this.employeeUsername, this.employeeDomain);
  }

  // ↓ initialization utility functions

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

  // ↓ utility functions

  // - mailbox capacity validation
  public static String isMailCapacityValid(int capacity) {
    if (!(capacity <= 2500 && capacity >= 500)) {
      return "Mailbox capacity must be between 500 & 2500 messages.";
    }
    return null;
  }

  // - name validation
  public static String isNameValid(String name) {
    if (!name.matches("(?!.*\\s\\s)^[A-Za-z'\\s-]+$")) {
      return "Name can only contain alphabetical characters, hyphens, apostrophes, and (1) whitespace.";
    }
    return null;
  }

  // - password validator
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

  // - email validation
  public String isEmailValid(String username) {
    if (!(username.matches("^(?!.*\\.\\.)(?!.*\\.$)(?!\\.)[a-zA-Z0-9.]{4,32}$"))) {
      return "Invalid email address, please try again using the following guidelines:\n- Between 4 and 32 characters\n- Contains only letters, numbers, and non-consecutive periods\n- Does not start or end with a period\n\nPlease enter a valid email:\n";
    }
    return null;
  }

  private void showNameErrorDialog(String errorMessage) {
    AlertUtils.showAlert(Alert.AlertType.ERROR, "Validation", null, errorMessage);
  }

  // - duplicate username check
  public boolean isUsernameTaken(String username) {
    DatabaseManager dbManager = DatabaseManager.getInstance();
    dbManager.connect();
    boolean isTaken = dbManager.isUsernameTaken(username);
    dbManager.disconnect();
    return isTaken;
  }

  // - username validation
  public String isUsernameValid(String username) {
    // checking database
    DatabaseManager dbManager = DatabaseManager.getInstance();
    dbManager.connect();
    boolean isDuplicate = dbManager.isUsernameTaken(username);
    dbManager.disconnect();

    // if username has invalid characters, return username guidelines
    if (!(username.matches("^(?!.*[-.]{2,}|[-.].*|.*[-.]$)[a-zA-Z.-]{4,32}$"))) {
      return "Invalid username, please try again using the following guidelines:\n- Between 4 and 32 characters\n- Contains only letters, one hyphen, and one period\n- Hyphen and period cannot be consecutive\n\nPlease enter a valid username:\n";
      // if username is valid, check for duplicates
    } else if (isDuplicate) {
      // if username is duplicate, return explanation & suggestions
      return "An account with this username already exists. Please enter an alternate username using some combination of your first, middle, and last name or initials.\\n"
          + //
          "Examples: john.bruce.wayne, john.b.wayne, j.b.wayne, etc.";
    }
    // if username valid & not a duplicate, return null
    return null;
  }

  // - duplicate username: pop-up
  public void invalidUsernameDialog(String validUserTry) {
    boolean usernameIsValid = false;

    while (!usernameIsValid) {
      // create dialog
      Dialog<String> dialog = new Dialog<>();
      dialog.setTitle("Invalid Username");
      dialog.setHeaderText(
          validUserTry);

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
      altUsernameField.setPromptText("johnBSmith");

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
          // end loop if given username valid
          createAltUsername(result.get());
          usernameIsValid = true;
          // continue loop if given username invalid
        } catch (IllegalArgumentException ex) {
          // display error dialog
          AlertUtils.showAlert(Alert.AlertType.ERROR, "Validation", null, ex.getMessage());
        }
      } else {
        // user chooses to cancel
        usernameIsValid = true;
        // user cancel clears text
        altUsernameField.clear();
      }

    }
  }

  // - create alternate username (if username is taken)
  public void createAltUsername(String username) {

    // validate username
    String usernameValid = isUsernameValid(username);
    if (usernameValid != null) {
      throw new IllegalArgumentException(usernameValid); // prints reason for invalidity
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