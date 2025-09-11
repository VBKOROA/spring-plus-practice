package org.example.expert.domain.searching.controller;

import org.example.expert.domain.searching.mapper.SearchingMapper;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.example.expert.domain.searching.service.SearchTodoCommand;
import org.example.expert.domain.searching.service.SearchingService;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class SearchingController {
    private final SearchingService searchingService;
    private final SearchingMapper searchingMapper;

    @PostMapping("/todos/search")
    public ResponseEntity<Page<SearchTodoResponse>> search(@RequestBody SearchTodoRequest request) {
        SearchTodoCommand command = searchingMapper.toSearchTodoCommand(request);
        Page<TodoSummaryProjection> result = searchingService.searchTodos(command);
        Page<SearchTodoResponse> response = result.map(searchingMapper::toSearchTodoResponse);
        return ResponseEntity.ok().body(response);
    }
}
