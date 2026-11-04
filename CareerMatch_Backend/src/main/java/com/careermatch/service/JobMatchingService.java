package com.careermatch.service;

import com.careermatch.model.JobMatch;
import com.careermatch.model.SkillGapResult;

import java.util.List;

/**
 * Service interface for job recommendation and skill gap analysis.
 * Demonstrates:
 * - Interface-based abstraction
 * - Method Overloading in recommendations calculation
 */
public interface JobMatchingService {

    /**
     * Calculates job recommendations based on user's skills.
     */
    List<JobMatch> getRecommendations(int userId);

    /**
     * Overloaded method with search filters.
     * Demonstrates Method Overloading.
     */
    List<JobMatch> getRecommendations(int userId, String keyword, String companyFilter, Double minSalary, Double minMatch);

    /**
     * Identifies missing skills for a target job.
     */
    List<String> getSkillGap(int userId, int jobId);

    /**
     * Generates a detailed skill gap analysis result.
     */
    SkillGapResult analyzeSkillGap(int userId, int jobId);
}
