package com.example.dataware.todolist.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dataware.todolist.dto.response.PageResponse;
import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.dto.response.builder.SuccessResponse;
import com.example.dataware.todolist.dto.response.builder.SuccessResponseBuilder;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.interfaces.UserService;
import com.example.dataware.todolist.jwt.JwtPayload;
import com.example.dataware.todolist.mapper.UserMapper;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@RestController
@RequestMapping("/users")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SuccessResponseBuilder apiResponseBuilder;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<PageResponse<UserResponse>>> findAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {

        page = page - 1;
        Page<User> users = userService.findAll(page, limit);
        Page<UserResponse> userResponsePage = users.map(user -> userMapper.toDTO(user));
        PageResponse<UserResponse> pageResponse = PageResponse.of(userResponsePage);
        return apiResponseBuilder.success(pageResponse, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<SuccessResponse<UserResponse>> getProfile(@AuthenticationPrincipal JwtPayload jwtPayload) {

        User user = userService.findOne(jwtPayload.getEmail());
        UserResponse userResponse = userMapper.toDTO(user);
        return apiResponseBuilder.success(userResponse, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<SuccessResponse<Void>> delete(@AuthenticationPrincipal JwtPayload jwtPayload) {

        userService.delete(jwtPayload.getEmail());
        return apiResponseBuilder.success(HttpStatus.OK);
    }

}
