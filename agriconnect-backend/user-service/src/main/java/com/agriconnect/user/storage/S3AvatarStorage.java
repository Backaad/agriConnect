package com.agriconnect.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3AvatarStorage {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.cdn.base-url}")
    private String cdnBaseUrl;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/jpg", "image/png", "image/webp");

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    public String uploadAvatar(UUID userId, MultipartFile file) throws IOException {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String key = "avatars/" + userId + "/" + UUID.randomUUID() + "." + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        log.info("Avatar uploadé: key={}", key);

        return cdnBaseUrl + "/" + key;
    }

    public void deleteAvatar(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.contains(cdnBaseUrl)) return;
        String key = avatarUrl.replace(cdnBaseUrl + "/", "");
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        log.info("Avatar supprimé: key={}", key);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("Le fichier est vide");
        if (!ALLOWED_TYPES.contains(file.getContentType()))
            throw new IllegalArgumentException("Type de fichier non autorisé. Utilisez JPEG, PNG ou WebP");
        if (file.getSize() > MAX_SIZE_BYTES)
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale de 5MB");
    }

    private String getExtension(String filename) {
        if (filename == null) return "jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "jpg";
    }
}
