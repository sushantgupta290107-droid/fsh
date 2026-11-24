package com.prizz.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Syllabus: Lectures 34-36 - JDBC and Database Programming: Relational concepts, driver, URL, DriverManager, Connection
public class DBConnection {
    // Syllabus: Lectures 34-36 - JDBC URL for SQLite database
    private static final String JDBC_URL = "jdbc:sqlite:prizz.db";
    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static boolean initialized = false;

    static {
        try {
            // Syllabus: Lectures 34-36 - Loading the JDBC Driver
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: SQLite JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    // Syllabus: Lectures 34-36 - DriverManager and Connection creation
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    // Syllabus: Lectures 37-39 - Statement: executeUpdate() for DDL and resource management
    public static synchronized void initializeDatabase() {
        if (initialized) return;

        // Syllabus: Lectures 52-54 & Lectures 37-39 - try-with-resources for exception-safe resource handling
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Syllabus: Lectures 37-39 - executeUpdate() to create tables
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "phone TEXT, " +
                    "role TEXT NOT NULL, " +
                    "extra1 TEXT, " +
                    "extra2 TEXT);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS requests (" +
                    "id TEXT PRIMARY KEY, " +
                    "customer_id TEXT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "description TEXT, " +
                    "category TEXT NOT NULL, " +
                    "budget REAL NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "created_at TEXT NOT NULL);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bids (" +
                    "id TEXT PRIMARY KEY, " +
                    "request_id TEXT NOT NULL, " +
                    "provider_id TEXT NOT NULL, " +
                    "amount REAL NOT NULL, " +
                    "estimated_hours INTEGER NOT NULL, " +
                    "proposal_notes TEXT, " +
                    "timestamp TEXT NOT NULL);");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (" +
                    "id TEXT PRIMARY KEY, " +
                    "request_id TEXT NOT NULL, " +
                    "customer_id TEXT NOT NULL, " +
                    "provider_id TEXT NOT NULL, " +
                    "agreed_amount REAL NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "booked_date TEXT NOT NULL);");

            initialized = true;
            System.out.println("[DB] Database initialized successfully (Tables verified via JDBC).");
        } catch (SQLException e) {
            System.err.println("[DB Error] Failed to initialize database: " + e.getMessage());
        }
    }
}
