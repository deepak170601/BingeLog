package com.shopapp.UserService.util;


import com.shopapp.UserService.dto.user.UserDTO;
import com.shopapp.UserService.model.User;

public class EntityDtoUtil {

    public static UserDTO convertToDto(User user){

        return new UserDTO(
                user.getId(),
                user.getEmail()
        );
    }


}
