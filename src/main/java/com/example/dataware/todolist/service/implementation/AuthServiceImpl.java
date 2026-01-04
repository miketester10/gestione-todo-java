package com.example.dataware.todolist.service.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.dataware.todolist.dto.response.TokenResponse;
import com.example.dataware.todolist.dto.validator.LoginDto;
import com.example.dataware.todolist.dto.validator.UserDto;
import com.example.dataware.todolist.entity.User;
import com.example.dataware.todolist.exception.custom.EmailConflictException;
import com.example.dataware.todolist.exception.custom.InvalidCredentialsException;
import com.example.dataware.todolist.jwt.service.JwtService;
import com.example.dataware.todolist.repository.UserRepository;
import com.example.dataware.todolist.s3.S3Properties;
import com.example.dataware.todolist.service.EncryptionService;
import com.example.dataware.todolist.service.interfaces.AuthService;
import com.example.dataware.todolist.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Logger
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EncryptionService encryptionService;
    private final S3Properties s3Properties;

    @Override
    public User register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailConflictException("Email già registrata");
        }

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = User.builder()
                .nome(userDto.getNome())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .profileImageUrl(s3Properties.getDefaultAvatarUrl())
                .build();

        return userRepository.save(user);
    }

    @Override
    public TokenResponse login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Email o password non validi"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email o password non validi");
        }

        return generateAndPersistTokens(user);
    }

    @Override
    public TokenResponse refreshToken(String email) {
        User user = userService.findOne(email);
        return generateAndPersistTokens(user);
    }

    @Override
    public void logout(String email) {
        User user = userService.findOne(email);
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    private TokenResponse generateAndPersistTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        persistEncryptedRefreshToken(user, refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void persistEncryptedRefreshToken(User user, String refreshToken) {
        String encryptedToken = encryptionService.encrypt(refreshToken);
        user.setRefreshToken(encryptedToken);
        userRepository.save(user);
    }

}
