package com.example.dataware.todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dataware.todolist.dto.response.TokenResponse;
import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.dto.response.builder.SuccessResponse;
import com.example.dataware.todolist.dto.response.builder.SuccessResponseBuilder;
import com.example.dataware.todolist.dto.validator.LoginDto;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.filter.jwt.payload.JwtPayload;
import com.example.dataware.todolist.mapper.UserMapper;
import com.example.dataware.todolist.service.interfaces.AuthService;

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
    public ResponseEntity<SuccessResponse<TokenResponse>> login(@Valid @RequestBody LoginDto loginDto) {

        TokenResponse tokenResponse = authService.login(loginDto);
        return apiResponseBuilder.success(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<SuccessResponse<TokenResponse>> refresh(@AuthenticationPrincipal JwtPayload jwtPayload) {

        TokenResponse tokenResponse = authService.refreshToken(jwtPayload.getEmail());
        return apiResponseBuilder.success(tokenResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<SuccessResponse<String>> logout(@AuthenticationPrincipal JwtPayload jwtPayload) {

        authService.logout(jwtPayload.getEmail());
        return apiResponseBuilder.success("Logged out successfully", HttpStatus.OK);
    }
}