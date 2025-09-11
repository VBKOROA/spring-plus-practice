package org.example.expert.domain.searching.controller;

public record SearchTodoResponse(
    String todoTitle,
    long managerCount,
    long commentCount
) {
}
