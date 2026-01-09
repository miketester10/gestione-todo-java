package com.example.dataware.todolist.service.implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final S3Properties S3Properties;
    private final S3Service S3Service;

    @Override
    @Transactional(readOnly = true) // Ottimizza la lettura paginata
    public Page<User> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true) // Lettura sicura
    public User findOne(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utente non trovato."));
    }

    @Override
    @Transactional // Gestisce la consistenza tra S3 (logica manuale) e DB
    public User updateProfileImage(String email, MultipartFile file) {
        User user = findOne(email);

        // Salva l'URL corrente PRIMA di fare qualsiasi operazione
        String oldImageUrl = user.getProfileImageUrl();

        // Carica la nuova immagine su S3
        String newUrl = S3Service.uploadUserProfileImage(user.getId(), file);

        // Aggiorna lo User nel DB PRIMA di eliminare il vecchio file
        // Questo garantisce che se il salvataggio fallisce, il vecchio file rimane
        user.setProfileImageUrl(newUrl);
        User savedUser = userRepository.save(user);

        // Elimina il vecchio file SOLO dopo che il DB è stato aggiornato con successo
        deleteImageFromS3(oldImageUrl, newUrl);

        return savedUser;
    }

    @Override
    @Transactional // Assicura che l'aggiornamento DB avvenga correttamente prima di rimuovere da
                   // S3
    public User deleteProfileImage(String email) {
        User user = findOne(email);
        String oldImageUrl = user.getProfileImageUrl();

        // Se l'immagine è già quella di default, non serve fare nulla
        if (S3Properties.getDefaultAvatarUrl().equals(oldImageUrl)) {
            return user;
        }

        // Elimina il vecchio file e aggiorna il DB
        deleteImageFromS3(oldImageUrl);
        user.setProfileImageUrl(S3Properties.getDefaultAvatarUrl());
        return userRepository.save(user);
    }

    @Override
    @Transactional // Eliminazione atomica dal DB
    public void delete(String email) {
        User user = findOne(email);
        userRepository.delete(user);
    }

    /**
     * Elimina un'immagine da S3 se non è null, vuota, corrisponde all'avatar di
     * default
     * o è uguale all'URL da escludere (se specificato).
     * 
     * @param imageUrl l'URL dell'immagine da eliminare
     */
    private void deleteImageFromS3(String imageUrl) {
        deleteImageFromS3(imageUrl, null);
    }

    /**
     * Elimina un'immagine da S3 se non è null, vuota, corrisponde all'avatar di
     * default
     * o è uguale all'URL da escludere (se specificato).
     * 
     * @param imageUrl   l'URL dell'immagine da eliminare
     * @param excludeUrl URL da escludere dal confronto (es. nuovo URL in
     *                   updateProfileImage)
     */
    private void deleteImageFromS3(String imageUrl, String excludeUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        if (S3Properties.getDefaultAvatarUrl().equals(imageUrl)) {
            return;
        }

        if (excludeUrl != null && imageUrl.equals(excludeUrl)) {
            return;
        }

        S3Service.deleteFileByUrl(imageUrl);
    }
}
