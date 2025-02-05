package com.tracker.UserService.util;


import com.tracker.UserService.model.User;
import com.tracker.UserService.user.UserDTO;

public class EntityDtoUtil {

    public static UserDTO convertToDto(
            User user){

        return new UserDTO(
                user.getId(),
                user.getEmail()
        );
    }


}
