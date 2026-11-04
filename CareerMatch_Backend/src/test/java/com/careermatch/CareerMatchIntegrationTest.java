package com.careermatch;

import com.careermatch.dao.*;
import com.careermatch.exception.AuthenticationException;
import com.careermatch.exception.ValidationException;
import com.careermatch.model.*;
import com.careermatch.service.*;
import com.careermatch.util.Database;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Comprehensive Academic & Functional Test Suite for CareerMatch.
 * Verifies:
 * 1. OOP: Classes, Encapsulation, Inheritance, Polymorphism, Abstraction, Interfaces.
 * 2. Advanced Concepts: Exception Handling, Collections, Generics, Java Streams.
 * 3. JDBC Database Connectivity: CRUD operations on SQLite.
 */
public class CareerMatchIntegrationTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("CareerMatch Comprehensive System Verification");
        System.out.println("=================================================");

        // 1. Initialize Database
        Database.createTables();
        System.out.println("✓ Database initialized & seeded successfully.");

        // 2. Test OOP Inheritance & Polymorphism
        testPolymorphism();

        // 3. Test AuthService & Custom Exception Handling
        testAuthAndExceptions();

        // 4. Test Generics & GenericDAO
        testGenericDAO();

        // 5. Test MatchingService & Java Streams
        testMatchingAndStreams();

        // 6. Test Company Comparison Service
        testCompanyComparison();

        // 7. Test Readiness Analytics Calculation & Overloading
        testReadinessService();

        // 8. Test Learning Roadmap & Completion Progress
        testLearningRoadmap();

        // 9. Test Admin Capabilities
        testAdminService();

        System.out.println("=================================================");
        System.out.println("ALL INTEGRATION TESTS PASSED SUCCESSFULLY! (100%)");
        System.out.println("=================================================");
    }

    private static void testPolymorphism() {
        System.out.print("Testing OOP Inheritance & Polymorphism... ");
        User seeker = new JobSeeker(101, "Test Candidate", "candidate@test.com", "pass123", "B.Tech CSE", "Java & Spring");
        User admin = new Admin(102, "Test Admin", "admin@test.com", "admin123");

        // Polymorphic call to overridden getDashboardName()
        if (!seeker.getDashboardName().contains("Job Seeker")) {
            throw new AssertionError("JobSeeker polymorphic getDashboardName failed");
        }
        if (!admin.getDashboardName().contains("Admin")) {
            throw new AssertionError("Admin polymorphic getDashboardName failed");
        }
        if (!admin.isAdmin() || seeker.isAdmin()) {
            throw new AssertionError("Role check failed");
        }
        System.out.println("PASSED [Polymorphic dispatch verified]");
    }

    private static void testAuthAndExceptions() {
        System.out.print("Testing AuthService & Exception Handling... ");
        AuthService auth = new AuthServiceImpl();

        // Test valid login
        try {
            User user = auth.login("ravi@gmail.com", "password123");
            if (user == null || !user.getEmail().equalsIgnoreCase("ravi@gmail.com")) {
                throw new AssertionError("Ravi login failed");
            }
        } catch (Exception e) {
            throw new AssertionError("Valid login threw unexpected exception: " + e.getMessage());
        }

        // Test invalid login throws AuthenticationException
        boolean caughtAuthEx = false;
        try {
            auth.login("ravi@gmail.com", "wrongpassword");
        } catch (AuthenticationException e) {
            caughtAuthEx = true;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception on invalid credentials: " + e);
        }
        if (!caughtAuthEx) throw new AssertionError("AuthenticationException was not thrown on bad password");

        // Test invalid email throws ValidationException
        boolean caughtValEx = false;
        try {
            auth.register("Name", "bad-email-format", "pass123", "pass123", "B.Tech", "Java");
        } catch (ValidationException e) {
            caughtValEx = true;
        } catch (Exception e) {
            throw new AssertionError("Unexpected exception on bad email: " + e);
        }
        if (!caughtValEx) throw new AssertionError("ValidationException was not thrown on invalid email");

        System.out.println("PASSED [Custom exceptions & validation verified]");
    }

    private static void testGenericDAO() {
        System.out.print("Testing Generics & GenericDAO CRUD... ");
        CompanyDAO companyDAO = new CompanyDAO();
        Company testCo = new Company(0, "Temp Test Co", "Remote Hub");

        // Create
        boolean inserted = companyDAO.insert(testCo);
        if (!inserted || testCo.getId() == 0) throw new AssertionError("CompanyDAO insert failed");

        // Read
        Optional<Company> fetched = companyDAO.findById(testCo.getId());
        if (fetched.isEmpty() || !fetched.get().getName().equals("Temp Test Co")) {
            throw new AssertionError("CompanyDAO findById failed");
        }

        // Update
        testCo.setLocation("Updated Hub");
        companyDAO.update(testCo);
        Optional<Company> updated = companyDAO.findById(testCo.getId());
        if (updated.isEmpty() || !updated.get().getLocation().equals("Updated Hub")) {
            throw new AssertionError("CompanyDAO update failed");
        }

        // Delete
        companyDAO.delete(testCo.getId());
        if (companyDAO.findById(testCo.getId()).isPresent()) {
            throw new AssertionError("CompanyDAO delete failed");
        }

        System.out.println("PASSED [Generics CRUD operations verified]");
    }

    private static void testMatchingAndStreams() {
        System.out.print("Testing MatchingService & Java Streams API... ");
        MatchingService matching = new MatchingService();
        UserDAO userDAO = new UserDAO();
        JobSeeker ravi = userDAO.login("ravi@gmail.com", "password123");
        if (ravi == null) throw new AssertionError("Ravi not found");

        List<JobMatch> recommendations = matching.getRecommendations(ravi.getId());
        if (recommendations == null || recommendations.isEmpty()) {
            throw new AssertionError("Recommendations list is empty");
        }

        // Verify sorted descending by match percentage (Stream sorted)
        for (int i = 0; i < recommendations.size() - 1; i++) {
            if (recommendations.get(i).getMatchPercentage() < recommendations.get(i + 1).getMatchPercentage()) {
                throw new AssertionError("Recommendations not sorted descending by match %");
            }
        }

        // Test Overloaded getRecommendations with filter
        List<JobMatch> filtered = matching.getRecommendations(ravi.getId(), "Java", "All Companies", 60000.0, 50.0);
        if (filtered.isEmpty()) throw new AssertionError("Filtered recommendations is empty");

        // Test Skill Gap Analysis
        SkillGapResult gap = matching.analyzeSkillGap(ravi.getId(), recommendations.get(0).getJob().getId());
        if (gap == null) throw new AssertionError("SkillGapResult is null");

        System.out.println("PASSED [Stream sorting, filtering & gap analysis verified]");
    }

    private static void testCompanyComparison() {
        System.out.print("Testing CompanyComparisonService... ");
        CompanyComparisonService service = new CompanyComparisonService();
        UserDAO userDAO = new UserDAO();
        JobSeeker ravi = userDAO.login("ravi@gmail.com", "password123");

        List<CompanyComparisonItem> items = service.getComparisonProfiles(Arrays.asList("Google", "Microsoft"), ravi.getId());
        if (items.size() < 2) throw new AssertionError("Comparison did not return both companies");

        CompanyComparisonItem google = items.get(0);
        if (google.getMinSalary() <= 0 || google.getRequiredSkills().isEmpty()) {
            throw new AssertionError("Google comparison profile incomplete");
        }
        System.out.println("PASSED [Multi-company criteria comparison verified]");
    }

    private static void testReadinessService() {
        System.out.print("Testing ReadinessService & Method Overloading... ");
        ReadinessService readiness = new ReadinessService();

        // Formula: technical * 0.40 + dsa * 0.25 + projects * 0.20 + interview * 0.15
        // 80*0.4(32) + 80*0.25(20) + 80*0.20(16) + 80*0.15(12) = 80.0
        double score = readiness.calculateScore(80, 80, 80, 80);
        if (Math.abs(score - 80.0) > 0.01) throw new AssertionError("Score calculation incorrect");

        // Overloaded method
        ReadinessScore rScore = new ReadinessScore(1, 1, 90, 70, 80, 60, 0);
        double overloadedScore = readiness.calculateScore(rScore);
        // 90*0.4(36) + 70*0.25(17.5) + 80*0.2(16) + 60*0.15(9) = 78.5
        if (Math.abs(overloadedScore - 78.5) > 0.01) throw new AssertionError("Overloaded score calculation incorrect");

        List<String> strengths = readiness.getStrengths(rScore);
        if (strengths.isEmpty()) throw new AssertionError("Strengths generator failed");

        System.out.println("PASSED [Weighted scoring formula & overloading verified]");
    }

    private static void testLearningRoadmap() {
        System.out.print("Testing LearningService & Roadmap Progress... ");
        LearningService learning = new LearningService();
        UserDAO userDAO = new UserDAO();
        JobSeeker ravi = userDAO.login("ravi@gmail.com", "password123");

        List<LearningResource> resources = learning.getRoadmapResources(Arrays.asList("Docker", "Spring Boot"), ravi.getId());
        if (resources.isEmpty()) throw new AssertionError("No roadmap resources returned for Docker/Spring Boot");

        // Toggle progress
        learning.updateProgress(ravi.getId(), resources.get(0).getId(), true);
        int prog = learning.calculateRoadmapProgress(Arrays.asList("Docker", "Spring Boot"), ravi.getId());
        if (prog <= 0) throw new AssertionError("Roadmap progress calculation failed");

        System.out.println("PASSED [Curated resources & progress tracking verified]");
    }

    private static void testAdminService() {
        System.out.print("Testing AdminService CRUD & Metrics... ");
        AdminService admin = new AdminService();

        int users = admin.getUserCount();
        int comps = admin.getCompanyCount();
        int jobs = admin.getJobCount();
        int skills = admin.getSkillCount();

        if (users <= 0 || comps <= 0 || jobs <= 0 || skills <= 0) {
            throw new AssertionError("Admin metrics count invalid: " + users + ", " + comps + ", " + jobs + ", " + skills);
        }

        System.out.println("PASSED [Administrative platform metrics verified]");
    }
}
