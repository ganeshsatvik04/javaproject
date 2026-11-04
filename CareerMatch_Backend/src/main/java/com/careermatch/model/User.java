package com.careermatch.model;

/**
 * Abstract base class representing an authenticated user in the CareerMatch platform.
 * Demonstrates:
 * - Abstraction (abstract class, abstract method getDashboardName())
 * - Encapsulation (private fields, protected access where applicable)
 * - Polymorphic base type for JobSeeker and Admin
 */
public abstract class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;

    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role.toUpperCase() : "USER";
    }

    /**
     * Abstract method that must be implemented by concrete subclasses.
     * Demonstrates Abstraction and Polymorphism.
     */
    public abstract String getDashboardName();

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role != null ? role.toUpperCase() : "USER"; }

    @Override
    public String toString() {
        return "User[id=" + id + ", name=" + name + ", role=" + role + "]";
    }
}
