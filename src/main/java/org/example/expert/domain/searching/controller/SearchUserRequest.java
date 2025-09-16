package org.example.expert.domain.searching.controller;

import jakarta.validation.constraints.NotBlank;

public record SearchUserRequest(
    @NotBlank
    String nickname,
    Integer page,
    Integer size
) {

}
