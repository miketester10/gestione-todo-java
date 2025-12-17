package com.example.dataware.todolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.dataware.todolist.dto.response.UserResponse;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.interfaces.UserService;
import com.example.dataware.todolist.jwt.JwtPayload;
import com.example.dataware.todolist.mapper.UserMapper;
import com.example.dataware.todolist.util.SuccessResponse;
import com.example.dataware.todolist.util.SuccessResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SuccessResponseBuilder apiResponseBuilder;

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
