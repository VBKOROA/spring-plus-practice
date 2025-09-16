package org.example.expert.domain.batch.service;

import org.example.expert.domain.batch.entity.BatchLock;
import org.example.expert.domain.batch.repository.BatchLockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BatchLockService {
    private final BatchLockRepository batchLockRepository;

    @Transactional
    public long createLock() {
        BatchLock newLock = BatchLock.newLock();
        BatchLock savedLock = batchLockRepository.save(newLock);
        return savedLock.getId();
    }

    @Transactional
    public void releaseLock(long lockId) {
        batchLockRepository.deleteById(lockId);
    }

    @Transactional(readOnly = true)
    public boolean isRunning() {
        return batchLockRepository.count() > 0;
    }
}
