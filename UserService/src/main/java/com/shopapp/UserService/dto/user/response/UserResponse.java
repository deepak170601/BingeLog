package com.shopapp.UserService.dto.user.response;

import com.shopapp.UserService.dto.user.JwtToken;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
}
