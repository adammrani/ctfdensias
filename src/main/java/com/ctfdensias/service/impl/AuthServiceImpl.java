package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.LoginRequest;
import com.ctfdensias.dto.request.RegisterRequest;
import com.ctfdensias.dto.response.AuthResponse;
import com.ctfdensias.exception.ConflictException;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.UserRepository;
import com.ctfdensias.security.JwtService;
import com.ctfdensias.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public User register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered: " + email);
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // hashes via BCrypt
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        return register(request.getUsername(), request.getEmail(), request.getPassword());
    }

    @Override
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password));
        return jwtService.generateToken((User) auth.getPrincipal());
    }

    @Override
    public AuthResponse loginDto(LoginRequest request) {
        String token = login(request.getEmail(), request.getPassword());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    @Override
    public void logout(String token) {
        // Stateless JWT: client simply discards the token.
        // To implement token blacklisting, store revoked tokens in a Redis/DB set here.
    }
}
