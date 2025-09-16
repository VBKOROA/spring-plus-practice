package org.example.expert.domain.searching.service;

import org.springframework.data.domain.Pageable;

public record SearchUserCommand(
    String nickname,
    Pageable pageable
) {

}
