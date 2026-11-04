package com.careermatch.model;

import java.util.Date;

/**
 * Domain model representing a placement readiness evaluation record.
 * Used in Module 7: Placement Readiness Score.
 */
public class ReadinessScore {
    private int id;
    private int userId;
    private double technical;
    private double dsa;
    private double projects;
    private double interview;
    private double total;
    private Date evaluatedAt;

    public ReadinessScore() {}

    public ReadinessScore(int id, int userId, double technical, double dsa,
                          double projects, double interview, double total) {
        this(id, userId, technical, dsa, projects, interview, total, new Date());
    }

    public ReadinessScore(int id, int userId, double technical, double dsa,
                          double projects, double interview, double total, Date evaluatedAt) {
        this.id = id;
        this.userId = userId;
        this.technical = technical;
        this.dsa = dsa;
        this.projects = projects;
        this.interview = interview;
        this.total = total;
        this.evaluatedAt = evaluatedAt != null ? evaluatedAt : new Date();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTechnical() { return technical; }
    public void setTechnical(double technical) { this.technical = technical; }

    public double getDsa() { return dsa; }
    public void setDsa(double dsa) { this.dsa = dsa; }

    public double getProjects() { return projects; }
    public void setProjects(double projects) { this.projects = projects; }

    public double getInterview() { return interview; }
    public void setInterview(double interview) { this.interview = interview; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Date getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(Date evaluatedAt) { this.evaluatedAt = evaluatedAt; }

    /**
     * Determines status descriptor based on total readiness score.
     */
    public String getStatusDescriptor() {
        if (total >= 85) return "Placement Ready - High Probability";
        if (total >= 70) return "Well Prepared - Minor Revision Needed";
        if (total >= 50) return "In Progress - Practice Recommended";
        return "Needs Focused Preparation";
    }
}
