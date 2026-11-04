package com.careermatch.service;

import com.careermatch.dao.SkillDAO;
import com.careermatch.model.Skill;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing user skills, proficiency evaluations, and skill inventories.
 * Demonstrates:
 * - Collections Framework (HashSet, ArrayList, Map)
 * - Java Streams API for set difference and filtering
 */
public class SkillService {
    private final SkillDAO skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public SkillService(SkillDAO skillDAO) {
        this.skillDAO = skillDAO;
    }

    public boolean addSkill(int userId, String skillName, String level) {
        if (skillName == null || skillName.trim().isEmpty()) return false;
        return skillDAO.addUserSkill(userId, skillName.trim(), level);
    }

    public boolean removeSkill(int userId, String skillName) {
        if (skillName == null) return false;
        return skillDAO.removeUserSkill(userId, skillName.trim());
    }

    public Set<String> getUserSkills(int userId) {
        List<Skill> list = skillDAO.getUserSkills(userId);
        return list.stream()
                .map(s -> s.getName().trim().toLowerCase())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public List<Skill> getUserSkillObjects(int userId) {
        return skillDAO.getUserSkills(userId);
    }

    public List<String> getMissingSkills(int userId, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return Collections.emptyList();
        Set<String> current = getUserSkills(userId);
        return requiredSkills.stream()
                .filter(skill -> !current.contains(skill.trim().toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<String> getAllSkills() {
        return skillDAO.getAll().stream()
                .map(Skill::getName)
                .sorted()
                .collect(Collectors.toList());
    }
}
