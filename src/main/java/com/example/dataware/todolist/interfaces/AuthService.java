package com.example.dataware.todolist.interfaces;

import com.example.dataware.todolist.dto.LoginDto;
import com.example.dataware.todolist.dto.response.TokenResponse;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;

public interface AuthService {

    User register(UserDto userDto);

    TokenResponse login(LoginDto loginDto);

    TokenResponse refreshToken(String email);

    void logout(String email);

}
