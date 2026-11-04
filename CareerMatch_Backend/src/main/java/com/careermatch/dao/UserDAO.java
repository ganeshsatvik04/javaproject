package com.careermatch.dao;

import com.careermatch.model.Admin;
import com.careermatch.model.JobSeeker;
import com.careermatch.model.User;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User operations (JobSeeker and Admin).
 * Demonstrates:
 * - Generics (extends GenericDAO<JobSeeker>)
 * - Polymorphism (returns concrete JobSeeker or Admin as User)
 * - JDBC CRUD Operations (PreparedStatements, ResultSets, Resource Management)
 * - Exception Handling
 */
public class UserDAO extends GenericDAO<JobSeeker> {

    @Override
    public boolean insert(JobSeeker user) {
        String sql = "INSERT INTO Users(name, email, password, role, education, interests) VALUES(?, ?, ?, 'USER', ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEducation());
            ps.setString(5, user.getInterests());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts an admin user.
     */
    public boolean insertAdmin(Admin admin) {
        String sql = "INSERT INTO Users(name, email, password, role, education, interests) VALUES(?, ?, ?, 'ADMIN', 'Administration', 'System Management')";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Admin registration failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Polymorphic login method that returns User (either JobSeeker or Admin).
     */
    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM Users WHERE LOWER(email)=LOWER(?) AND password=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password")
                        );
                    } else {
                        return new JobSeeker(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("education"),
                                rs.getString("interests")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Legacy login method preserved for backward compatibility.
     */
    public JobSeeker login(String email, String password) {
        User u = loginUser(email, password);
        if (u instanceof JobSeeker) {
            return (JobSeeker) u;
        }
        return null;
    }

    @Override
    public List<JobSeeker> getAll() {
        List<JobSeeker> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role='USER' ORDER BY id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new JobSeeker(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("education"),
                        rs.getString("interests")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Could not load users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Retrieves all users (both JobSeeker and Admin) polymorphically.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY id ASC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String role = rs.getString("role");
                if ("ADMIN".equalsIgnoreCase(role)) {
                    users.add(new Admin(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                    ));
                } else {
                    users.add(new JobSeeker(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("education"),
                            rs.getString("interests")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not load all users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<JobSeeker> findById(int id) {
        String sql = "SELECT * FROM Users WHERE id=? AND role='USER'";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new JobSeeker(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("education"),
                            rs.getString("interests")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find user by ID error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM Users WHERE LOWER(email)=LOWER(?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Email check error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(JobSeeker user) {
        String sql = "UPDATE Users SET name=?, email=?, education=?, interests=? WHERE id=?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getEducation());
            ps.setString(4, user.getInterests());
            ps.setInt(5, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Profile update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Delete child relationships
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM UserSkills WHERE user_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM ReadinessScores WHERE user_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM LearningProgress WHERE user_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Users WHERE id=?")) {
                    ps.setInt(1, id);
                    int affected = ps.executeUpdate();
                    con.commit();
                    return affected > 0;
                }
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("User delete failed: " + e.getMessage());
            return false;
        }
    }
}
