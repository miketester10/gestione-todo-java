package com.example.dataware.todolist.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.interfaces.UserService;
import com.example.dataware.todolist.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findOne(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato."));
    }

    @Override
    public void delete(String email) {
        User user = findOne(email);
        userRepository.delete(user);
    }
}
