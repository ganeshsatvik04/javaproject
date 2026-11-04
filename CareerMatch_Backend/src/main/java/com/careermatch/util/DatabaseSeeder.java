package com.careermatch.util;

import com.careermatch.dao.*;
import com.careermatch.model.Admin;
import com.careermatch.model.Company;
import com.careermatch.model.JobSeeker;
import com.careermatch.service.ReadinessService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * Automatically seeds initial realistic, high-quality career-tech data
 * if the database is empty upon first startup.
 * Provides ready-to-test credentials:
 * - Job Seeker: ravi@gmail.com / password123
 * - Administrator: admin@careermatch.com / admin123
 */
public class DatabaseSeeder {

    public static void seedIfEmpty() {
        boolean isEmpty = false;
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Companies")) {
            if (rs.next() && rs.getInt(1) == 0) {
                isEmpty = true;
            }
        } catch (Exception e) {
            isEmpty = true;
        }

        if (!isEmpty) {
            return;
        }

        try {
            System.out.println("Initializing CareerMatch demo dataset with industry-grade data...");

            UserDAO userDAO = new UserDAO();
            CompanyDAO companyDAO = new CompanyDAO();
            JobDAO jobDAO = new JobDAO();
            SkillDAO skillDAO = new SkillDAO();
            LearningDAO learningDAO = new LearningDAO();
            ReadinessService readinessService = new ReadinessService();

            // 1. Create Users
            JobSeeker ravi = new JobSeeker(
                    0,
                    "Ravi Kumar",
                    "ravi@gmail.com",
                    "password123",
                    "B.Tech Computer Science (2025)",
                    "Backend Architecture, Cloud Systems & High Performance APIs",
                    "2025",
                    "Entry Level / 1 Year"
            );
            userDAO.insert(ravi);

            JobSeeker priya = new JobSeeker(
                    0,
                    "Priya Sharma",
                    "priya@gmail.com",
                    "password123",
                    "B.Tech Information Technology (2024)",
                    "Full Stack Development, Microservices & Data Engineering",
                    "2024",
                    "Junior Developer"
            );
            userDAO.insert(priya);

            Admin admin = new Admin(
                    0,
                    "CareerMatch Administrator",
                    "admin@careermatch.com",
                    "admin123"
            );
            userDAO.insertAdmin(admin);

            // Fetch created ravi id
            JobSeeker loggedRavi = userDAO.login("ravi@gmail.com", "password123");
            int raviId = loggedRavi != null ? loggedRavi.getId() : 1;

            // 2. Create Companies
            Company google = new Company(0, "Google", "Mountain View, CA & Bangalore, IN");
            companyDAO.insert(google);

            Company microsoft = new Company(0, "Microsoft", "Redmond, WA & Hyderabad, IN");
            companyDAO.insert(microsoft);

            Company amazon = new Company(0, "Amazon", "Seattle, WA & Bangalore, IN");
            companyDAO.insert(amazon);

            Company razorpay = new Company(0, "Razorpay", "Bangalore, India");
            companyDAO.insert(razorpay);

            Company tcs = new Company(0, "TCS Digital", "Mumbai & Bangalore, India");
            companyDAO.insert(tcs);

            Company infosys = new Company(0, "Infosys Special", "Bangalore & Pune, India");
            companyDAO.insert(infosys);

            Company accenture = new Company(0, "Accenture Tech Labs", "Gurgaon & Hyderabad, India");
            companyDAO.insert(accenture);

            // 3. Create Job Roles with Skills
            jobDAO.addJobWithSkills(
                    google.getId(),
                    "Software Engineer - Backend (Java)",
                    135000, 185000, "1 - 3 years",
                    Arrays.asList("Java", "Spring Boot", "Microservices", "SQL", "System Design", "Docker", "Git")
            );

            jobDAO.addJobWithSkills(
                    microsoft.getId(),
                    "Cloud Solutions Engineer (Azure)",
                    125000, 170000, "1 - 3 years",
                    Arrays.asList("Java", "Cloud Computing", "Kubernetes", "SQL", "REST API", "Git")
            );

            jobDAO.addJobWithSkills(
                    amazon.getId(),
                    "Software Development Engineer (SDE I)",
                    130000, 175000, "0 - 2 years",
                    Arrays.asList("Java", "Data Structures", "Algorithms", "AWS", "Object-Oriented Design", "Git")
            );

            jobDAO.addJobWithSkills(
                    razorpay.getId(),
                    "Backend Engineer - Payments Infra",
                    95000, 140000, "1 - 3 years",
                    Arrays.asList("Java", "Spring Boot", "SQL", "Redis", "Kafka", "Microservices")
            );

            jobDAO.addJobWithSkills(
                    tcs.getId(),
                    "Systems Engineer - Digital Core",
                    55000, 80000, "Fresher (0 - 1 year)",
                    Arrays.asList("Java", "SQL", "Object-Oriented Design", "Git", "Problem Solving")
            );

            jobDAO.addJobWithSkills(
                    infosys.getId(),
                    "Associate Specialist Programmer",
                    60000, 85000, "Fresher (0 - 2 years)",
                    Arrays.asList("Java", "Spring Boot", "SQL", "REST API", "Data Structures")
            );

            jobDAO.addJobWithSkills(
                    accenture.getId(),
                    "Full Stack Enterprise Developer",
                    70000, 95000, "1 - 3 years",
                    Arrays.asList("Java", "React", "Spring Boot", "REST API", "Docker", "SQL")
            );

            // 4. Add User Skills for Ravi
            skillDAO.addUserSkill(raviId, "Java", "Advanced");
            skillDAO.addUserSkill(raviId, "SQL", "Intermediate");
            skillDAO.addUserSkill(raviId, "Object-Oriented Design", "Intermediate");
            skillDAO.addUserSkill(raviId, "Git", "Intermediate");
            skillDAO.addUserSkill(raviId, "Data Structures", "Intermediate");
            skillDAO.addUserSkill(raviId, "Spring Boot", "Beginner");
            skillDAO.addUserSkill(raviId, "REST API", "Beginner");

            // 5. Add Learning Resources with real, clickable URLs
            addResource(learningDAO, "Java", "Oracle Official Java SE 17 Developer Tutorials", "https://docs.oracle.com/javase/tutorial/");
            addResource(learningDAO, "Spring Boot", "Spring Boot Official Guides & REST Building", "https://spring.io/guides/quickstart");
            addResource(learningDAO, "Microservices", "Martin Fowler: Microservice Architecture Deep Dive", "https://martinfowler.com/articles/microservices.html");
            addResource(learningDAO, "Docker", "Docker Containerization Essentials & Hands-on Lab", "https://docs.docker.com/get-started/");
            addResource(learningDAO, "System Design", "The System Design Primer (Scalability & Architecture)", "https://github.com/donnemartin/system-design-primer");
            addResource(learningDAO, "Data Structures", "LeetCode Curated Top Interview 150 Coding Problems", "https://leetcode.com/studyplan/top-interview-150/");
            addResource(learningDAO, "Algorithms", "GeeksforGeeks Standard Dynamic Programming & Graph Series", "https://www.geeksforgeeks.org/fundamentals-of-algorithms/");
            addResource(learningDAO, "Kubernetes", "Official Kubernetes Interactive Bootcamp", "https://kubernetes.io/docs/tutorials/kubernetes-basics/");
            addResource(learningDAO, "AWS", "AWS Fundamentals for Backend Cloud Developers", "https://aws.amazon.com/getting-started/");
            addResource(learningDAO, "Redis", "Redis University: In-Memory Caching and Data Structures", "https://redis.io/university/");
            addResource(learningDAO, "Kafka", "Apache Kafka Distributed Event Streaming Quickstart", "https://kafka.apache.org/quickstart");

            // 6. Save Baseline Readiness Score for Ravi
            readinessService.saveScore(raviId, 78.0, 72.0, 80.0, 68.0);

            System.out.println("CareerMatch database successfully initialized with sample data!");

        } catch (Exception e) {
            System.err.println("Seeding warning: " + e.getMessage());
        }
    }

    private static void addResource(LearningDAO dao, String skillName, String title, String url) {
        SkillDAO sdao = new SkillDAO();
        int skillId = 0;
        List<com.careermatch.model.Skill> all = sdao.getAll();
        for (com.careermatch.model.Skill s : all) {
            if (s.getName().equalsIgnoreCase(skillName)) {
                skillId = s.getId();
                break;
            }
        }
        if (skillId == 0) {
            sdao.insert(new com.careermatch.model.Skill(0, skillName));
            for (com.careermatch.model.Skill s : sdao.getAll()) {
                if (s.getName().equalsIgnoreCase(skillName)) {
                    skillId = s.getId();
                    break;
                }
            }
        }
        if (skillId > 0) {
            dao.insert(new com.careermatch.model.LearningResource(0, skillId, skillName, title, url, "Platform", false));
        }
    }
}
