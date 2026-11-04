package com.careermatch.service;

import com.careermatch.dao.JobDAO;
import com.careermatch.model.CompanyComparisonItem;
import com.careermatch.model.Job;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing side-by-side comparative analysis of target companies.
 * Demonstrates:
 * - Java Streams API (groupingBy, mapping, reducing, filtering)
 * - Collections Framework (LinkedHashMap, HashSet)
 */
public class CompanyComparisonService {
    private final JobDAO jobDAO;
    private final SkillService skillService;
    private final MatchingService matchingService;

    public CompanyComparisonService() {
        this.jobDAO = new JobDAO();
        this.skillService = new SkillService();
        this.matchingService = new MatchingService();
    }

    public CompanyComparisonService(JobDAO jobDAO, SkillService skillService, MatchingService matchingService) {
        this.jobDAO = jobDAO;
        this.skillService = skillService;
        this.matchingService = matchingService;
    }

    /**
     * Legacy method preserved for compatibility.
     */
    public List<Job> compareCompanies(List<String> companyNames) {
        if (companyNames == null || companyNames.isEmpty()) return Collections.emptyList();
        return jobDAO.getAllJobs().stream()
                .filter(job -> companyNames.stream().anyMatch(
                        name -> name.equalsIgnoreCase(job.getCompanyName())))
                .collect(Collectors.toList());
    }

    /**
     * Builds structured comparison records for side-by-side comparative analysis.
     */
    public List<CompanyComparisonItem> getComparisonProfiles(List<String> companyNames, int userId) {
        if (companyNames == null || companyNames.isEmpty()) return Collections.emptyList();

        Set<String> userSkills = skillService.getUserSkills(userId);
        List<Job> allJobs = jobDAO.getAllJobs();

        // Group jobs by company name
        Map<String, List<Job>> jobsByCompany = allJobs.stream()
                .filter(j -> companyNames.stream().anyMatch(name -> name.equalsIgnoreCase(j.getCompanyName())))
                .collect(Collectors.groupingBy(Job::getCompanyName, LinkedHashMap::new, Collectors.toList()));

        List<CompanyComparisonItem> result = new ArrayList<>();

        for (String cName : companyNames) {
            // Find matched group (case-insensitive)
            Map.Entry<String, List<Job>> entry = jobsByCompany.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(cName))
                    .findFirst()
                    .orElse(null);

            if (entry != null) {
                List<Job> compJobs = entry.getValue();
                CompanyComparisonItem item = new CompanyComparisonItem(entry.getKey());
                item.setOpenPositions(compJobs.size());

                // Aggregate roles
                item.setRoles(compJobs.stream().map(Job::getTitle).distinct().collect(Collectors.toList()));

                // Salaries
                double minSal = compJobs.stream().mapToDouble(Job::getMinSalary).min().orElse(0);
                double maxSal = compJobs.stream().mapToDouble(Job::getMaxSalary).max().orElse(0);
                item.setMinSalary(minSal);
                item.setMaxSalary(maxSal);
                item.setSalaryRange(String.format("$%.0fk - $%.0fk / yr", minSal / 1000.0, maxSal / 1000.0));

                // Distinct required skills
                Set<String> allSkills = new LinkedHashSet<>();
                for (Job j : compJobs) {
                    allSkills.addAll(j.getRequiredSkills());
                }
                item.setRequiredSkills(new ArrayList<>(allSkills));

                // Eligibility and experience
                item.setEligibility("B.Tech / MCA with 60%+ aggregate");
                String exp = compJobs.stream()
                        .map(Job::getExperience)
                        .distinct()
                        .collect(Collectors.joining(", "));
                item.setExperience(exp.isEmpty() ? "0 - 3 years" : exp);

                // Match percentage: average match of available jobs
                double avgMatch = compJobs.stream()
                        .mapToDouble(j -> matchingService.calculateMatch(userSkills, j.getRequiredSkills()))
                        .average()
                        .orElse(0.0);
                item.setMatchPercentage(Math.round(avgMatch * 10.0) / 10.0);

                result.add(item);
            }
        }

        return result;
    }
}
