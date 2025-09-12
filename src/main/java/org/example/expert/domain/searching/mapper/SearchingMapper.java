package org.example.expert.domain.searching.mapper;

import org.example.expert.domain.searching.controller.SearchTodoRequest;
import org.example.expert.domain.searching.controller.SearchTodoResponse;
import org.example.expert.domain.searching.repository.SearchTodoQuery;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.example.expert.domain.searching.service.SearchTodoCommand;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class SearchingMapper {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;

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
        Integer safePage = request.page();
        Integer safeSize = request.size();

        if(isNullOrNegative(safePage)) {
            safePage = DEFAULT_PAGE;
        }

        if(isNullOrNegative(safeSize)) {
            safeSize = DEFAULT_SIZE;
        }

        Pageable pageable = PageRequest.of(safePage, safeSize);

        return SearchTodoCommand.builder()
                .endDate(request.endDate())
                .managerNickname(request.managerNickname())
                .pageable(pageable)
                .startDate(request.startDate())
                .title(request.title())
                .build();
    }

    public SearchTodoResponse toSearchTodoResponse(TodoSummaryProjection projection) {
        return new SearchTodoResponse(projection.todoTitle(), projection.managerCount(), projection.commentCount());
    }

    private boolean isNullOrNegative(Integer value) {
        return value == null || value < 0;
    }
}
