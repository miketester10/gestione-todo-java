package com.example.dataware.todolist.interfaces;

import java.util.Map;

import com.example.dataware.todolist.dto.LoginDto;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;

public interface AuthService {

    User register(UserDto userDto);

    Map<String, String> login(LoginDto loginDto);

}
