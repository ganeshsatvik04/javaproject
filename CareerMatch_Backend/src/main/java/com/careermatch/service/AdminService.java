package com.careermatch.service;

import com.careermatch.dao.CompanyDAO;
import com.careermatch.dao.JobDAO;
import com.careermatch.dao.LearningDAO;
import com.careermatch.dao.SkillDAO;
import com.careermatch.dao.UserDAO;
import com.careermatch.model.*;

import java.util.List;

/**
 * Service orchestrating administrative capabilities, CRUD operations across entities,
 * and high-level platform analytics.
 * Demonstrates:
 * - Delegation and Facade design patterns
 * - Role-based authorization logic
 */
public class AdminService {
    private final UserDAO userDAO;
    private final JobDAO jobDAO;
    private final CompanyDAO companyDAO;
    private final SkillDAO skillDAO;
    private final LearningDAO learningDAO;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.jobDAO = new JobDAO();
        this.companyDAO = new CompanyDAO();
        this.skillDAO = new SkillDAO();
        this.learningDAO = new LearningDAO();
    }

    public List<JobSeeker> viewUsers() {
        return userDAO.getAll();
    }

    public List<User> viewAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean removeUser(int userId) {
        return userDAO.delete(userId);
    }

    public int getUserCount() {
        return userDAO.getAllUsers().size();
    }

    public int getCompanyCount() {
        return companyDAO.getAll().size();
    }

    public int getJobCount() {
        return jobDAO.getAllJobs().size();
    }

    public int getSkillCount() {
        return skillDAO.getAll().size();
    }

    // Company operations
    public List<Company> viewCompanies() {
        return companyDAO.getAll();
    }

    public boolean addCompany(String name, String location) {
        return companyDAO.insert(new Company(0, name, location));
    }

    public boolean updateCompany(int id, String name, String location) {
        return companyDAO.update(new Company(id, name, location));
    }

    public boolean removeCompany(int companyId) {
        return companyDAO.delete(companyId);
    }

    // Job operations
    public List<Job> viewJobs() {
        return jobDAO.getAllJobs();
    }

    public boolean addJob(int companyId, String title, double minSalary,
                          double maxSalary, String experience) {
        return jobDAO.addJob(companyId, title, minSalary, maxSalary, experience);
    }

    public boolean addJobWithSkills(int companyId, String title, double minSalary,
                                    double maxSalary, String experience, List<String> skills) {
        return jobDAO.addJobWithSkills(companyId, title, minSalary, maxSalary, experience, skills);
    }

    public boolean updateJob(Job job) {
        return jobDAO.update(job);
    }

    public boolean removeJob(int jobId) {
        return jobDAO.delete(jobId);
    }

    // Skill operations
    public List<Skill> viewSkills() {
        return skillDAO.getAll();
    }

    public boolean addSkill(String skillName) {
        return skillDAO.insert(new Skill(0, skillName));
    }

    public boolean removeSkill(int skillId) {
        return skillDAO.delete(skillId);
    }

    // Learning Resource operations
    public List<LearningResource> viewResources() {
        return learningDAO.getAll();
    }

    public boolean addLearningResource(int skillId, String title, String url) {
        return learningDAO.insert(new LearningResource(0, skillId, "", title, url, "Online", false));
    }

    public boolean removeLearningResource(int resourceId) {
        return learningDAO.delete(resourceId);
    }
}
