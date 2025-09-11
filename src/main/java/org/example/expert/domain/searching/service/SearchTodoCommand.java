package org.example.expert.domain.searching.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import lombok.Builder;

@Builder
public record SearchTodoCommand(
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String managerNickname,
    Pageable pageable
) {

}
