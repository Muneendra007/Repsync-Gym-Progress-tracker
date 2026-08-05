package com.repsync.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton class to manage MySQL database connections.
 * Reads configuration from db.properties file.
 * 
 * Usage:
 *   Connection conn = DatabaseConnection.getInstance().getConnection();
 */
public class DatabaseConnection {

    // Single instance (Singleton Pattern)
    private static DatabaseConnection instance;

    // Database configuration
    private String url;
    private String username;
    private String password;

    /**
     * Private constructor - loads database settings from properties file.
     */
    private DatabaseConnection() {
        loadProperties();
    }

    /**
     * Get the single instance of DatabaseConnection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Load database settings from db.properties file.
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("ERROR: db.properties file not found! Using defaults.");
                this.url = "jdbc:mysql://localhost:3306/RepSync_db?connectTimeout=3000&socketTimeout=6000";
                this.username = "root";
                this.password = "Muni@#$152";
                return;
            }
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load db.properties: " + e.getMessage());
        }
    }

    /**
     * Get a fresh database connection.
     * Always close the connection after use with try-with-resources.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get a connection to MySQL server WITHOUT selecting a database.
     * Used for creating the database on first run.
     */
    public Connection getServerConnection() throws SQLException {
        int prefixLen = "jdbc:mysql://".length();
        int slashIndex = url.indexOf('/', prefixLen);
        String serverUrl = (slashIndex > prefixLen) ? url.substring(0, slashIndex + 1) : "jdbc:mysql://localhost:3306/";
        int queryIndex = url.indexOf('?');
        if (queryIndex != -1 && slashIndex > prefixLen) {
            serverUrl += url.substring(queryIndex);
        }
        return DriverManager.getConnection(serverUrl, username, password);
    }

    /**
     * Test if the database connection works.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }
}
