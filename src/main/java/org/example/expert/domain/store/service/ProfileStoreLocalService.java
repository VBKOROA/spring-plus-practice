package org.example.expert.domain.store.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

@Service
@Slf4j
@Profile({ "dev" })
public class ProfileStoreLocalService implements ProfileStoreService {
    private final Path storeLocation;
    private final ProfileValidator profileValidator;

    public ProfileStoreLocalService(@Value("${profile.store.location}") String storeLocation, ProfileValidator profileValidator) {
        this.storeLocation = Paths.get(storeLocation).toAbsolutePath().normalize();
        this.profileValidator = profileValidator;

        // 현재 경로를 사용할 수 있는지 확인
        try {
            Files.createDirectories(this.storeLocation);
        } catch (IOException ignore) {
            log.error("ProfileStore Setup Error: {}", ignore.getMessage());
        }
    }

    @Override
    public String store(MultipartFile profile) {
        // 경로 조작 공격 방어
        String safeFileName = StringUtils.cleanPath(profile.getOriginalFilename());
        String storeFileName = UUID.randomUUID().toString() + "-" + safeFileName;

        if (profileValidator.isValid(profile) == false) {
            throw new InvalidRequestException("이미지 파일이 아닙니다.");
        }

        Path targetLocation = this.storeLocation.resolve(storeFileName);
        try {
            Files.copy(profile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            log.warn("Store Profile Failed: {}", exception.getMessage());
            throw new ServerException("파일 업로드에 실패했습니다.");
        }

        // 전체 경로 반환
        return targetLocation.toAbsolutePath().normalize().toString().replace('\\', '/');
    }
}
