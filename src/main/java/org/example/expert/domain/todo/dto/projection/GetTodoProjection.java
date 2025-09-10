package org.example.expert.domain.todo.dto.projection;

import java.time.LocalDateTime;

public record GetTodoProjection(
    Long id,
    String title,
    String contents,
    String weather,
    SimpleUserProjection user,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {
    
}
