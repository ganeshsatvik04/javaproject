package com.careermatch.model;

/**
 * Domain model representing a learning resource (course, documentation, tutorial, project).
 * Used in Module 6: Personalized Learning Roadmap.
 */
public class LearningResource {
    private int id;
    private int skillId;
    private String skillName;
    private String title;
    private String url;
    private String platform;
    private boolean completed;

    public LearningResource() {}

    public LearningResource(int id, int skillId, String skillName, String title, String url, String platform, boolean completed) {
        this.id = id;
        this.skillId = skillId;
        this.skillName = skillName;
        this.title = title;
        this.url = url;
        this.platform = platform != null ? platform : "Online Course";
        this.completed = completed;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }

    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return title + " [" + platform + "]";
    }
}
