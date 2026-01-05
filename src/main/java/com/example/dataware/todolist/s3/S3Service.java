package com.example.dataware.todolist.s3;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.dataware.todolist.exception.custom.S3UploadException;
import com.example.dataware.todolist.util.fileValidation.ImageValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client S3Client;
    private final S3Properties S3Properties;

    public String uploadUserProfileImage(Long userId, MultipartFile file) {
        String mimeType = ImageValidation.validateAndGetMimeType(file);

        String ext = switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        String key = "users/" + userId + "/profile" + ext;

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

            log.error("Errore durante l'upload dell'immagine profilo: " + e.getMessage());
            throw new S3UploadException("Errore durante l'upload dell'immagine profilo");

        }

        return buildPublicUrl(key);
    }

    public void deleteUserProfileImage(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserProfileImage'");
    }

    private String buildPublicUrl(String key) {
        return "https://" + S3Properties.getS3Bucket() + ".s3." + S3Properties.getRegion() + ".amazonaws.com/" + key;
    }

}
