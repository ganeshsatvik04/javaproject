package com.careermatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the results of a skill gap analysis for a specific job.
 * Used in Module 5: Skill Gap Analysis.
 */
public class SkillGapResult {
    private final Job job;
    private final List<String> skillsYouHave = new ArrayList<>();
    private final List<String> skillsYouNeed = new ArrayList<>();
    private final List<String> skillsToImprove = new ArrayList<>();
    private double matchPercentage;
    private double gapPercentage;

    public SkillGapResult(Job job) {
        this.job = job;
    }

    public Job getJob() { return job; }

    public List<String> getSkillsYouHave() { return skillsYouHave; }
    public List<String> getSkillsYouNeed() { return skillsYouNeed; }
    public List<String> getSkillsToImprove() { return skillsToImprove; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
        this.gapPercentage = Math.max(0, 100.0 - matchPercentage);
    }

    public double getGapPercentage() { return gapPercentage; }
    public void setGapPercentage(double gapPercentage) { this.gapPercentage = gapPercentage; }
}
