package com.careermatch.dao;

import com.careermatch.model.ReadinessScore;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Placement Readiness Scores.
 * Demonstrates:
 * - Generics (extends GenericDAO<ReadinessScore>)
 * - JDBC CRUD and historical tracking
 */
public class ReadinessDAO extends GenericDAO<ReadinessScore> {

    @Override
    public boolean insert(ReadinessScore score) {
        String sql = "INSERT INTO ReadinessScores(user_id, technical, dsa, projects, interview, total) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, score.getUserId());
            ps.setDouble(2, score.getTechnical());
            ps.setDouble(3, score.getDsa());
            ps.setDouble(4, score.getProjects());
            ps.setDouble(5, score.getInterview());
            ps.setDouble(6, score.getTotal());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) score.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Could not save readiness score: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<ReadinessScore> getAll() {
        List<ReadinessScore> list = new ArrayList<>();
        String sql = "SELECT * FROM ReadinessScores ORDER BY id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ReadinessScore(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getDouble("technical"),
                        rs.getDouble("dsa"),
                        rs.getDouble("projects"),
                        rs.getDouble("interview"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Could not load readiness scores: " + e.getMessage());
        }
        return list;
    }

    public Optional<ReadinessScore> getLatestForUser(int userId) {
        String sql = "SELECT * FROM ReadinessScores WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ReadinessScore(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("technical"),
                            rs.getDouble("dsa"),
                            rs.getDouble("projects"),
                            rs.getDouble("interview"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not load latest readiness score: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReadinessScore> findById(int id) {
        String sql = "SELECT * FROM ReadinessScores WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ReadinessScore(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDouble("technical"),
                            rs.getDouble("dsa"),
                            rs.getDouble("projects"),
                            rs.getDouble("interview"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Find score error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(ReadinessScore score) {
        String sql = "UPDATE ReadinessScores SET technical = ?, dsa = ?, projects = ?, interview = ?, total = ? WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, score.getTechnical());
            ps.setDouble(2, score.getDsa());
            ps.setDouble(3, score.getProjects());
            ps.setDouble(4, score.getInterview());
            ps.setDouble(5, score.getTotal());
            ps.setInt(6, score.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update score error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ReadinessScores WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete score error: " + e.getMessage());
            return false;
        }
    }
}
