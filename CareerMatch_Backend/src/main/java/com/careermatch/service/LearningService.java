package com.careermatch.service;

import com.careermatch.dao.LearningDAO;
import com.careermatch.model.LearningResource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing personalized learning roadmap and resource completion tracking.
 * Demonstrates Collections, Generics, and Streams.
 */
public class LearningService {
    private final LearningDAO learningDAO;

    public LearningService() {
        this.learningDAO = new LearningDAO();
    }

    public LearningService(LearningDAO learningDAO) {
        this.learningDAO = learningDAO;
    }

    public boolean addResource(int skillId, String title, String url) {
        if (title == null || title.trim().isEmpty() || url == null || url.trim().isEmpty()) {
            return false;
        }
        return learningDAO.insert(new LearningResource(0, skillId, "", title.trim(), url.trim(), "Online Course", false));
    }

    public boolean updateProgress(int userId, int resourceId, boolean completed) {
        return learningDAO.updateProgress(userId, resourceId, completed);
    }

    public List<String> getResourcesForSkill(int skillId) {
        return learningDAO.getAll().stream()
                .filter(r -> r.getSkillId() == skillId)
                .map(r -> r.getTitle() + " - " + r.getUrl())
                .collect(Collectors.toList());
    }

    public List<LearningResource> getAllResources() {
        return learningDAO.getAll();
    }

    public List<LearningResource> getRoadmapResources(List<String> missingSkills, int userId) {
        return learningDAO.getResourcesForSkillsWithUserStatus(missingSkills, userId);
    }

    public int calculateRoadmapProgress(List<String> missingSkills, int userId) {
        List<LearningResource> list = getRoadmapResources(missingSkills, userId);
        if (list.isEmpty()) return 100;
        long completed = list.stream().filter(LearningResource::isCompleted).count();
        return (int) Math.round((completed * 100.0) / list.size());
    }

    public boolean deleteResource(int id) {
        return learningDAO.delete(id);
    }
}
