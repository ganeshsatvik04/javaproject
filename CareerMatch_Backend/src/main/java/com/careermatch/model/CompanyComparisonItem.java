package com.careermatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model representing an aggregated comparative profile for a company.
 * Used in Module 4: Company Comparison.
 */
public class CompanyComparisonItem {
    private String companyName;
    private String location;
    private List<String> roles = new ArrayList<>();
    private String salaryRange;
    private double minSalary;
    private double maxSalary;
    private List<String> requiredSkills = new ArrayList<>();
    private String eligibility;
    private String experience;
    private double matchPercentage;
    private int openPositions;

    public CompanyComparisonItem(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public String getSalaryRange() { return salaryRange; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }

    public double getMinSalary() { return minSalary; }
    public void setMinSalary(double minSalary) { this.minSalary = minSalary; }

    public double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(double maxSalary) { this.maxSalary = maxSalary; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }

    public int getOpenPositions() { return openPositions; }
    public void setOpenPositions(int openPositions) { this.openPositions = openPositions; }
}
