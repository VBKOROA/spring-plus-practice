package org.example.expert.domain.searching.repository;

public record TodoSummaryProjection(
    Long todoId,
    String todoTitle,
    Long managerCount,
    Long commentCount
) {
    
}
