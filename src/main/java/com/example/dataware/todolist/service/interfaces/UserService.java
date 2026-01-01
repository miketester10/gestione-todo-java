package com.example.dataware.todolist.service.interfaces;

import org.springframework.data.domain.Page;

import com.example.dataware.todolist.entity.User;

public interface UserService {

    Page<User> findAll(int page, int limit);

    User findOne(String email);

    void delete(String email);

}
