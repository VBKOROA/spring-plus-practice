package org.example.expert.domain.store.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileStoreService {
    String store(MultipartFile files);
}