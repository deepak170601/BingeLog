package com.tracker.UserService.mapper;


import com.tracker.UserService.model.User;
import com.tracker.UserService.user.request.RegisterUserRequest;
import com.tracker.UserService.user.request.UpdateUserRequest;
import com.tracker.UserService.user.response.UserResponse;
import com.tracker.UserService.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    @Autowired
    private JwtUtil jwtUtil;

    public User toEntity(RegisterUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // Hashing is expected in the service layer
        user.setFullName(request.getFullName());
        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setFullName(request.getFullName());user.setEmail(request.getEmail());
        user.setRatings(request.getRatings());
        user.setFavorites(request.getFavorites());
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        String jwt = jwtUtil.generateToken(user.getEmail());


        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRatings(user.getRatings());
        response.setFavorites(user.getFavorites());
        return response;
    }
}
