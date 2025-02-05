package com.tracker.UserService.service;


import com.tracker.UserService.user.request.RegisterUserRequest;
import com.tracker.UserService.user.request.UpdateUserRequest;
import com.tracker.UserService.user.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {
    UserResponse register(RegisterUserRequest request);

//    UserResponse login(LoginRequest request);

    UserResponse updateProfile(UUID userId, UpdateUserRequest request);

    UserResponse findUserById(UUID userId);

    boolean doesUserExist(UUID userId);

}
