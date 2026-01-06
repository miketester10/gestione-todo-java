package com.example.dataware.todolist.s3;

import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.S3UploadException;
import com.example.dataware.todolist.util.fileValidation.ImageValidation;
import com.example.dataware.todolist.util.fileValidation.enums.ImageMimeType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
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
     * @param file   file da caricare
     * @return URL pubblico dell'immagine caricata
     * @throws S3UploadException se l'upload fallisce
     */
    public String uploadUserProfileImage(Long userId, MultipartFile file) {

        // Valida il file (verifica che sia un'immagine valida)
        ImageMimeType imageMimeType = ImageValidation.validateAndGetImageMimeType(file);

        String ext = imageMimeType.getExtension();
        String key = "users/" + userId + "/profile" + ext;

        // Carica l'immagine originale su S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(S3Properties.getS3Bucket())
                .key(key)
                .contentType(imageMimeType.getMimeType())
                .build();

        try {
            S3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.debug("File caricato con successo su S3: {}", key);
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

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(S3Properties.getS3Bucket())
                .key(key)
                .build();

        try {
            S3Client.deleteObject(deleteObjectRequest);
            log.debug("File eliminato con successo da S3: {}", key);
        } catch (Exception e) {
            // Log ma non lanciare eccezione: il file potrebbe non esistere o essere già
            // eliminato
            log.warn("Errore durante l'eliminazione del file su S3 (key: {}): {}", key, e.getMessage());
        }
    }

    /**
     * Estrae la key S3 dall'URL pubblico.
     * Supporta solo il formato URL Virtual-hosted style di S3:
     * - https://bucket.s3.region.amazonaws.com/key
     * 
     * @param s3Url URL pubblico del file
     * @return la key S3 o null se l'URL non è valido
     */
    private String extractKeyFromUrl(String s3Url) {
        try {
            URI uri = new URI(s3Url);
            String path = uri.getPath(); // Da https://bucket.s3.region.amazonaws.com/key restituisce "/key"
            return path.substring(1); // Rimuove lo slash iniziale da "/key" e restituisce "key"
        } catch (Exception e) {
            log.warn("Errore durante l'estrazione della key dall'URL S3: {} - {}", s3Url, e.getMessage());
            return null;
        }
    }

    /**
     * Costruisce URL pubblico.
     * 
     * @param key Key s3
     * @return l'URL pubblico del file
     */
    private String buildPublicUrl(String key) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(S3Properties.getS3Bucket())
                .key(key)
                .build();
        return S3Client.utilities().getUrl(request).toExternalForm();
    }

}
