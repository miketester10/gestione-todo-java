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

        // Elimina il vecchio file se diverso dal default
        deleteCurrentProfileImageIfNotDefault(user);

        // Carica la nuova immagine su S3
        String newUrl = s3Service.uploadUserProfileImage(user.getId(), file);
        user.setProfileImageUrl(newUrl);

        return userRepository.save(user);
    }

    @Override
    public User deleteProfileImage(String email) {
        User user = findOne(email);

        // Se l'immagine è già quella di default, non serve fare nulla
        if (s3Properties.getDefaultAvatarUrl().equals(user.getProfileImageUrl())) {
            return user; // Ritorna l'utente senza modifiche
        }

        // Elimina il vecchio file se diverso dal default
        deleteCurrentProfileImageIfNotDefault(user);

        // Imposta l'immagine di default
        user.setProfileImageUrl(s3Properties.getDefaultAvatarUrl());

        return userRepository.save(user);
    }

    @Override
    public void delete(String email) {
        User user = findOne(email);
        userRepository.delete(user);
    }

    private void deleteCurrentProfileImageIfNotDefault(User user) {
        String currentImageUrl = user.getProfileImageUrl();
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            if (!currentImageUrl.equals(s3Properties.getDefaultAvatarUrl())) {
                s3Service.deleteFileByUrl(currentImageUrl);
            }
        }
    }

}
