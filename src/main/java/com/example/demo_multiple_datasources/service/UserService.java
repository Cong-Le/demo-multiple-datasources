package com.example.demo_multiple_datasources.service;

import com.example.demo_multiple_datasources.domain.User;
import com.example.demo_multiple_datasources.model.dto.UserDTO;
import com.example.demo_multiple_datasources.model.mapper.UserMapper;
import com.example.demo_multiple_datasources.repository.UserRepository;
import com.example.demo_multiple_datasources.repository.UserRepositoryCustom;
import com.example.demo_multiple_datasources.request.DeleteUserRequest;
import com.example.demo_multiple_datasources.request.RegisterUserRequest;
import com.example.demo_multiple_datasources.request.UpdateUserRequest;
import com.example.demo_multiple_datasources.util.UuidGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    private final UserRepositoryCustom userRepositoryCustom;
    
    private final UserMapper userMapper;
    
    @Transactional(transactionManager = "dmdTransactionManager", readOnly = true)
    public List<UserDTO> findAllUsers(String userId) {
        List<User> userList;
                
        if (StringUtils.isNotBlank(userId)) {
            userList = userRepository.findUserById(userId);
        } else {
            userList = userRepository.findAllUsers();
        }

        return userMapper.toUserDTO(userList);
    }

    @Transactional(transactionManager = "dmdTransactionManager")
    public String registerUser(RegisterUserRequest request) {
        String id = UuidGenerator.generateModelId();
        
        User user = new User();
        user.setId(id);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDeleted(false);
        
        userRepository.save(user);
        
        return id;
    }

    @Transactional(transactionManager = "dmdTransactionManager")
    public boolean updateUser(UpdateUserRequest request) {
        if (StringUtils.isBlank(request.getId())) {
            return false;
        }

        List<User> userList = userRepository.findUserById(request.getId());
        if (userList.isEmpty()) {
            return false;
        }
        
        User user = userList.get(0);
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return true;
    }

    @Transactional(transactionManager = "dmdTransactionManager")
    public boolean deleteUser(DeleteUserRequest request) {
        if (StringUtils.isBlank(request.getId())) {
            return false;
        }

        List<User> userList = userRepository.findUserById(request.getId());
        if (userList.isEmpty()) {
            return false;
        }

        User user = userList.get(0);
        user.setDeleted(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return true;
    }
}
