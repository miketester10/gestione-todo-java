package com.example.dataware.todolist.service.implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.exception.custom.UserNotFoundException;
import com.example.dataware.todolist.repository.UserRepository;
import com.example.dataware.todolist.s3.S3Properties;
import com.example.dataware.todolist.s3.S3Service;
import com.example.dataware.todolist.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final S3Properties s3Properties;
    private final S3Service s3Service;

    @Override
    public Page<User> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));
        return userRepository.findAll(pageable);
    }

    @Override
    public User findOne(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato."));
    }

    @Override
    public User updateProfileImage(String email, MultipartFile file) {
        User user = findOne(email);
        String newUrl = s3Service.uploadUserProfileImage(user.getId(), file);
        user.setProfileImageUrl(newUrl);
        return userRepository.save(user);
    }

    @Override
    public User deleteProfileImage(String email) {
        User user = findOne(email);
        s3Service.deleteUserProfileImage(user.getId());
        user.setProfileImageUrl(s3Properties.getDefaultAvatarUrl());
        return userRepository.save(user);
    }

    @Override
    public void delete(String email) {
        User user = findOne(email);
        userRepository.delete(user);
    }

}
