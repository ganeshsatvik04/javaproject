package com.careermatch.model;

import java.util.Objects;

/**
 * Domain model representing a technical or professional skill.
 * Encapsulates skill details and proficiency levels.
 */
public class Skill {
    private int id;
    private String name;
    private String category;
    private String proficiencyLevel; // Beginner, Intermediate, Advanced

    public Skill() {}

    public Skill(int id, String name) {
        this(id, name, "General", "Intermediate");
    }

    public Skill(int id, String name, String proficiencyLevel) {
        this(id, name, "Technical", proficiencyLevel);
    }

    public Skill(int id, String name, String category, String proficiencyLevel) {
        this.id = id;
        this.name = name;
        this.category = category != null ? category : "Technical";
        this.proficiencyLevel = proficiencyLevel != null ? proficiencyLevel : "Intermediate";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return Objects.equals(name != null ? name.toLowerCase() : "", 
                              skill.name != null ? skill.name.toLowerCase() : "");
    }

    @Override
    public int hashCode() {
        return Objects.hash(name != null ? name.toLowerCase() : "");
    }

    @Override
    public String toString() {
        return name + (proficiencyLevel != null ? " (" + proficiencyLevel + ")" : "");
    }
}
