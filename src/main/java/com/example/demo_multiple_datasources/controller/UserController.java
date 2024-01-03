package com.example.demo_multiple_datasources.controller;

import com.example.demo_multiple_datasources.model.dto.UserDTO;
import com.example.demo_multiple_datasources.request.DeleteUserRequest;
import com.example.demo_multiple_datasources.request.RegisterUserRequest;
import com.example.demo_multiple_datasources.request.UpdateUserRequest;
import com.example.demo_multiple_datasources.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(value = "user_id", required = false) String userId) {
        List<UserDTO> users = userService.findAllUsers(userId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request) {
        String userId = userService.registerUser(request);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserRequest request) {
        return ResponseEntity.ok(userService.deleteUser(request));
    }
}
