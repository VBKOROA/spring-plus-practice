package org.example.expert.domain.searching.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public record SearchTodoRequest(
    String title,
    Integer page,
    Integer size,
    String managerNickname,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
) {
    public static SearchTodoRequest allNull() {
        return new SearchTodoRequest(null, null, null, null, null, null);
    }
}
