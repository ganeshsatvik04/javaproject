package com.careermatch.model;

/**
 * Represents an administrator in the CareerMatch platform.
 * Extends abstract User class and implements specialized admin capabilities.
 */
public class Admin extends User {

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, "ADMIN");
    }

    @Override
    public String getDashboardName() {
        return "CareerMatch Administrative Console";
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
