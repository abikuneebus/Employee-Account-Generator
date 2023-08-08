package com.abikuneebus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
  private static final String DATABASE_URL = "C:\\Projects\\Java\\accountgenerator\\accountgen\\accounts.db";

  private Connection connection;

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

  // TODO database operations

  // * insert

  // * update

  // * delete
}