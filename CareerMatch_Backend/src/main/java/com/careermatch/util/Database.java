package com.careermatch.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connectivity, schema table initialization,
 * and automatic sample data seeding.
 */
public class Database {
    private static String getDatabaseUrl() {
        String dbPath = System.getenv("DB_PATH");
        if (dbPath != null && !dbPath.trim().isEmpty()) {
            java.io.File file = new java.io.File(dbPath.trim());
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            return "jdbc:sqlite:" + file.getAbsolutePath();
        }
        return "jdbc:sqlite:careermatch.db";
    }

    public static Connection getConnection() throws SQLException {
        Connection con = DriverManager.getConnection(getDatabaseUrl());
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA busy_timeout = 5000");
        } catch (SQLException ignored) {}
        return con;
    }

    public static void createTables() {
        String[] queries = {
            "CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, email TEXT UNIQUE NOT NULL, password TEXT NOT NULL, role TEXT NOT NULL DEFAULT 'USER', education TEXT, interests TEXT)",
            "CREATE TABLE IF NOT EXISTS Skills (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL)",
            "CREATE TABLE IF NOT EXISTS UserSkills (user_id INTEGER, skill_id INTEGER, level TEXT, PRIMARY KEY(user_id, skill_id), FOREIGN KEY(user_id) REFERENCES Users(id), FOREIGN KEY(skill_id) REFERENCES Skills(id))",
            "CREATE TABLE IF NOT EXISTS Companies (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL, location TEXT)",
            "CREATE TABLE IF NOT EXISTS JobRoles (id INTEGER PRIMARY KEY AUTOINCREMENT, company_id INTEGER, title TEXT NOT NULL, min_salary REAL, max_salary REAL, experience TEXT, FOREIGN KEY(company_id) REFERENCES Companies(id))",
            "CREATE TABLE IF NOT EXISTS JobSkills (job_id INTEGER, skill_id INTEGER, PRIMARY KEY(job_id, skill_id), FOREIGN KEY(job_id) REFERENCES JobRoles(id), FOREIGN KEY(skill_id) REFERENCES Skills(id))",
            "CREATE TABLE IF NOT EXISTS LearningResources (id INTEGER PRIMARY KEY AUTOINCREMENT, skill_id INTEGER, title TEXT, url TEXT, FOREIGN KEY(skill_id) REFERENCES Skills(id))",
            "CREATE TABLE IF NOT EXISTS LearningProgress (user_id INTEGER, resource_id INTEGER, completed INTEGER DEFAULT 0, PRIMARY KEY(user_id, resource_id))",
            "CREATE TABLE IF NOT EXISTS ReadinessScores (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, technical REAL, dsa REAL, projects REAL, interview REAL, total REAL, FOREIGN KEY(user_id) REFERENCES Users(id))"
        };

        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            for (String query : queries) st.execute(query);
        } catch (SQLException e) {
            System.err.println("Could not create database tables: " + e.getMessage());
        }

        // Automatically seed rich demo data if database is fresh
        DatabaseSeeder.seedIfEmpty();
    }
}
