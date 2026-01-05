package com.example.dataware.todolist.util.fileValidation;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.EmptyFileException;
import com.example.dataware.todolist.exception.custom.InvalidFileTypeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageValidation {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private static final Tika TIKA = new Tika();

    public static String validateAndGetMimeType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Il file è vuoto");
        }

        try {

            String detectedMimeType = TIKA.detect(file.getInputStream());

            if (!ALLOWED_TYPES.contains(detectedMimeType)) {
                throw new InvalidFileTypeException("Tipo di file non valido: " + detectedMimeType);
            }

            return detectedMimeType;

        } catch (IOException e) {

            log.error("Errore lettura file: " + e.getMessage());
            throw new InvalidFileTypeException("Errore lettura file");

        }
    }

}
