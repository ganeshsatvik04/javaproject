package com.careermatch.service;

import com.careermatch.dao.ReadinessDAO;
import com.careermatch.model.ReadinessScore;

import java.util.ArrayList;
import java.util.List;

/**
 * Service calculating placement readiness analytics and personalized recommendations.
 * Demonstrates:
 * - Method Overloading (calculateScore)
 * - Exception Handling (IllegalArgumentException)
 * - Algorithmic evaluation of placement factors
 */
public class ReadinessService {
    private final ReadinessDAO readinessDAO;

    public ReadinessService() {
        this.readinessDAO = new ReadinessDAO();
    }

    public ReadinessService(ReadinessDAO readinessDAO) {
        this.readinessDAO = readinessDAO;
    }

    /**
     * Primary formula: Technical (40%), DSA (25%), Projects (20%), Interview (15%).
     */
    public double calculateScore(double technical, double dsa,
                                 double projects, double interview) {
        return (technical * 0.40) + (dsa * 0.25) +
               (projects * 0.20) + (interview * 0.15);
    }

    /**
     * Overloaded method demonstrating Method Overloading.
     */
    public double calculateScore(ReadinessScore score) {
        if (score == null) return 0.0;
        return calculateScore(score.getTechnical(), score.getDsa(), score.getProjects(), score.getInterview());
    }

    public boolean saveScore(int userId, double technical, double dsa,
                             double projects, double interview) {
        if (!valid(technical) || !valid(dsa) || !valid(projects) || !valid(interview))
            throw new IllegalArgumentException("Enter scores between 0 and 100.");

        double total = Math.round(calculateScore(technical, dsa, projects, interview) * 10.0) / 10.0;
        ReadinessScore score = new ReadinessScore(0, userId, technical, dsa, projects, interview, total);
        return readinessDAO.insert(score);
    }

    public ReadinessScore getLatestScore(int userId) {
        return readinessDAO.getLatestForUser(userId).orElseGet(() -> {
            // Default baseline evaluation for first-time profile
            double tech = 75.0, dsa = 70.0, proj = 80.0, inter = 65.0;
            double tot = Math.round(calculateScore(tech, dsa, proj, inter) * 10.0) / 10.0;
            ReadinessScore defaultScore = new ReadinessScore(0, userId, tech, dsa, proj, inter, tot);
            saveScore(userId, tech, dsa, proj, inter);
            return defaultScore;
        });
    }

    public List<String> getStrengths(ReadinessScore score) {
        List<String> strengths = new ArrayList<>();
        if (score.getTechnical() >= 75) strengths.add("Strong technical foundation and syntax competency");
        if (score.getDsa() >= 75) strengths.add("Solid grasp of algorithmic problem solving and time complexities");
        if (score.getProjects() >= 75) strengths.add("Practical project portfolio showing real-world development");
        if (score.getInterview() >= 75) strengths.add("Confident communication and behavioral articulation");
        if (strengths.isEmpty()) strengths.add("Consistent progress and proactive learning orientation");
        return strengths;
    }

    public List<String> getAreasForImprovement(ReadinessScore score) {
        List<String> areas = new ArrayList<>();
        if (score.getTechnical() < 75) areas.add("Modern enterprise backend frameworks (Spring Boot, Microservices)");
        if (score.getDsa() < 75) areas.add("Dynamic Programming, Graph algorithms, and LeetCode Mediums");
        if (score.getProjects() < 75) areas.add("Deploy end-to-end full-stack projects on Cloud (AWS/Docker)");
        if (score.getInterview() < 75) areas.add("Mock HR and Technical System Design interview simulations");
        if (areas.isEmpty()) areas.add("Advanced High-Scale System Design and Distributed Systems");
        return areas;
    }

    public List<String> getRecommendations(ReadinessScore score) {
        List<String> recs = new ArrayList<>();
        if (score.getDsa() < 80) recs.add("Solve 2 LeetCode Medium problems daily focusing on Trees & Graphs");
        if (score.getTechnical() < 80) recs.add("Complete hands-on Spring Boot REST API + JPA projects");
        if (score.getProjects() < 80) recs.add("Add CI/CD pipelines, Docker containerization, and unit tests to GitHub");
        recs.add("Schedule 2 peer mock interviews weekly on Pramp or Interviewing.io");
        return recs;
    }

    private boolean valid(double score) {
        return score >= 0 && score <= 100;
    }
}
