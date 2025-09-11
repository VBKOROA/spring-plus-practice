package org.example.expert.domain.searching.service;

import org.example.expert.domain.searching.mapper.SearchingMapper;
import org.example.expert.domain.searching.repository.SearchTodoQuery;
import org.example.expert.domain.searching.repository.SearchingRepository;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchingService {
    private final SearchingRepository searchingRepository;
    private final SearchingMapper searchingMapper;

    public Page<TodoSummaryProjection> searchTodos(SearchTodoCommand command) {
        SearchTodoQuery query = searchingMapper.toSearchTodoQuery(command);
        return searchingRepository.searchTodos(query);
    }
}
