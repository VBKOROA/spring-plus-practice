package org.example.expert.domain.batch.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.example.expert.domain.batch.service.UserBatchService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/batches")
@RequiredArgsConstructor
@Profile("batch")
public class BatchController {
    private final UserBatchService userBatchService;

    @PostMapping("/insert-many-user")
    public String insertManyUser() {
        return userBatchService.runBatchInsert();
    }
}
