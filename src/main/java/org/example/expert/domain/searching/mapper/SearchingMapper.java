package org.example.expert.domain.searching.mapper;

import org.example.expert.domain.searching.controller.SearchTodoRequest;
import org.example.expert.domain.searching.controller.SearchTodoResponse;
import org.example.expert.domain.searching.controller.SearchUserRequest;
import org.example.expert.domain.searching.controller.SearchUserResponse;
import org.example.expert.domain.searching.repository.SearchTodoQuery;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.example.expert.domain.searching.repository.UserSummaryProjection;
import org.example.expert.domain.searching.service.SearchTodoCommand;
import org.example.expert.domain.searching.service.SearchUserCommand;
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

        Pageable pageable = safePageable(request.page(), request.size());

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

    public SearchUserResponse toSearchUserResponse(UserSummaryProjection projection) {
        return new SearchUserResponse(projection.id(), projection.nickname(), projection.profile());
    }

    private boolean isNullOrNegative(Integer value) {
        return value == null || value < 0;
    }

    public SearchUserCommand toSearchUserCommand(SearchUserRequest request) {

        Pageable pageable = safePageable(request.page(), request.size());

        return new SearchUserCommand(request.nickname(), pageable);
    }

    private Pageable safePageable(Integer page, Integer size) {
        Integer safePage = page;
        Integer safeSize = size;

        if(isNullOrNegative(safePage)) {
            safePage = DEFAULT_PAGE;
        }

        if(isNullOrNegative(safeSize)) {
            safeSize = DEFAULT_SIZE;
        }

        return PageRequest.of(safePage, safeSize);
    }
}
