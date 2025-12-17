package com.example.dataware.todolist.interfaces;

import com.example.dataware.todolist.entity.User;

public interface UserService {

    User findOne(String email);

    void delete(String email);

}
