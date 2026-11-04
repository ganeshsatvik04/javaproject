package com.careermatch.dao;

import com.careermatch.model.Company;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Company entity CRUD operations.
 * Demonstrates:
 * - Generics (extends GenericDAO<Company>)
 * - JDBC CRUD operations
 */
public class CompanyDAO extends GenericDAO<Company> {

    @Override
    public boolean insert(Company c) {
        String sql = "INSERT INTO Companies(name, location) VALUES(?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName().trim());
            ps.setString(2, c.getLocation() != null ? c.getLocation().trim() : "Global");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) c.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Could not add company: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Company> getAll() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM Companies ORDER BY name ASC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Could not load companies: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<Company> findById(int id) {
        String sql = "SELECT * FROM Companies WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Company(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("location")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find company error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Company> findByName(String name) {
        String sql = "SELECT * FROM Companies WHERE LOWER(name) = LOWER(?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Company(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("location")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find company by name error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Company c) {
        String sql = "UPDATE Companies SET name = ?, location = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName().trim());
            ps.setString(2, c.getLocation());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Company update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                // Remove job skills & job roles for this company first
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM JobSkills WHERE job_id IN (SELECT id FROM JobRoles WHERE company_id = ?)")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JobRoles WHERE company_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Companies WHERE id = ?")) {
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
            System.err.println("Could not delete company: " + e.getMessage());
            return false;
        }
    }
}
