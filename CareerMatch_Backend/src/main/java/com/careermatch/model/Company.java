package com.careermatch.model;

/**
 * Domain model representing a recruiting company.
 * Encapsulates company information such as name, headquarters, industry, and website.
 */
public class Company {
    private int id;
    private String name;
    private String location;
    private String industry;
    private String website;

    public Company() {}

    public Company(int id, String name, String location) {
        this(id, name, location, "Technology", "https://" + name.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com");
    }

    public Company(int id, String name, String location, String industry, String website) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.industry = industry != null ? industry : "Technology";
        this.website = website != null ? website : "https://www.example.com";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    @Override
    public String toString() {
        return name + (location != null && !location.isEmpty() ? " (" + location + ")" : "");
    }
}
