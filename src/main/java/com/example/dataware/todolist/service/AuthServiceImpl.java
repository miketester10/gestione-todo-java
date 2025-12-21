package com.example.dataware.todolist.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.dataware.todolist.dto.LoginDto;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.interfaces.AuthService;
import com.example.dataware.todolist.jwt.service.JwtService;
import com.example.dataware.todolist.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public User register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già registrata");
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = User.builder()
                .nome(userDto.getNome())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .build();

        return userRepository.save(user);
    }

    @Override
    public Map<String, String> login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password non validi"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o password non validi");
        }

        String accessToken = jwtService.generateToken(user);

        return Map.of("accessToken", accessToken);

    }
}
