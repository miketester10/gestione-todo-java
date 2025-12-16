package com.example.dataware.todolist.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dataware.todolist.dto.LoginDto;
import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.mapper.UserMapper;
import com.example.dataware.todolist.service.AuthService;
import com.example.dataware.todolist.util.SuccessResponse;
import com.example.dataware.todolist.util.SuccessResponseBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final SuccessResponseBuilder apiResponseBuilder;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<UserResponse>> register(@Valid @RequestBody UserDto userDto) {

        User user = authService.register(userDto);
        UserResponse userResponse = userMapper.toDTO(user);
        return apiResponseBuilder.success(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Map<String, String>>> login(@Valid @RequestBody LoginDto loginDto) {

        Map<String, String> accessToken = authService.login(loginDto);
        return apiResponseBuilder.success(accessToken, HttpStatus.OK);
    }
}