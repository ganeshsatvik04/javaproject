package com.careermatch.dao;

import com.careermatch.model.Skill;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Skill and UserSkill management.
 * Demonstrates:
 * - Generics (extends GenericDAO<Skill>)
 * - Collections Framework (List, Map, Set)
 * - JDBC Transactions and Batch/Prepared statements
 */
public class SkillDAO extends GenericDAO<Skill> {

    @Override
    public boolean insert(Skill s) {
        String sql = "INSERT OR IGNORE INTO Skills(name) VALUES(?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName().trim());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) s.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Could not add skill: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Skill> getAll() {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM Skills ORDER BY name ASC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Skill(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.err.println("Could not load skills: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<Skill> findById(int id) {
        String sql = "SELECT * FROM Skills WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Skill(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find skill error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Skill s) {
        String sql = "UPDATE Skills SET name = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName().trim());
            ps.setInt(2, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Skill update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM UserSkills WHERE skill_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JobSkills WHERE skill_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM LearningResources WHERE skill_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM Skills WHERE id = ?")) {
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
            System.err.println("Skill deletion failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all skills associated with a specific user, including proficiency level.
     */
    public List<Skill> getUserSkills(int userId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT s.id, s.name, us.level FROM Skills s " +
                     "JOIN UserSkills us ON s.id = us.skill_id WHERE us.user_id = ? ORDER BY s.name ASC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(new Skill(
                            rs.getInt("id"),
                            rs.getString("name"),
                            "Technical",
                            rs.getString("level") != null ? rs.getString("level") : "Intermediate"
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user skills: " + e.getMessage());
        }
        return skills;
    }

    /**
     * Adds or updates a user skill with proficiency level.
     */
    public boolean addUserSkill(int userId, String skillName, String level) {
        String skillSql = "INSERT OR IGNORE INTO Skills(name) VALUES(?)";
        String linkSql = "INSERT OR REPLACE INTO UserSkills(user_id, skill_id, level) VALUES(?, ?, ?)";
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            int skillId = 0;
            try (PreparedStatement ps = con.prepareStatement(skillSql)) {
                ps.setString(1, skillName.trim());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM Skills WHERE LOWER(name) = LOWER(?)")) {
                ps.setString(1, skillName.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) skillId = rs.getInt(1);
                    else {
                        con.rollback();
                        return false;
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(linkSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, skillId);
                ps.setString(3, level != null ? level : "Intermediate");
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not add user skill: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a skill association for a user.
     */
    public boolean removeUserSkill(int userId, String skillName) {
        String sql = "DELETE FROM UserSkills WHERE user_id = ? AND skill_id IN (SELECT id FROM Skills WHERE LOWER(name) = LOWER(?))";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, skillName.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing user skill: " + e.getMessage());
            return false;
        }
    }
}
