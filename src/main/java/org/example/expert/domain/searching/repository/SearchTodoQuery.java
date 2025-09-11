package org.example.expert.domain.searching.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import lombok.Builder;

@Builder
public record SearchTodoQuery(
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String managerNickname,
    Pageable pageable
) {
}
