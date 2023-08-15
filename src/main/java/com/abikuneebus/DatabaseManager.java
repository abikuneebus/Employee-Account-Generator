package com.abikuneebus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
  private static final String DATABASE_URL = "C:\\Projects\\Java\\accountgenerator\\accountgen\\accounts.db";

  private Connection connection;

  // * establish connection
  public void connect() {
    try {
      // loading SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      // establishing connection to SQLite database
      connection = DriverManager.getConnection(DATABASE_URL);
      System.out.println("Connected to database!");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  // * close connection
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

  // * test connection
  public static void testDatabaseConnection() {
    String url = "jdbc:sqlite:" + DATABASE_URL;
    try (Connection conn = DriverManager.getConnection(url)) {
      if (conn != null) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT sqlite_version() AS 'SQLite Version';");

        while (rs.next()) {
          System.out.println("SQLite Version: " + rs.getString("SQLite Version"));
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // * GENERAL CRUD

  // * adding new accounts
  public void insertEmailAccount(EmailAccount emailAccount) {
    String sql = "INSERT INTO email_accounts(firstName,lastName,department,email,username,password, mailboxCapacity) VALUES(?,?,?,?,?,?,?)";

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

  // * retrieve data
  public void selectAccount(EmailAccount emailAccount) {
    String sql = "SELECT firstName,lastName,department,email,username,password, mailboxCapacity FROM email_accounts WHERE email = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, emailAccount.getEmail()); // WHERE email = ?

      try (ResultSet resultSet = pstmt.executeQuery()) {

        if (resultSet.next()) {
          emailAccount.setFirstName(resultSet.getString("firstName"));
          emailAccount.setLastName(resultSet.getString("lastName"));
          emailAccount.setDepartment(resultSet.getString("department"));
          emailAccount.setUsername(resultSet.getString("username"));
          emailAccount.setPassword(resultSet.getString("password"));
          emailAccount.setMailCapacity(resultSet.getInt("mailboxCapacity"));
        } else {
          System.out.println("Account not found.");
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  // * modify existing records
  public void updateAccount(EmailAccount emailAccount) {
    String sql = "UPDATE email_accounts SET firstName = ?, lastName = ?, mailCapacity = ? WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, emailAccount.getFirstName());
      pstmt.setString(2, emailAccount.getLastName());
      pstmt.setInt(3, emailAccount.getMailCapacity());
      pstmt.setString(4, emailAccount.getUsername());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // * remove records
  public void deleteAccount(String email) {
    String sql = "DELETE FROM email_accounts WHERE email = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, email);
      int rowsDeleted = pstmt.executeUpdate();

      if (rowsDeleted > 0) {
        System.out.println("Account with email address " + email + " successfully deleted.");
      } else {
        System.out.println("Account with email address " + email + " not found.");
      }
    } catch (SQLException e) {
      System.out.println("Error deleting account: " + e.getMessage());
    }
  }

  // * UTILITY

  // * check if given username already exists
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

  // * selecting account by username
  public EmailAccount getAccountByUsername(String username) {
    String sql = "SELECT * FROM email_accounts WHERE username = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();

      if (rs.next()) {
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String email = rs.getString("email");
        int mailCapacity = rs.getInt("mailCapacity");
        String department = rs.getString("department");
        String hashedPassword = rs.getString("hashedPassword");

        return new EmailAccount(firstName, lastName, email, mailCapacity, department, hashedPassword);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }
}