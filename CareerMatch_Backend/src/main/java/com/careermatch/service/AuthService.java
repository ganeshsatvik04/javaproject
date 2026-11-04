package com.careermatch.service;

import com.careermatch.exception.AuthenticationException;
import com.careermatch.exception.ValidationException;
import com.careermatch.model.JobSeeker;
import com.careermatch.model.User;

/**
 * Service interface for authentication, user registration, and session management.
 * Demonstrates Abstraction and Interface-based design.
 */
public interface AuthService {

    /**
     * Authenticates user against credentials.
     *
     * @param email User email
     * @param password Raw password
     * @return Authenticated User (polymorphic JobSeeker or Admin)
     * @throws AuthenticationException if credentials are invalid
     * @throws ValidationException if input format is invalid
     */
    User login(String email, String password) throws AuthenticationException, ValidationException;

    /**
     * Registers a new JobSeeker account.
     *
     * @param name Full name
     * @param email Valid email
     * @param password Password
     * @param confirmPassword Password confirmation matching password
     * @param education Education level
     * @param interests Career interests
     * @return Newly registered JobSeeker
     * @throws ValidationException if validation rules fail
     */
    JobSeeker register(String name, String email, String password, String confirmPassword,
                       String education, String interests) throws ValidationException;

    /**
     * Validates whether an email is already registered.
     */
    boolean isEmailRegistered(String email);
}
