package com.careermatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain model representing a career job opening / role.
 * Encapsulates job details, salary expectations, experience requirements, and required skills.
 */
public class Job {
    private int id;
    private int companyId;
    private String companyName;
    private String title;
    private double minSalary;
    private double maxSalary;
    private String experience;
    private String location;
    private String eligibility;
    private List<String> requiredSkills = new ArrayList<>();

    public Job() {}

    public Job(int id, int companyId, String companyName, String title,
               double minSalary, double maxSalary, String experience) {
        this(id, companyId, companyName, title, minSalary, maxSalary, experience, "Multiple Locations", "B.Tech / B.E / MCA / M.Tech");
    }

    public Job(int id, int companyId, String companyName, String title,
               double minSalary, double maxSalary, String experience,
               String location, String eligibility) {
        this.id = id;
        this.companyId = companyId;
        this.companyName = companyName;
        this.title = title;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.experience = experience;
        this.location = location != null ? location : "Flexible";
        this.eligibility = eligibility != null ? eligibility : "Bachelor's Degree in CS / IT / Related";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getMinSalary() { return minSalary; }
    public void setMinSalary(double minSalary) { this.minSalary = minSalary; }

    public double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(double maxSalary) { this.maxSalary = maxSalary; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> skills) { 
        this.requiredSkills = skills != null ? skills : new ArrayList<>(); 
    }

    /**
     * Formats salary range attractively for GUI display.
     */
    public String getSalaryFormatted() {
        if (minSalary <= 0 && maxSalary <= 0) return "Competitive / Not Disclosed";
        return String.format("$%.0fk - $%.0fk / yr", minSalary / 1000.0, maxSalary / 1000.0);
    }

    @Override
    public String toString() {
        return title + " at " + companyName;
    }
}
