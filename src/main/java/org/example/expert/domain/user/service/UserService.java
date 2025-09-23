package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;

import org.example.expert.config.auth.JwtUtil;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.store.service.ProfileStoreService;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileStoreService profileStoreService;
    private final JwtUtil jwtUtil;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getProfile());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));
        String newPassword = userChangePasswordRequest.getNewPassword();
        String oldPassword = userChangePasswordRequest.getOldPassword();

        user.changePassword(newPassword, oldPassword, passwordEncoder);
    }

    @Transactional
    public String changeProfile(long userId, MultipartFile profile) {
        if (profile.isEmpty()) {
            throw new InvalidRequestException("파일에 내용이 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        String profileUrl = profileStoreService.store(profile);

        user.changeProfile(profileUrl);

        return jwtUtil.createToken(userId, user.getEmail(), user.getNickname(), user.getProfile(), user.getUserRole());
    }
}
