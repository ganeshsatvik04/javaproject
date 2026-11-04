package com.careermatch.service;

import com.careermatch.dao.UserDAO;
import com.careermatch.exception.AuthenticationException;
import com.careermatch.exception.ValidationException;
import com.careermatch.model.JobSeeker;
import com.careermatch.model.User;

import java.util.regex.Pattern;

/**
 * Implementation of AuthService.
 * Demonstrates:
 * - Interface Implementation
 * - Exception Handling (custom ValidationException and AuthenticationException)
 * - Input validation with regular expressions
 */
public class AuthServiceImpl implements AuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UserDAO userDAO;

    public AuthServiceImpl() {
        this.userDAO = new UserDAO();
    }

    public AuthServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
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

    @Override
    public JobSeeker register(String name, String email, String password, String confirmPassword,
                              String education, String interests) throws ValidationException {
        if (name == null || name.trim().length() < 2) {
            throw new ValidationException("name", "Full name must be at least 2 characters.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("email", "Please enter a valid email address (e.g., user@example.com).");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("password", "Password must be at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("confirmPassword", "Passwords do not match.");
        }
        if (userDAO.emailExists(email.trim())) {
            throw new ValidationException("email", "An account with this email already exists.");
        }

        JobSeeker seeker = new JobSeeker(
                0,
                name.trim(),
                email.trim().toLowerCase(),
                password,
                education != null && !education.trim().isEmpty() ? education.trim() : "B.Tech / Bachelor's",
                interests != null && !interests.trim().isEmpty() ? interests.trim() : "Software Engineering"
        );

        boolean success = userDAO.insert(seeker);
        if (!success) {
            throw new ValidationException("Registration failed due to a database error. Please retry.");
        }

        // Return user with populated ID
        return userDAO.login(seeker.getEmail(), password);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        if (email == null) return false;
        return userDAO.emailExists(email.trim());
    }
}
