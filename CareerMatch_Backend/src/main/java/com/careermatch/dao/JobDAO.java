package com.careermatch.dao;

import com.careermatch.model.Job;
import com.careermatch.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for JobRoles and JobSkills.
 * Demonstrates:
 * - Generics (extends GenericDAO<Job>)
 * - JDBC PreparedStatements with transactions (commit/rollback)
 * - CRUD operations for Job entity
 */
public class JobDAO extends GenericDAO<Job> {

    @Override
    public boolean insert(Job job) {
        String sql = "INSERT INTO JobRoles(company_id, title, min_salary, max_salary, experience) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            int jobId;
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, job.getCompanyId());
                ps.setString(2, job.getTitle());
                ps.setDouble(3, job.getMinSalary());
                ps.setDouble(4, job.getMaxSalary());
                ps.setString(5, job.getExperience());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        jobId = rs.getInt(1);
                        job.setId(jobId);
                    } else {
                        con.rollback();
                        return false;
                    }
                }
            }

            // Associate skills
            if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
                assignSkillsToJob(con, jobId, job.getRequiredSkills());
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Could not add job: " + e.getMessage());
            return false;
        }
    }

    public boolean addJob(int companyId, String title, double minSalary,
                          double maxSalary, String experience) {
        Job job = new Job(0, companyId, "", title, minSalary, maxSalary, experience);
        return insert(job);
    }

    public boolean addJobWithSkills(int companyId, String title, double minSalary,
                                    double maxSalary, String experience, List<String> skills) {
        Job job = new Job(0, companyId, "", title, minSalary, maxSalary, experience);
        job.setRequiredSkills(skills);
        return insert(job);
    }

    @Override
    public List<Job> getAll() {
        return getAllJobs();
    }

    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.*, c.name AS company_name, c.location AS company_loc FROM JobRoles j " +
                     "JOIN Companies c ON j.company_id = c.id ORDER BY j.id DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Job job = new Job(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("title"),
                        rs.getDouble("min_salary"),
                        rs.getDouble("max_salary"),
                        rs.getString("experience"),
                        rs.getString("company_loc"),
                        "Bachelor's Degree in CS/IT/EC or equivalent"
                );
                job.setRequiredSkills(getSkillsForJob(job.getId(), con));
                jobs.add(job);
            }
        } catch (SQLException e) {
            System.err.println("Could not load jobs: " + e.getMessage());
        }
        return jobs;
    }

    @Override
    public Optional<Job> findById(int id) {
        String sql = "SELECT j.*, c.name AS company_name, c.location AS company_loc FROM JobRoles j " +
                     "JOIN Companies c ON j.company_id = c.id WHERE j.id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Job job = new Job(
                            rs.getInt("id"),
                            rs.getInt("company_id"),
                            rs.getString("company_name"),
                            rs.getString("title"),
                            rs.getDouble("min_salary"),
                            rs.getDouble("max_salary"),
                            rs.getString("experience"),
                            rs.getString("company_loc"),
                            "Bachelor's Degree in CS/IT/EC or equivalent"
                    );
                    job.setRequiredSkills(getSkillsForJob(job.getId(), con));
                    return Optional.of(job);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find job error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Job job) {
        String sql = "UPDATE JobRoles SET company_id=?, title=?, min_salary=?, max_salary=?, experience=? WHERE id=?";
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, job.getCompanyId());
                ps.setString(2, job.getTitle());
                ps.setDouble(3, job.getMinSalary());
                ps.setDouble(4, job.getMaxSalary());
                ps.setString(5, job.getExperience());
                ps.setInt(6, job.getId());
                ps.executeUpdate();
            }

            // Refresh skills
            try (PreparedStatement delPs = con.prepareStatement("DELETE FROM JobSkills WHERE job_id=?")) {
                delPs.setInt(1, job.getId());
                delPs.executeUpdate();
            }

            if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
                assignSkillsToJob(con, job.getId(), job.getRequiredSkills());
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Job update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JobSkills WHERE job_id=?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JobRoles WHERE id=?")) {
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
            System.err.println("Job deletion failed: " + e.getMessage());
            return false;
        }
    }

    public List<String> getSkillsForJob(int jobId) {
        try (Connection con = Database.getConnection()) {
            return getSkillsForJob(jobId, con);
        } catch (SQLException e) {
            System.err.println("Error loading job skills: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String> getSkillsForJob(int jobId, Connection con) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT s.name FROM Skills s JOIN JobSkills js ON s.id = js.skill_id WHERE js.job_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(rs.getString("name"));
                }
            }
        }
        return skills;
    }

    private void assignSkillsToJob(Connection con, int jobId, List<String> skills) throws SQLException {
        String skillSql = "INSERT OR IGNORE INTO Skills(name) VALUES(?)";
        String linkSql = "INSERT OR IGNORE INTO JobSkills(job_id, skill_id) VALUES(?, ?)";

        for (String skillName : skills) {
            if (skillName == null || skillName.trim().isEmpty()) continue;
            String trimmed = skillName.trim();
            // Ensure skill exists
            try (PreparedStatement ps = con.prepareStatement(skillSql)) {
                ps.setString(1, trimmed);
                ps.executeUpdate();
            }
            int skillId = 0;
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM Skills WHERE name = ?")) {
                ps.setString(1, trimmed);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) skillId = rs.getInt(1);
                }
            }
            if (skillId > 0) {
                try (PreparedStatement ps = con.prepareStatement(linkSql)) {
                    ps.setInt(1, jobId);
                    ps.setInt(2, skillId);
                    ps.executeUpdate();
                }
            }
        }
    }
}
