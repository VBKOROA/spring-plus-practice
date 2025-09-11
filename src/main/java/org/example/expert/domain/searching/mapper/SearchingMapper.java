package org.example.expert.domain.searching.mapper;

import org.example.expert.domain.searching.controller.SearchTodoRequest;
import org.example.expert.domain.searching.controller.SearchTodoResponse;
import org.example.expert.domain.searching.repository.SearchTodoQuery;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.example.expert.domain.searching.service.SearchTodoCommand;
import org.springframework.stereotype.Component;

@Component
public class SearchingMapper {
    public SearchTodoQuery toSearchTodoQuery(SearchTodoCommand command) {
        return SearchTodoQuery.builder()
                .endDate(command.endDate())
                .managerNickname(command.managerNickname())
                .pageable(command.pageable())
                .startDate(command.startDate())
                .title(command.title())
                .build();
    }

    public SearchTodoCommand toSearchTodoCommand(SearchTodoRequest request) {
        return SearchTodoCommand.builder()
                .endDate(request.endDate())
                .managerNickname(request.managerNickname())
                .pageable(request.pageable())
                .startDate(request.startDate())
                .title(request.title())
                .build();
    }

    public SearchTodoResponse toSearchTodoResponse(TodoSummaryProjection projection) {
        return new SearchTodoResponse(projection.todoTitle(), projection.managerCount(), projection.commentCount());
    }
}
