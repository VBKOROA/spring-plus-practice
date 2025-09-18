package org.example.expert.domain.store.service;

import java.io.IOException;
import java.util.UUID;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.example.expert.domain.store.component.ProfileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import lombok.extern.slf4j.Slf4j;

@Service
@Profile({"gcp", "prod"})
@Slf4j
public class ProfileStoreGcpService implements ProfileStoreService {
    private final Storage storage;
    private final String bucketName;
    private final ProfileValidator profileValidator;

    public ProfileStoreGcpService(Storage storage, @Value("${spring.cloud.gcp.storage.bucket-name}") String bucketName, ProfileValidator profileValidator) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.profileValidator = profileValidator;
    }

    @Override
    public String store(MultipartFile profile) {
        // 경로 조작 방어
        String safeFileName = StringUtils.cleanPath(profile.getOriginalFilename());
        String storeFileName = UUID.randomUUID().toString() + "-" + safeFileName;

        if (!profileValidator.isValid(profile)) {
            throw new InvalidRequestException("이미지 파일이 아닙니다.");
        }

        try {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, storeFileName)
                .setContentType(profile.getContentType())
                .build();

            storage.create(blobInfo, profile.getBytes());
        } catch (StorageException | IOException ex) {
            log.warn("GCP Store Profile Failed: {}", ex.getMessage());
            throw new ServerException("파일 업로드에 실패했습니다.");
        }

        // GCP 공개 URL 반환 (환경에 따라 URL 포맷 변경 필요)
        return "https://storage.googleapis.com/" + bucketName + "/" + storeFileName;
    }
}
