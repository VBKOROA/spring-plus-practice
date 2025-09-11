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

    @Before("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void beforeSaveManager(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length < 3) {
                return;
            }

            AuthUser authUser = (AuthUser) args[0];
            Long todoId = (Long) args[1];
            ManagerSaveRequest req = (ManagerSaveRequest) args[2];

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
