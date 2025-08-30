package com.ticketsystem.service;

import com.ticketsystem.dto.AuthResponse;
import com.ticketsystem.dto.LoginRequest;
import com.ticketsystem.dto.RegisterRequest;
import com.ticketsystem.entity.User;
import com.ticketsystem.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            
            // Create additional claims for JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId().toString());
            claims.put("role", user.getRole().name());
            claims.put("fullName", user.getFullName());

            String token = jwtUtil.generateToken(user, claims);

            logger.info("User logged in successfully: {}", user.getEmail());
            return new AuthResponse(token, user);

        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        User user = userService.createUser(
            registerRequest.getEmail(),
            registerRequest.getPassword(),
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            registerRequest.getRole()
        );

        // Create additional claims for JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("role", user.getRole().name());
        claims.put("fullName", user.getFullName());

        String token = jwtUtil.generateToken(user, claims);

        logger.info("User registered successfully: {}", user.getEmail());
        return new AuthResponse(token, user);
    }

    public AuthResponse refreshToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            User user = userService.getUserByEmail(username);

            if (jwtUtil.validateToken(token, user)) {
                // Create additional claims for JWT
                Map<String, Object> claims = new HashMap<>();
                claims.put("userId", user.getId().toString());
                claims.put("role", user.getRole().name());
                claims.put("fullName", user.getFullName());

                String newToken = jwtUtil.generateToken(user, claims);
                
                logger.info("Token refreshed for user: {}", user.getEmail());
                return new AuthResponse(newToken, user);
            } else {
                throw new BadCredentialsException("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new BadCredentialsException("Token refresh failed");
        }
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
