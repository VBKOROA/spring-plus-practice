package org.example.expert.domain.batch.repository;

import org.example.expert.domain.batch.entity.BatchLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchLockRepository extends JpaRepository<BatchLock, Long>{
    
}
