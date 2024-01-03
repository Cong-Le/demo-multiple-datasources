package com.example.demo_multiple_datasources.model.mapper;

import com.example.demo_multiple_datasources.domain.User;
import com.example.demo_multiple_datasources.model.dto.UserDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserMapper {

    public List<UserDTO> toUserDTO(List<User> userList) {
        if (userList == null) {
            return null;
        }

        return userList.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setEmail(user.getEmail());
                userDTO.setName(user.getName());
                userDTO.setAddress(user.getAddress());
                userDTO.setPhoneNumber(user.getPhoneNumber());
                return userDTO;
            }
        ).toList();
    }
}
