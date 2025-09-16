package org.example.expert.domain.batch.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.expert.domain.batch.repository.UserInsertQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserManyDummyInsertAsyncService {

    private final int dummyCount;
    private final int maxInsertCount;
    private final UserBatchInsertService userBatchInsertService;
    private final BatchLockService batchLockService;

    public UserManyDummyInsertAsyncService(@Value("${batch.user.dummy-count}") int dummyCount,
            UserBatchInsertService userBatchInsertService,
            @Value("${batch.user.max-insert-count}") int maxInsertCount, BatchLockService batchLockService) {
        this.dummyCount = dummyCount;
        this.maxInsertCount = maxInsertCount;
        this.userBatchInsertService = userBatchInsertService;
        this.batchLockService = batchLockService;
    }

    @Async
    public void insertManyDummies(long lockId) {

        log.info("=== 배치 작업 시작 ===");
        List<UserInsertQuery> dummies = createDummies();

        for (int i = 0; i < dummyCount; i += maxInsertCount) {
            int end = Math.min(i + maxInsertCount, dummies.size());
            log.info("=== 배치 넘버: {} ~ {} ===", i, end);
            List<UserInsertQuery> batchDummies = dummies.subList(i, end);
            try {
                userBatchInsertService.insert(batchDummies);
            } catch (Exception e) {
                log.warn("배치 INSERT 도중 오류 발생: {}", e.getMessage());
            }
        }
        log.info("=== 배치 작업 끝 ===");
        batchLockService.releaseLock(lockId);
    }

    private List<UserInsertQuery> createDummies() {

        LocalDateTime current = LocalDateTime.now();
        List<UserInsertQuery> dummies = new ArrayList<>();

        for (int i = 0; i < dummyCount; i++) {
            String randomStr = UUID.randomUUID().toString();
            dummies.add(UserInsertQuery.builder()
                    .randomStr(randomStr)
                    .time(current.plusSeconds(i))
                    .build());
        }

        return dummies;
    }
}
