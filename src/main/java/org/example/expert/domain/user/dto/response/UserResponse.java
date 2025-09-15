package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String profile;

    public UserResponse(Long id, String email, String profile) {
        this.id = id;
        this.email = email;
        this.profile = profile;
    }
}
