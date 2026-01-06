package com.example.dataware.todolist.util.fileValidation;

import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.EmptyFileException;
import com.example.dataware.todolist.exception.custom.InvalidFileTypeException;
import com.example.dataware.todolist.util.fileValidation.enums.ImageMimeType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageValidation {

    private static final Tika TIKA = new Tika();

    /**
     * Valida il file immagine e restituisce il tipo MIME supportato.
     *
     * @param file il file da validare
     * @return {@link ImageMimeType} rilevato dal contenuto del file
     * @throws EmptyFileException       se il file è nullo o vuoto
     * @throws InvalidFileTypeException se il tipo di file non è supportato
     */
    public static ImageMimeType validateAndGetImageMimeType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Il file è vuoto");
        }

        try {

            String detectedMimeType = TIKA.detect(file.getInputStream());
            log.debug("Mime Type rilevato: {}", detectedMimeType);

            return ImageMimeType.fromMimeType(detectedMimeType)
                    .orElseThrow(() -> new InvalidFileTypeException("Tipo di file non valido: " + detectedMimeType));

        } catch (IOException e) {
            log.error("Errore lettura file: {}", e.getMessage(), e);
            throw new InvalidFileTypeException("Errore lettura file");
        }
    }

}
