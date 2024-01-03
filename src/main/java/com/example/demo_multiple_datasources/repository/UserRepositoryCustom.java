package com.example.demo_multiple_datasources.repository;

import com.example.demo_multiple_datasources.model.dto.UserDTO;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserDTO> findAllUsers(String userId);
}
