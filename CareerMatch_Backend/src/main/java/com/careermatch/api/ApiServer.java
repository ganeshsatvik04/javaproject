package com.careermatch.api;

import com.careermatch.dao.*;
import com.careermatch.exception.AuthenticationException;
import com.careermatch.exception.ValidationException;
import com.careermatch.model.*;
import com.careermatch.service.*;
import com.careermatch.util.Database;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cloud REST API Server for CareerMatch.
 * Bridges all 8 backend business services with Web Frontends (React / Vite / Railway).
 */
public class ApiServer {

    private static final AuthService authService = new AuthServiceImpl();
    private static final UserDAO userDAO = new UserDAO();
    private static final JobDAO jobDAO = new JobDAO();
    private static final SkillDAO skillDAO = new SkillDAO();
    private static final SkillService skillService = new SkillService();
    private static final MatchingService matchingService = new MatchingService();
    private static final CompanyService companyService = new CompanyService();
    private static final CompanyComparisonService comparisonService = new CompanyComparisonService();
    private static final LearningService learningService = new LearningService();
    private static final ReadinessService readinessService = new ReadinessService();
    private static final AdminService adminService = new AdminService();

    public static void main(String[] args) {
        // Initialize SQLite database schema and demo seed records
        Database.createTables();

        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        Javalin app = Javalin.create(config -> {
            // Enable CORS for web clients
            config.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));

            // Serve built frontend files from classpath /public if present
            try {
                config.staticFiles.add(staticFiles -> {
                    staticFiles.hostedPath = "/";
                    staticFiles.directory = "/public";
                    staticFiles.location = Location.CLASSPATH;
                    staticFiles.precompress = false;
                });
            } catch (Exception ignored) {}
        }).start(port);

        // SPA fallback: serve index.html for all non-API GET requests
        // so React client-side navigation works on page refresh or direct URL access.
        app.get("/*", ctx -> {
            String path = ctx.path();
            if (!path.startsWith("/api")) {
                try {
                    java.io.InputStream indexStream = ApiServer.class
                            .getResourceAsStream("/public/index.html");
                    if (indexStream != null) {
                        ctx.contentType("text/html;charset=utf-8")
                           .result(indexStream);
                    }
                } catch (Exception ignored) {}
            }
        });

        // System Health Check
        app.get("/api/health", ctx -> {
            Map<String, Object> status = new HashMap<>();
            status.put("status", "UP");
            status.put("platform", "CareerMatch Platform");
            status.put("timestamp", System.currentTimeMillis());
            ctx.json(status);
        });

        // -------------------------------------------------------------
        // Module 1: Authentication & User Registration
        // -------------------------------------------------------------
        app.post("/api/auth/login", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String email = body.get("email");
            String password = body.get("password");

            try {
                User user = authService.login(email, password);
                ctx.json(user);
            } catch (AuthenticationException | ValidationException e) {
                ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", e.getMessage()));
            }
        });

        app.post("/api/auth/register", ctx -> {
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String name = body.get("name");
            String email = body.get("email");
            String password = body.get("password");
            String confirmPassword = body.get("confirmPassword");
            String education = body.get("education");
            String interests = body.get("interests");

            try {
                JobSeeker seeker = authService.register(name, email, password, confirmPassword, education, interests);
                ctx.status(HttpStatus.CREATED).json(seeker);
            } catch (ValidationException e) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", e.getMessage()));
            }
        });

        // -------------------------------------------------------------
        // Module 2: User Profile & Skill Management
        // -------------------------------------------------------------
        app.get("/api/users/{id}/skills", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            List<Skill> skills = skillService.getUserSkillObjects(userId);
            ctx.json(skills);
        });

        app.post("/api/users/{id}/skills", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, String> body = ctx.bodyAsClass(Map.class);
            String skillName = body.get("name");
            String level = body.getOrDefault("level", "Intermediate");

            // Correct method: addSkill (not addSkillToUser)
            boolean success = skillService.addSkill(userId, skillName, level);
            if (success) {
                ctx.status(HttpStatus.CREATED).json(Map.of("message", "Skill saved successfully"));
            } else {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Failed to add skill"));
            }
        });

        app.delete("/api/users/{id}/skills/{name}", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            String skillName = ctx.pathParam("name");
            // Correct method: removeSkill (not deleteUserSkill)
            boolean success = skillService.removeSkill(userId, skillName);
            ctx.json(Map.of("success", success));
        });

        // -------------------------------------------------------------
        // Module 3: Smart Job Recommendations
        // -------------------------------------------------------------
        app.get("/api/jobs", ctx -> {
            ctx.json(jobDAO.getAllJobs());
        });

        app.get("/api/users/{id}/recommendations", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            String keyword = ctx.queryParam("keyword");
            String company = ctx.queryParam("company");
            double minSalary = ctx.queryParamAsClass("minSalary", Double.class).getOrDefault(0.0);
            double minMatch = ctx.queryParamAsClass("minMatch", Double.class).getOrDefault(0.0);

            List<JobMatch> recommendations = matchingService.getRecommendations(
                    userId, keyword, company, minSalary, minMatch);
            ctx.json(recommendations);
        });

        // -------------------------------------------------------------
        // Module 4: Company Comparison
        // -------------------------------------------------------------
        app.get("/api/companies", ctx -> {
            // Correct method: getAllCompanyObjects (not getAllCompanies)
            ctx.json(companyService.getAllCompanyObjects());
        });

        app.get("/api/companies/compare", ctx -> {
            String comp1 = ctx.queryParam("comp1");
            String comp2 = ctx.queryParam("comp2");
            int userId = ctx.queryParamAsClass("userId", Integer.class).getOrDefault(1);

            if (comp1 == null || comp2 == null) {
                ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", "Provide comp1 and comp2 query params"));
                return;
            }
            // Correct method: getComparisonProfiles(List<String>, int userId)
            List<CompanyComparisonItem> items = comparisonService.getComparisonProfiles(
                    Arrays.asList(comp1, comp2), userId);
            ctx.json(items);
        });

        // -------------------------------------------------------------
        // Module 5: Skill Gap Analysis
        // -------------------------------------------------------------
        app.get("/api/users/{id}/skill-gap/{jobId}", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            int jobId = Integer.parseInt(ctx.pathParam("jobId"));
            SkillGapResult result = matchingService.analyzeSkillGap(userId, jobId);
            ctx.json(result);
        });

        // -------------------------------------------------------------
        // Module 6: Personalized Learning Roadmap
        // -------------------------------------------------------------
        app.get("/api/users/{id}/roadmap", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            Integer targetJobId = ctx.queryParamAsClass("jobId", Integer.class).getOrDefault(null);

            // Build missing-skills list to drive the roadmap
            List<String> missingSkills;
            if (targetJobId != null) {
                missingSkills = matchingService.getSkillGap(userId, targetJobId);
            } else {
                // No specific job: use all user's missing skills across top jobs
                Set<String> userSkills = skillService.getUserSkills(userId);
                missingSkills = jobDAO.getAllJobs().stream()
                        .flatMap(job -> job.getRequiredSkills().stream())
                        .filter(s -> !userSkills.contains(s.trim().toLowerCase()))
                        .distinct()
                        .collect(Collectors.toList());
            }

            // Correct method: getRoadmapResources(List<String>, int userId)
            List<LearningResource> resources = learningService.getRoadmapResources(missingSkills, userId);
            ctx.json(resources);
        });

        app.post("/api/users/{id}/roadmap/progress", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> body = ctx.bodyAsClass(Map.class);
            int resourceId = (int) body.get("resourceId");
            boolean completed = (boolean) body.get("completed");

            boolean ok = learningService.updateProgress(userId, resourceId, completed);
            ctx.json(Map.of("success", ok));
        });

        // -------------------------------------------------------------
        // Module 7: Placement Readiness Score
        // -------------------------------------------------------------
        app.get("/api/users/{id}/readiness", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            ReadinessScore score = readinessService.getLatestScore(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("score", score);
            response.put("strengths", readinessService.getStrengths(score));
            response.put("areasForImprovement", readinessService.getAreasForImprovement(score));
            response.put("recommendations", readinessService.getRecommendations(score));
            ctx.json(response);
        });

        app.post("/api/users/{id}/readiness", ctx -> {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Number> body = ctx.bodyAsClass(Map.class);
            double technical = body.get("technical").doubleValue();
            double dsa = body.get("dsa").doubleValue();
            double projects = body.get("projects").doubleValue();
            double interview = body.get("interview").doubleValue();

            boolean saved = readinessService.saveScore(userId, technical, dsa, projects, interview);
            ReadinessScore updated = readinessService.getLatestScore(userId);
            ctx.json(Map.of("saved", saved, "score", updated));
        });

        // -------------------------------------------------------------
        // Module 8: Admin Dashboard Console
        // -------------------------------------------------------------
        app.get("/api/admin/metrics", ctx -> {
            Map<String, Object> metrics = new HashMap<>();
            // Correct methods: use adminService counts (userDAO.count() does not exist)
            metrics.put("totalUsers", adminService.getUserCount());
            metrics.put("totalCompanies", adminService.getCompanyCount());
            metrics.put("totalJobs", adminService.getJobCount());
            metrics.put("totalSkills", adminService.getSkillCount());
            ctx.json(metrics);
        });

        app.get("/api/admin/users", ctx -> {
            ctx.json(adminService.viewAllUsers());
        });

        System.out.println("=================================================");
        System.out.println("CareerMatch REST API Server active on port " + port);
        System.out.println("Ready for Railway, Docker & Web Frontend clients!");
        System.out.println("=================================================");
    }
}
