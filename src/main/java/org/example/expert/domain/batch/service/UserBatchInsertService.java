package org.example.expert.domain.batch.service;

import java.util.List;

import org.example.expert.domain.batch.repository.UserInsertQuery;
import org.example.expert.domain.batch.repository.UserJdbcRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("batch")
public class UserBatchInsertService {
    private final UserJdbcRepository userJdbcRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(List<UserInsertQuery> queries) {
        userJdbcRepository.insertBatch(queries);
    }
}
