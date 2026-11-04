package com.careermatch.model;

/**
 * Represents a student or job seeker using the CareerMatch platform.
 * Inherits from the abstract User class and demonstrates:
 * - Inheritance (extends User)
 * - Method Overriding (getDashboardName)
 * - Constructor Overloading
 * - Encapsulation (private fields with public getters/setters)
 */
public class JobSeeker extends User {
    private String education;
    private String interests;
    private String graduationYear;
    private String experienceLevel;

    /**
     * Legacy constructor for backward compatibility.
     */
    public JobSeeker(int id, String name, String email, String password,
                     String education, String interests) {
        this(id, name, email, password, education, interests, "2025", "Fresher / Entry Level");
    }

    /**
     * Overloaded full constructor demonstrating Constructor Overloading.
     */
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

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    @Override
    public String toString() {
        return "JobSeeker{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", education='" + education + '\'' +
                ", graduationYear='" + graduationYear + '\'' +
                '}';
    }
}
