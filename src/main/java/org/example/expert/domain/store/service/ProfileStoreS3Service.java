package org.example.expert.domain.store.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.store.component.ProfileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Profile({"prod"})
@Slf4j
public class ProfileStoreS3Service implements ProfileStoreService {
    private final S3Client s3Client;
    private final ProfileValidator profileValidator;
    private final String bucket;
    
    public ProfileStoreS3Service(S3Client s3Client, ProfileValidator profileValidator, @Value("${spring.cloud.aws.s3.bucket}") String bucket) {
        this.s3Client = s3Client;
        this.profileValidator = profileValidator;
        this.bucket = bucket;
    }

    @Override
    public String store(MultipartFile profile) {
        // 경로 조작 방어
        String safeFileName = StringUtils.cleanPath(profile.getOriginalFilename());
        String storeFileName = UUID.randomUUID().toString() + "-" + safeFileName;

        if (!profileValidator.validate(profile)) {
            throw new InvalidRequestException("이미지 파일이 아닙니다.");
        }

        String key = storeFileName;

        try (InputStream is = profile.getInputStream()) {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(profile.getContentType())
                    .contentLength(profile.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(is, profile.getSize()));
        } catch (IOException | SdkException ex) {
            log.warn("S3 Store Profile Failed: {}", ex.getMessage());
            throw new ServerException("파일 업로드에 실패했습니다.");
        }

        // S3 공개 URL 반환 (환경에 따라 URL 포맷 변경 필요)
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
    }
}
