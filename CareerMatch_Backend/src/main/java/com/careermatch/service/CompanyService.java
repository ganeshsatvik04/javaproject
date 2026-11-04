package com.careermatch.service;

import com.careermatch.dao.CompanyDAO;
import com.careermatch.model.Company;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing companies and hiring partner organizations.
 * Demonstrates Collections and Streams.
 */
public class CompanyService {
    private final CompanyDAO companyDAO;

    public CompanyService() {
        this.companyDAO = new CompanyDAO();
    }

    public CompanyService(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    public boolean addCompany(String name, String location) {
        if (name == null || name.trim().isEmpty()) return false;
        return companyDAO.insert(new Company(0, name.trim(), location != null ? location.trim() : "Global"));
    }

    public List<Company> getAllCompanyObjects() {
        return companyDAO.getAll();
    }

    public List<String> getCompanies() {
        return companyDAO.getAll().stream()
                .map(c -> c.getName() + (c.getLocation() != null && !c.getLocation().isEmpty() ? " - " + c.getLocation() : ""))
                .collect(Collectors.toList());
    }

    public List<String> getCompanyNames() {
        return companyDAO.getAll().stream()
                .map(Company::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean updateCompany(int id, String name, String location) {
        return companyDAO.update(new Company(id, name, location));
    }

    public boolean deleteCompany(int id) {
        return companyDAO.delete(id);
    }
}
