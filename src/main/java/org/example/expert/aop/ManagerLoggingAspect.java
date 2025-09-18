package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerLoggingAspect {
    private final LogService logService;

    @Before("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..)) && args(authUser, todoId, req)")
    public void beforeSaveManager(JoinPoint joinPoint, AuthUser authUser, Long todoId, ManagerSaveRequest req) {
        try {
            if (authUser == null || todoId == null || req == null) {
                return;
            }

            long requesterId = authUser.getId();
            long targetTodoId = todoId.longValue();
            long targetUserId = req.getManagerUserId();

            logService.saveLog(requesterId, targetTodoId, targetUserId);
        } catch (Exception e) {
            log.warn("Failed to save log before saveManager ({}): {}", joinPoint.getSignature().toShortString(), e.getMessage(), e);
        }
    }
}
