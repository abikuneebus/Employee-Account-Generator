package com.abikuneebus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
  private static final String DATABASE_URL = "jdbc:sqlite:C:\\Projects\\Java\\accountgenerator\\accountgen\\accounts.db";
  private Connection connection;
  private static DatabaseManager instance;

  // TODO refactor all connections to use 'instance'
  // • singleton pattern
  public static DatabaseManager getInstance() {
    if (instance == null) {
      synchronized (DatabaseManager.class) {
        if (instance == null) {
          instance = new DatabaseManager();
        }
      }
    }
    return instance;
  }

  // • create table
  public void createTable() {
    String sql = "CREATE TABLE IF NOT EXISTS email_accounts ("
        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "firstname TEXT NOT NULL,"
        + "lastName TEXT NOT NULL,"
        + "department TEXT NOT NULL,"
        + "email TEXT NOT NULL,"
        + "username TEXT NOT NULL,"
        + "hashedPassword TEXT NOT NULL,"
        + "mailboxCapacity INTEGER NOT NULL);";

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(sql);
      System.out.println("Table created successfully!");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // • establish connection
  public void connect() {
    try {
      // loading SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      // establishing connection to SQLite database
      connection = DriverManager.getConnection(DATABASE_URL);
      System.out.println("Connected to database!");

      // create a table if nonexistent
      createTable();
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  // • close connection
  public void disconnect() {
    try {
      if (connection != null) {
        connection.close();
        System.out.println("Disconnected from database!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // ↓ GENERAL CRUD

  // • adding new accounts
  public void insertEmailAccount(EmailAccount emailAccount) {
    String sql = "INSERT INTO email_accounts(firstName,lastName,department,email,username,hashedPassword, mailboxCapacity) VALUES(?,?,?,?,?,?,?)";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, emailAccount.getFirstName());
      pstmt.setString(2, emailAccount.getLastName());
      pstmt.setString(3, emailAccount.getDepartment());
      pstmt.setString(4, emailAccount.getEmail());
      pstmt.setString(5, emailAccount.getUsername());
      pstmt.setString(6, emailAccount.getHashedPassword());
      pstmt.setInt(7, emailAccount.getMailCapacity());

      // executes prepared statement, inserting data
      pstmt.executeUpdate();

    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // • retrieve data
  public void selectAccount(EmailAccount emailAccount) {
    String sql = "SELECT firstName,lastName,department,email,username,hashedPassword, mailboxCapacity FROM email_accounts WHERE email = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, emailAccount.getEmail()); // WHERE email = ?

      try (ResultSet resultSet = pstmt.executeQuery()) {

        if (resultSet.next()) {
          emailAccount.setFirstName(resultSet.getString("firstName"));
          emailAccount.setLastName(resultSet.getString("lastName"));
          emailAccount.setDepartment(resultSet.getString("department"));
          emailAccount.setUsername(resultSet.getString("username"));
          emailAccount.setPassword(resultSet.getString("hashedPassword"));
          emailAccount.setMailCapacity(resultSet.getInt("mailboxCapacity"));
        } else {
          System.out.println("Account not found.");
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // • modify existing records
  public void updateAccount(EmailAccount updatedAccount) {
    String sql = "UPDATE email_accounts SET firstName = ?, lastName = ?, mailboxCapacity = ? WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, updatedAccount.getFirstName());
      pstmt.setString(2, updatedAccount.getLastName());
      pstmt.setInt(3, updatedAccount.getMailCapacity());
      pstmt.setString(4, updatedAccount.getUsername());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // • remove records
  public boolean deleteAccount(String email) {
    String sql = "DELETE FROM email_accounts WHERE email = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, email);
      int rowsDeleted = pstmt.executeUpdate();

      if (rowsDeleted > 0) {
        System.out.println("Account with email address " + email + " successfully deleted.");
        return true;
      } else {
        System.out.println("Account with email address " + email + " not found.");
        return false;
      }
    } catch (SQLException e) {
      System.out.println("Error deleting account: " + e.getMessage());
      return false;
    }
  }

  // ↓ UTILITY

  // • check if given username already exists
  public boolean isUsernameTaken(String username) {
    String sql = "SELECT COUNT(username) FROM email_accounts WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        int count = rs.getInt(1);
        return count > 0;
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return false;
  }

  // • selecting account by username
  public EmailAccount getAccountByUsername(String usernameInput) {
    String sql = "SELECT * FROM email_accounts WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, usernameInput);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        int mailboxCapacity = rs.getInt("mailboxCapacity");
        String department = rs.getString("department");
        String username = rs.getString("username");
        String hashedPassword = rs.getString("hashedPassword");

        return new EmailAccount(firstName, lastName, email, mailboxCapacity, department, username, hashedPassword);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

  // • check password
  public String getHashedPassword(String username) {
    String sql = "SELECT hashedPassword FROM email_accounts WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, username);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return rs.getString("hashedPassword");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null; // Username not found
  }

  // • updating password
  public void updatePassword(EmailAccount updatedAccount) {
    String sql = "UPDATE email_accounts SET hashedPassword = ? WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, updatedAccount.getHashedPassword());
      pstmt.setString(2, updatedAccount.getUsername());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}