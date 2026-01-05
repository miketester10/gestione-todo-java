package com.example.dataware.todolist.s3;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.S3UploadException;
import com.example.dataware.todolist.util.fileValidation.ImageValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client S3Client;
    private final S3Properties S3Properties;

    /**
     * Carica un'immagine profilo su S3 mantenendo il formato originale.
     * 
     * @param userId ID dell'utente
     * @param file   file immagine da caricare (JPEG, PNG o WebP)
     * @return URL pubblico dell'immagine caricata
     */
    public String uploadUserProfileImage(Long userId, MultipartFile file) {

        // Valida il file (verifica che sia un'immagine valida)
        String mimeType = ImageValidation.validateAndGetMimeType(file);

        // Determina l'estensione basata sul MIME type
        String ext = switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        String key = "users/" + userId + "/profile" + ext;

        // Carica l'immagine originale su S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(S3Properties.getS3Bucket())
                .key(key)
                .contentType(mimeType)
                .build();

        try {

            S3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        } catch (Exception e) {
            log.error("Errore durante l'upload del file su S3: {}", e.getMessage(), e);
            throw new S3UploadException("Errore durante l'upload del file");
        }

        return buildPublicUrl(key);
    }

    /**
     * Elimina un file da S3 usando il suo URL pubblico.
     * 
     * @param s3Url URL pubblico del file su S3 (es:
     *              https://bucket.s3.region.amazonaws.com/key)
     */
    public void deleteFileByUrl(String s3Url) {
        if (s3Url == null || s3Url.isEmpty()) {
            log.debug("URL S3 vuoto, nessun file da eliminare");
            return;
        }

        // Estrai la key dall'URL
        String key = extractKeyFromUrl(s3Url);

        if (key == null || key.isEmpty()) {
            log.warn("Impossibile estrarre la key dall'URL S3: {}", s3Url);
            return;
        }

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(S3Properties.getS3Bucket())
                .key(key)
                .build();

        try {

            S3Client.deleteObject(deleteRequest);
            log.debug("File eliminato da S3: {}", key);

        } catch (Exception e) {
            // Log ma non lanciare eccezione: il file potrebbe non esistere
            log.debug("Errore durante l'eliminazione del file su S3: {}", e.getMessage(), e);
        }
    }

    /**
     * Estrae la key S3 dall'URL pubblico.
     * 
     * @param s3Url URL pubblico del file (es:
     *              https://bucket.s3.region.amazonaws.com/key)
     * @return la key S3 o null se l'URL non è valido
     */
    private String extractKeyFromUrl(String s3Url) {

        String baseUrl = "https://" + S3Properties.getS3Bucket() + ".s3." + S3Properties.getRegion()
                + ".amazonaws.com/";

        if (s3Url.startsWith(baseUrl)) {
            return s3Url.substring(baseUrl.length());
        }

        // Fallback: prova a estrarre tutto dopo l'ultimo /
        int lastSlash = s3Url.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < s3Url.length() - 1) {
            return s3Url.substring(lastSlash + 1);
        }

        return null;
    }

    /**
     * Costruisce URL pubblico.
     * 
     * @param key Key s3
     * @return l'URL pubblico del file
     */
    private String buildPublicUrl(String key) {
        return "https://" + S3Properties.getS3Bucket() + ".s3." + S3Properties.getRegion() + ".amazonaws.com/" + key;
    }

}
