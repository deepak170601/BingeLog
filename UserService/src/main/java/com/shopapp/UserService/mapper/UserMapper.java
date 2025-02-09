package com.shopapp.UserService.mapper;


import com.shopapp.UserService.model.User;
import com.shopapp.UserService.dto.user.request.RegisterUserRequest;
import com.shopapp.UserService.dto.user.request.UpdateUserRequest;
import com.shopapp.UserService.dto.user.response.UserResponse;
import com.shopapp.UserService.util.JwtUtil;
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
        return user;
    }

    public void updateEntity(User user, UpdateUserRequest request) {
        user.setEmail(request.getEmail());

    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        String jwt = jwtUtil.generateToken(user.getEmail());


        response.setId(user.getId());
        response.setEmail(user.getEmail());

        return response;
    }
}
