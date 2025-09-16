package org.example.expert.domain.batch.repository;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record UserInsertQuery(
    String randomStr,
    LocalDateTime time
) {
    
}
