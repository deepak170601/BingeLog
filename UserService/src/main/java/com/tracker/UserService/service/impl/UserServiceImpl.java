package com.tracker.UserService.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.UserService.exception.UserAlreadyExistsException;
import com.tracker.UserService.exception.UserNotFoundException;
import com.tracker.UserService.mapper.UserMapper;
import com.tracker.UserService.model.User;
import com.tracker.UserService.service.SupabaseService;
import com.tracker.UserService.service.UserService;
import com.tracker.UserService.user.request.RegisterUserRequest;
import com.tracker.UserService.user.request.UpdateUserRequest;
import com.tracker.UserService.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final SupabaseService supabaseService;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists in Supabase
        if (doesUserExistByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Securely encode the password before storing
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Prepare user data for Supabase
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", UUID.randomUUID().toString());
        userData.put("email", request.getEmail());
        userData.put("password", encodedPassword);

        // Save user to Supabase
        JsonNode response = supabaseService.postToSupabase("users", userData);

        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Failed to register user in Supabase");
        }

        log.info("User registered successfully in Supabase");
        return userMapper.toResponse(objectMapper.convertValue(userData, User.class));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        // Fetch user from Supabase
        JsonNode userJson = supabaseService.fetchFromSupabase("users", "id", userId.toString());

        if (userJson == null || userJson.isEmpty()) {
            throw new UserNotFoundException("User not found in Supabase");
        }

        // Convert to User object
        User user = objectMapper.convertValue(userJson.get(0), User.class);

        // Update email only if it's not already taken
        if (!user.getEmail().equals(request.getEmail()) && doesUserExistByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }

        userMapper.updateEntity(user, request);

        // Encode password if it's being updated
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Prepare update data
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("email", user.getEmail());
        updatedData.put("password", user.getPassword());

        // Update user in Supabase
        supabaseService.updateInSupabase("users", "id", userId.toString(), updatedData);

        log.info("User profile updated successfully in Supabase");
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserById(UUID userId) {
        log.info("Retrieving user by ID: {}", userId);

        JsonNode userJson = supabaseService.fetchFromSupabase("users", "id", userId.toString());

        if (userJson == null || userJson.isEmpty()) {
            throw new UserNotFoundException("User not found in Supabase");
        }

        User user = objectMapper.convertValue(userJson.get(0), User.class);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesUserExist(UUID userId) {
        log.info("Checking existence for user ID: {}", userId);
        JsonNode userJson = supabaseService.fetchFromSupabase("users", "id", userId.toString());
        return userJson != null && !userJson.isEmpty();
    }

    private boolean doesUserExistByEmail(String email) {
        JsonNode userJson = supabaseService.fetchFromSupabase("users", "email", email);
        return userJson != null && !userJson.isEmpty();
    }
}
