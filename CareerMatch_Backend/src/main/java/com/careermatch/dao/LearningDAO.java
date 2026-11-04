package com.careermatch.dao;

import com.careermatch.model.LearningResource;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for LearningResources and LearningProgress.
 * Demonstrates:
 * - Generics (extends GenericDAO<LearningResource>)
 * - User progress tracking persistence
 */
public class LearningDAO extends GenericDAO<LearningResource> {

    @Override
    public boolean insert(LearningResource r) {
        String sql = "INSERT INTO LearningResources(skill_id, title, url) VALUES(?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getSkillId());
            ps.setString(2, r.getTitle());
            ps.setString(3, r.getUrl());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Could not add resource: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<LearningResource> getAll() {
        List<LearningResource> list = new ArrayList<>();
        String sql = "SELECT lr.*, s.name AS skill_name FROM LearningResources lr " +
                     "JOIN Skills s ON lr.skill_id = s.id ORDER BY s.name ASC, lr.title ASC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new LearningResource(
                        rs.getInt("id"),
                        rs.getInt("skill_id"),
                        rs.getString("skill_name"),
                        rs.getString("title"),
                        rs.getString("url"),
                        derivePlatform(rs.getString("url")),
                        false
                ));
            }
        } catch (SQLException e) {
            System.err.println("Could not load resources: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Optional<LearningResource> findById(int id) {
        String sql = "SELECT lr.*, s.name AS skill_name FROM LearningResources lr " +
                     "JOIN Skills s ON lr.skill_id = s.id WHERE lr.id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new LearningResource(
                            rs.getInt("id"),
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("title"),
                            rs.getString("url"),
                            derivePlatform(rs.getString("url")),
                            false
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find resource error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(LearningResource r) {
        String sql = "UPDATE LearningResources SET skill_id = ?, title = ?, url = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getSkillId());
            ps.setString(2, r.getTitle());
            ps.setString(3, r.getUrl());
            ps.setInt(4, r.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Resource update error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM LearningProgress WHERE resource_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM LearningResources WHERE id = ?")) {
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
            System.err.println("Resource delete error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets learning resources for specific skills, augmented with user's completion status.
     */
    public List<LearningResource> getResourcesForSkillsWithUserStatus(List<String> skillNames, int userId) {
        List<LearningResource> list = new ArrayList<>();
        if (skillNames == null || skillNames.isEmpty()) return list;

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT lr.id, lr.skill_id, s.name AS skill_name, lr.title, lr.url, ");
        sb.append("COALESCE(lp.completed, 0) AS is_completed ");
        sb.append("FROM LearningResources lr ");
        sb.append("JOIN Skills s ON lr.skill_id = s.id ");
        sb.append("LEFT JOIN LearningProgress lp ON lr.id = lp.resource_id AND lp.user_id = ? ");
        sb.append("WHERE LOWER(s.name) IN (");
        for (int i = 0; i < skillNames.size(); i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        sb.append(") ORDER BY s.name ASC, lr.title ASC");

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sb.toString())) {
            ps.setInt(1, userId);
            for (int i = 0; i < skillNames.size(); i++) {
                ps.setString(i + 2, skillNames.get(i).toLowerCase().trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LearningResource(
                            rs.getInt("id"),
                            rs.getInt("skill_id"),
                            rs.getString("skill_name"),
                            rs.getString("title"),
                            rs.getString("url"),
                            derivePlatform(rs.getString("url")),
                            rs.getInt("is_completed") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching roadmap resources: " + e.getMessage());
        }
        return list;
    }

    public boolean updateProgress(int userId, int resourceId, boolean completed) {
        String sql = "INSERT OR REPLACE INTO LearningProgress(user_id, resource_id, completed) VALUES(?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, resourceId);
            ps.setInt(3, completed ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Progress update error: " + e.getMessage());
            return false;
        }
    }

    private String derivePlatform(String url) {
        if (url == null) return "Online Resource";
        String lower = url.toLowerCase();
        if (lower.contains("coursera")) return "Coursera";
        if (lower.contains("udemy")) return "Udemy";
        if (lower.contains("leetcode")) return "LeetCode";
        if (lower.contains("geeksforgeeks") || lower.contains("gfg")) return "GeeksforGeeks";
        if (lower.contains("youtube")) return "YouTube";
        if (lower.contains("docs.oracle") || lower.contains("spring.io") || lower.contains("github")) return "Official Docs";
        if (lower.contains("edx")) return "edX";
        return "Interactive Tutorial";
    }
}
