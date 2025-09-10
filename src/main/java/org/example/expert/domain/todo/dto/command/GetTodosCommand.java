package org.example.expert.domain.todo.dto.command;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record GetTodosCommand(
    int page,
    int size,
    String weather,
    LocalDate startDate,
    LocalDate endDate
) {
    
}
