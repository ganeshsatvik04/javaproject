package com.careermatch.service;

import com.careermatch.dao.JobDAO;
import com.careermatch.model.Job;
import com.careermatch.model.JobMatch;
import com.careermatch.model.Skill;
import com.careermatch.model.SkillGapResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing smart job recommendations and skill gap analysis.
 * Demonstrates:
 * - Interface Implementation (JobMatchingService)
 * - Method Overloading (getRecommendations)
 * - Java Streams API (filter, map, sorted, count, collect)
 * - Lambda Expressions and Method References
 * - Collections Framework (HashSet, ArrayList, HashMap)
 */
public class MatchingService implements JobMatchingService {
    private final JobDAO jobDAO;
    private final SkillService skillService;

    public MatchingService() {
        this.jobDAO = new JobDAO();
        this.skillService = new SkillService();
    }

    public MatchingService(JobDAO jobDAO, SkillService skillService) {
        this.jobDAO = jobDAO;
        this.skillService = skillService;
    }

    @Override
    public List<JobMatch> getRecommendations(int userId) {
        Set<String> userSkills = skillService.getUserSkills(userId);
        return jobDAO.getAllJobs().stream()
                .map(job -> new JobMatch(job, calculateMatch(userSkills, job.getRequiredSkills())))
                .sorted(Comparator.comparingDouble(JobMatch::getMatchPercentage).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Overloaded method demonstrating Method Overloading with multi-criteria filtering.
     */
    @Override
    public List<JobMatch> getRecommendations(int userId, String keyword, String companyFilter,
                                             Double minSalary, Double minMatch) {
        Set<String> userSkills = skillService.getUserSkills(userId);
        final String kw = keyword != null ? keyword.trim().toLowerCase() : "";
        final String comp = companyFilter != null ? companyFilter.trim().toLowerCase() : "";

        return jobDAO.getAllJobs().stream()
                // Filter by company if specified
                .filter(job -> comp.isEmpty() || comp.equals("all companies") ||
                               job.getCompanyName().toLowerCase().contains(comp))
                // Filter by keyword in title, company or required skills
                .filter(job -> kw.isEmpty() ||
                               job.getTitle().toLowerCase().contains(kw) ||
                               job.getCompanyName().toLowerCase().contains(kw) ||
                               job.getRequiredSkills().stream().anyMatch(s -> s.toLowerCase().contains(kw)))
                // Filter by minimum salary
                .filter(job -> minSalary == null || job.getMaxSalary() >= minSalary)
                // Map to JobMatch with calculated percentage
                .map(job -> new JobMatch(job, calculateMatch(userSkills, job.getRequiredSkills())))
                // Filter by minimum match percentage
                .filter(match -> minMatch == null || match.getMatchPercentage() >= minMatch)
                // Sort by match percentage descending, then salary descending
                .sorted(Comparator.comparingDouble(JobMatch::getMatchPercentage).reversed()
                        .thenComparing(m -> m.getJob().getMaxSalary(), Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * Helper calculating matching percentage using Java Streams and Lambdas.
     */
    public double calculateMatch(Set<String> userSkills, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 100.0;
        if (userSkills == null || userSkills.isEmpty()) return 0.0;

        long matched = requiredSkills.stream()
                .filter(skill -> userSkills.contains(skill.trim().toLowerCase()))
                .count();

        double percent = (matched * 100.0) / requiredSkills.size();
        return Math.round(percent * 10.0) / 10.0;
    }

    @Override
    public List<String> getSkillGap(int userId, int jobId) {
        Job job = jobDAO.getAllJobs().stream()
                .filter(item -> item.getId() == jobId)
                .findFirst()
                .orElse(null);
        if (job == null) return new ArrayList<>();
        return skillService.getMissingSkills(userId, job.getRequiredSkills());
    }

    @Override
    public SkillGapResult analyzeSkillGap(int userId, int jobId) {
        Job job = jobDAO.findById(jobId).orElseGet(() ->
                jobDAO.getAllJobs().stream()
                        .filter(j -> j.getId() == jobId)
                        .findFirst()
                        .orElse(null)
        );

        if (job == null) return null;

        SkillGapResult result = new SkillGapResult(job);
        List<Skill> userSkillsList = skillService.getUserSkillObjects(userId);

        // Map user skills by lowercase name for fast lookup
        Map<String, String> userSkillLevels = userSkillsList.stream()
                .collect(Collectors.toMap(
                        s -> s.getName().trim().toLowerCase(),
                        Skill::getProficiencyLevel,
                        (existing, replacement) -> existing
                ));

        for (String reqSkill : job.getRequiredSkills()) {
            String lower = reqSkill.trim().toLowerCase();
            if (userSkillLevels.containsKey(lower)) {
                String level = userSkillLevels.get(lower);
                if ("Beginner".equalsIgnoreCase(level)) {
                    // Skill exists but needs improvement for industry expectations
                    result.getSkillsToImprove().add(reqSkill.trim() + " (Current: Beginner)");
                } else {
                    result.getSkillsYouHave().add(reqSkill.trim() + " (" + level + ")");
                }
            } else {
                result.getSkillsYouNeed().add(reqSkill.trim());
            }
        }

        double match = calculateMatch(
                userSkillLevels.keySet(),
                job.getRequiredSkills()
        );
        result.setMatchPercentage(match);

        return result;
    }
}
