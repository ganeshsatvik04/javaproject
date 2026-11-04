# CareerMatch – Academic Java Architecture & Demonstration Documentation
**Smart Career Planning and Job Recommendation System**

---

## Executive Overview
CareerMatch is a production-grade career-tech desktop application developed using **Java 17**, **Java Swing**, and **SQLite JDBC**. The application strictly implements all fundamental Object-Oriented Programming (OOP) principles, Advanced Java concepts (Exception Handling, Multithreading with `SwingWorker`, Collections Framework, Generics, and Java Streams API), pure Swing GUI design with modern SaaS aesthetics, and relational database persistence with complete JDBC CRUD operations.

---

## Table of Contents
1. [Object-Oriented Programming (OOP) Concepts](#1-object-oriented-programming-oop-concepts)
   - [Classes and Objects](#11-classes-and-objects)
   - [Encapsulation](#12-encapsulation)
   - [Inheritance](#13-inheritance)
   - [Polymorphism (Overriding & Overloading)](#14-polymorphism)
   - [Abstraction & Interfaces](#15-abstraction-and-interfaces)
2. [Advanced Java Concepts](#2-advanced-java-concepts)
   - [Exception Handling Architecture](#21-exception-handling-architecture)
   - [Multithreading with SwingWorker](#22-multithreading-with-swingworker)
   - [Collections Framework](#23-collections-framework)
   - [Java Generics & Parameterized Repositories](#24-java-generics)
   - [Java Streams API & Functional Programming](#25-java-streams-api)
3. [Graphical User Interface (Swing SaaS Design)](#3-graphical-user-interface-swing-saas-design)
4. [JDBC Database Connectivity & CRUD Implementation](#4-jdbc-database-connectivity--crud-implementation)
5. [Verification & Integration Test Execution](#5-verification--integration-test-execution)

---

## 1. Object-Oriented Programming (OOP) Concepts

### 1.1 Classes and Objects
Every entity in CareerMatch represents a tangible real-world domain concept modeled as a dedicated Java class. Instances of these classes represent live objects in the application runtime.
- **Domain Models:**
  - `User` (Abstract base domain entity)
  - `JobSeeker` (Subclass representing candidates/students)
  - `Admin` (Subclass representing platform administrators)
  - `Job` (Job role opening)
  - `JobMatch` (Calculated recommendation pair)
  - `Company` (Hiring partner organization)
  - `Skill` (Technical competency with proficiency levels)
  - `LearningResource` (Curated tutorial/course)
  - `ReadinessScore` (Evaluated placement scores)
  - `SkillGapResult` (Analytical gap comparison result)
  - `CompanyComparisonItem` (Aggregated comparative profile)

### 1.2 Encapsulation
Encapsulation is enforced across all domain entities and services:
- Fields are declared `private` to restrict unauthorized direct modification.
- Public getter and setter methods expose controlled access with domain validations.

**Code Example (`Job.java`):**
```java
public class Job {
    private int id;
    private int companyId;
    private String companyName;
    private String title;
    private double minSalary;
    private double maxSalary;
    private String experience;
    private List<String> requiredSkills = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getMinSalary() { return minSalary; }
    public void setMinSalary(double minSalary) { this.minSalary = minSalary; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> skills) {
        this.requiredSkills = skills != null ? skills : new ArrayList<>();
    }
}
```

### 1.3 Inheritance
Inheritance promotes code reuse and models hierarchical "is-a" relationships:
- `abstract class User` serves as the superclass containing common identity fields (`id`, `name`, `email`, `password`, `role`).
- `JobSeeker extends User` specializes candidate fields (`education`, `interests`, `graduationYear`, `experienceLevel`).
- `Admin extends User` specializes administrative capabilities.

**Code Example (`JobSeeker.java` extending `User.java`):**
```java
public class JobSeeker extends User {
    private String education;
    private String interests;
    private String graduationYear;
    private String experienceLevel;

    public JobSeeker(int id, String name, String email, String password,
                     String education, String interests,
                     String graduationYear, String experienceLevel) {
        super(id, name, email, password, "USER");
        this.education = education;
        this.interests = interests;
        this.graduationYear = graduationYear != null ? graduationYear : "2025";
        this.experienceLevel = experienceLevel != null ? experienceLevel : "Fresher / Entry Level";
    }

    @Override
    public String getDashboardName() {
        return "Job Seeker Dashboard";
    }
}
```

### 1.4 Polymorphism
CareerMatch leverages both **Dynamic Polymorphism (Method Overriding)** and **Static Polymorphism (Method Overloading)**.

#### A. Method Overriding (Dynamic Polymorphism)
Subclasses provide custom implementations of abstract or inherited methods. At runtime, Java's dynamic method dispatch resolves the correct method based on the actual object instance:
```java
// Abstract method in User.java
public abstract String getDashboardName();

// Overridden in JobSeeker.java
@Override
public String getDashboardName() {
    return "Job Seeker Dashboard";
}

// Overridden in Admin.java
@Override
public String getDashboardName() {
    return "CareerMatch Administrative Console";
}
```
In `UserDAO.java`, the polymorphic method `loginUser()` queries credentials and returns a polymorphic `User` reference pointing to either `JobSeeker` or `Admin`:
```java
public User loginUser(String email, String password) {
    ...
    if ("ADMIN".equalsIgnoreCase(role)) {
        return new Admin(id, name, email, password);
    } else {
        return new JobSeeker(id, name, email, password, education, interests);
    }
}
```

#### B. Method Overloading (Static Polymorphism)
Methods in services share names with distinct parameter lists:
1. `ReadinessService.java`:
   - `calculateScore(double technical, double dsa, double projects, double interview)`
   - `calculateScore(ReadinessScore score)`
2. `MatchingService.java`:
   - `getRecommendations(int userId)`
   - `getRecommendations(int userId, String keyword, String companyFilter, Double minSalary, Double minMatch)`

### 1.5 Abstraction and Interfaces
Abstraction hides implementation complexities while providing high-level contracts:
- `abstract class User`: Declares abstract behavior `getDashboardName()`.
- `interface DatabaseOperations<T>`: Generic persistence abstraction defining CRUD contracts.
- `interface AuthService`: Business contract for authentication and registration.
- `interface JobMatchingService`: Business contract for recommendation algorithms.

**Code Example (`DatabaseOperations.java`):**
```java
public interface DatabaseOperations<T> {
    boolean insert(T item);
    List<T> getAll();
    boolean update(T item);
    boolean delete(int id);
}
```

---

## 2. Advanced Java Concepts

### 2.1 Exception Handling Architecture
CareerMatch uses custom checked and unchecked exceptions with `try-catch-finally` blocks and multiple catch handlers:
- `ValidationException`: Thrown when input syntax, email format, or password constraints are violated.
- `AuthenticationException`: Thrown when credentials fail verification.
- `DatabaseOperationException`: Unchecked runtime wrapper for low-level JDBC errors.

**Code Example (`AuthServiceImpl.java`):**
```java
public User login(String email, String password) throws AuthenticationException, ValidationException {
    if (email == null || email.trim().isEmpty()) {
        throw new ValidationException("email", "Email address cannot be empty.");
    }
    if (password == null || password.trim().isEmpty()) {
        throw new ValidationException("password", "Password cannot be empty.");
    }
    User user = userDAO.loginUser(email.trim(), password);
    if (user == null) {
        throw new AuthenticationException("Invalid email or password. Please try again.");
    }
    return user;
}
```

### 2.2 Multithreading with `SwingWorker`
Desktop user interfaces freeze if heavy database queries or mathematical calculations run on the main Event Dispatch Thread (EDT). CareerMatch implements multithreading via `SwingWorker<T, V>`:
- `doInBackground()` runs in a background worker thread.
- `done()` executes on the EDT to update GUI components safely.

**Code Example (`JobRecommendationPanel.java`):**
```java
SwingWorker<List<JobMatch>, Void> worker = new SwingWorker<>() {
    @Override
    protected List<JobMatch> doInBackground() {
        // Runs on background thread
        return matchingService.getRecommendations(
                currentUser.getId(), keyword, company, fMinSal, fMinMatch
        );
    }

    @Override
    protected void done() {
        try {
            // Safely update Swing GUI on Event Dispatch Thread (EDT)
            List<JobMatch> matches = get();
            renderJobCards(matches);
        } catch (Exception ex) {
            resultCountLabel.setText("Error loading recommendations: " + ex.getMessage());
        }
    }
};
worker.execute();
```

### 2.3 Collections Framework
The Collections Framework is extensively utilized:
- `ArrayList<T>`: Dynamic arrays for job listings, companies, and learning resources.
- `HashSet<String>`: O(1) set membership operations for candidate skills to eliminate duplicates.
- `LinkedHashMap<String, List<Job>>`: Preserves insertion order when grouping jobs by company.
- `Map<String, String>`: Lookup maps associating skills with proficiency levels.

**Code Example (`SkillService.java`):**
```java
public Set<String> getUserSkills(int userId) {
    List<Skill> list = skillDAO.getUserSkills(userId);
    return list.stream()
            .map(s -> s.getName().trim().toLowerCase())
            .collect(Collectors.toCollection(HashSet::new));
}
```

### 2.4 Java Generics
Generic classes and interfaces allow type-safe, reusable data access repositories:
- `GenericDAO<T>`: Parameterized abstract base class implementing `DatabaseOperations<T>`.
- Parameterized return types such as `Optional<T>` prevent `NullPointerException`.

**Code Example (`GenericDAO.java`):**
```java
public abstract class GenericDAO<T> implements DatabaseOperations<T> {
    @Override public abstract boolean insert(T item);
    @Override public abstract List<T> getAll();
    @Override public abstract boolean update(T item);
    @Override public abstract boolean delete(int id);
    public abstract Optional<T> findById(int id);
    public int count() {
        List<T> list = getAll();
        return list != null ? list.size() : 0;
    }
}
```

### 2.5 Java Streams API & Functional Programming
Streams with lambda expressions and method references process data declaratively:
- **Filtering**: `filter(job -> job.getMaxSalary() >= minSalary)`
- **Mapping**: `map(job -> new JobMatch(job, calculateMatch(...)))`
- **Sorting**: `sorted(Comparator.comparingDouble(JobMatch::getMatchPercentage).reversed())`
- **Aggregation**: `count()`, `average()`, `collect(Collectors.toList())`

**Code Example (`MatchingService.java`):**
```java
public double calculateMatch(Set<String> userSkills, List<String> requiredSkills) {
    if (requiredSkills == null || requiredSkills.isEmpty()) return 100.0;
    if (userSkills == null || userSkills.isEmpty()) return 0.0;

    long matched = requiredSkills.stream()
            .filter(skill -> userSkills.contains(skill.trim().toLowerCase()))
            .count();

    double percent = (matched * 100.0) / requiredSkills.size();
    return Math.round(percent * 10.0) / 10.0;
}
```

---

## 3. Graphical User Interface (Swing SaaS Design)

CareerMatch rejects standard default Swing appearances in favor of a modern SaaS design system:
1. **Design System & Palette (`UITheme.java`):**
   - Primary Dark Navy: `#172554`
   - Secondary Royal Blue: `#2563EB`
   - Accent Teal: `#14B8A6`
   - Background Surface: `#F8FAFC`
   - Card Surface: `#FFFFFF`
   - Typography: Clean Segoe UI font hierarchy.
2. **Custom Reusable Components:**
   - `IconHelper`: Resolution-independent antialiased vector graphics.
   - `ModernCard`: Rounded container cards (`drawRoundRect`) with subtle border.
   - `ModernButton`: Rounded buttons with hover, active, and press visual states.
   - `ModernTextField` & `ModernPasswordField`: Inputs with placeholders and show/hide password toggle.
   - `RadialScoreGauge`: High-DPI 360-degree circular arc meter with central percentage display.
   - `ModernProgressBar`: Sleek rounded progress bar.
   - `ModernTable`: Custom table with 42px row padding and alternating row colors.
3. **Unified Single-Window Navigation (`MainFrame.java`):**
   - Coordinates navigation via `CardLayout` across all 8 modules without creating cluttered windows.
   - Persistent `SidebarPanel` with active pill indicators.
   - `TopNavPanel` with title breadcrumb, global search, and user profile chip.

---

## 4. JDBC Database Connectivity & CRUD Implementation

CareerMatch integrates SQLite via JDBC (`sqlite-jdbc-3.46.1.0`) with connection pooling parameters, WAL concurrency mode, transaction rollbacks, and parameter binding.

### Schema Architecture
| Table | Description |
|---|---|
| `Users` | Candidate and administrator authentication records |
| `Skills` | Master catalog of technical and soft skills |
| `UserSkills` | Many-to-Many mapping with candidate proficiency levels |
| `Companies` | Hiring partner organizations and locations |
| `JobRoles` | Job openings with salary bounds and experience |
| `JobSkills` | Many-to-Many mapping between jobs and required skills |
| `LearningResources` | Curated tutorials, courses, and documentation links |
| `LearningProgress` | Candidate progress tracking per learning resource |
| `ReadinessScores` | Historical placement readiness evaluation scores |

### CRUD Demonstration

#### 1. Create (Insert)
```java
String sql = "INSERT INTO Companies(name, location) VALUES(?, ?)";
try (Connection con = Database.getConnection();
     PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    ps.setString(1, c.getName().trim());
    ps.setString(2, c.getLocation());
    int affected = ps.executeUpdate();
    if (affected > 0) {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) c.setId(rs.getInt(1));
        }
        return true;
    }
}
```

#### 2. Read (Query)
```java
String sql = "SELECT * FROM Users WHERE LOWER(email)=LOWER(?) AND password=?";
try (Connection con = Database.getConnection();
     PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, email.trim());
    ps.setString(2, password);
    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            return new JobSeeker(rs.getInt("id"), rs.getString("name"), ...);
        }
    }
}
```

#### 3. Update
```java
String sql = "UPDATE Users SET name=?, email=?, education=?, interests=? WHERE id=?";
try (Connection con = Database.getConnection();
     PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, user.getName());
    ps.setString(2, user.getEmail());
    ps.setString(3, user.getEducation());
    ps.setString(4, user.getInterests());
    ps.setInt(5, user.getId());
    return ps.executeUpdate() > 0;
}
```

#### 4. Delete with Transaction Rollback
```java
try (Connection con = Database.getConnection()) {
    con.setAutoCommit(false);
    try {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM UserSkills WHERE user_id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM Users WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
        con.commit();
        return true;
    } catch (SQLException ex) {
        con.rollback();
        throw ex;
    }
}
```

---

## 5. Verification & Integration Test Execution

The application includes an automated verification test class: `CareerMatchIntegrationTest.java`.
Execution of the test suite confirms all concepts:
```
=================================================
CareerMatch Comprehensive System Verification
=================================================
✓ Database initialized & seeded successfully.
Testing OOP Inheritance & Polymorphism... PASSED [Polymorphic dispatch verified]
Testing AuthService & Exception Handling... PASSED [Custom exceptions & validation verified]
Testing Generics & GenericDAO CRUD... PASSED [Generics CRUD operations verified]
Testing MatchingService & Java Streams API... PASSED [Stream sorting, filtering & gap analysis verified]
Testing Company Comparison Service... PASSED [Multi-company criteria comparison verified]
Testing ReadinessService & Method Overloading... PASSED [Weighted scoring formula & overloading verified]
Testing LearningService & Roadmap Progress... PASSED [Curated resources & progress tracking verified]
Testing AdminService CRUD & Metrics... PASSED [Administrative platform metrics verified]
=================================================
ALL INTEGRATION TESTS PASSED SUCCESSFULLY! (100%)
=================================================
```

---

## Quick Start & Evaluation Credentials
- **To Launch GUI:**
  - Run `run.ps1` in PowerShell, OR `run.bat` in Command Prompt, OR execute `com.careermatch.Main`.
- **Preloaded Test Accounts:**
  - **Candidate / Job Seeker:** `ravi@gmail.com` / `password123`
  - **Administrator:** `admin@careermatch.com` / `admin123`
  - *(One-click demo buttons are provided on the authentication screen for instant evaluation).*
