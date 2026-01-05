package com.example.dataware.todolist.util.fileValidation;

import java.io.IOException;
import java.util.List;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.EmptyFileException;
import com.example.dataware.todolist.exception.custom.InvalidFileTypeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageValidation {

    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    private static final Tika TIKA = new Tika();

    /**
     * Valida il file e restituisce il MIME type.
     * 
     * @param file il file da validare
     * @return il MIME type del file se valido
     * @throws EmptyFileException       se il file è vuoto
     * @throws InvalidFileTypeException se il tipo di file non è supportato
     */
    public static String validateAndGetMimeType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Il file è vuoto");
        }

        try {

            String detectedMimeType = TIKA.detect(file.getInputStream());
            log.warn("Mime Type rilevato: {}", detectedMimeType);

            if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
                throw new InvalidFileTypeException("Tipo di file non valido: " + detectedMimeType);
            }

            return detectedMimeType;

        } catch (IOException e) {

            log.error("Errore lettura file: " + e.getMessage());
            throw new InvalidFileTypeException("Errore lettura file");

        }
    }

}
