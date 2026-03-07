package com.ctfdensias.service;

import com.ctfdensias.dto.request.LoginRequest;
import com.ctfdensias.dto.request.RegisterRequest;
import com.ctfdensias.dto.response.AuthResponse;
import com.ctfdensias.model.User;

/**
 * AuthService — from UML diagram.
 */
public interface AuthService {
    User register(String username, String email, String password);
    String login(String email, String password);
    void logout(String token);

    // Convenience overloads using DTOs
    User register(RegisterRequest request);
    AuthResponse loginDto(LoginRequest request);
}
