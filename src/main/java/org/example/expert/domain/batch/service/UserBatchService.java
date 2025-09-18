package org.example.expert.domain.batch.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor    
@Profile("batch")
public class UserBatchService {

    private final UserManyDummyInsertAsyncService userManyDummyInsertAsyncService;
    private final BatchLockService batchLockService;

    @Transactional
    public String runBatchInsert() {
        if( batchLockService.isRunning() ) {
            return "이미 실행중입니다.";
        } else {
            userManyDummyInsertAsyncService.insertManyDummies(batchLockService.createLock());
            return "배치를 실행하였습니다.";
        }
    }
}
