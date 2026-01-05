package com.example.dataware.todolist.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.entity.User;

public interface UserService {

    Page<User> findAll(int page, int limit);

    User findOne(String email);

    User updateProfileImage(String email, MultipartFile file);

    User deleteProfileImage(String email);

    void delete(String email);

}
