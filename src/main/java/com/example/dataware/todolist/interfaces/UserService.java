package com.example.dataware.todolist.interfaces;

import java.util.List;

import com.example.dataware.todolist.entity.User;

public interface UserService {

    List<User> findAll();

    User findOne(String email);

    void delete(String email);

}
