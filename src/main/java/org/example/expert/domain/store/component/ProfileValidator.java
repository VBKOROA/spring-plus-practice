package org.example.expert.domain.store.component;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProfileValidator {
    public boolean validate(MultipartFile proifle) {
        return proifle.getContentType().startsWith("image/");
    }
}
