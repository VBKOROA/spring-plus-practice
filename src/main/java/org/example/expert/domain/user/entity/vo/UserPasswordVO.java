package org.example.expert.domain.user.entity.vo;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPasswordVO {
    @Column(name = "password", nullable = false)
    private String value;

    public static UserPasswordVO fromRawString(String password, PasswordEncoder passwordEncoder) {
        if (password.length() < 8 ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
        return new UserPasswordVO(passwordEncoder.encode(password));
    }

    public boolean matches(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, value);
    }
}
