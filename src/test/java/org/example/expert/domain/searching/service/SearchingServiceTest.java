package org.example.expert.domain.searching.service;

import org.example.expert.domain.searching.mapper.SearchingMapper;
import org.example.expert.domain.searching.repository.SearchTodoQuery;
import org.example.expert.domain.searching.repository.SearchingRepository;
import org.example.expert.domain.searching.repository.TodoSummaryProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchingServiceTest {

    @Mock
    private SearchingRepository searchingRepository;

    @Mock
    private SearchingMapper searchingMapper;

    @InjectMocks
    private SearchingService searchingService;

    @Test
    void searchTodos_shouldReturnPageOfTodoSummaryProjection() {
        // Given
        String title = "Test Title";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String managerNickname = "Test Manager";
        Pageable pageable = PageRequest.of(0, 10);

        SearchTodoCommand command = SearchTodoCommand.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .managerNickname(managerNickname)
                .pageable(pageable)
                .build();

        SearchTodoQuery query = SearchTodoQuery.builder()
                .title(title)
                .startDate(startDate)
                .endDate(endDate)
                .managerNickname(managerNickname)
                .pageable(pageable)
                .build();

        TodoSummaryProjection projection = new TodoSummaryProjection(1L, "Todo Title", 1L, 5L);
        Page<TodoSummaryProjection> expectedPage = new PageImpl<>(List.of(projection), pageable, 1);

        when(searchingMapper.toSearchTodoQuery(command)).thenReturn(query);
        when(searchingRepository.searchTodos(query)).thenReturn(expectedPage);

        // When
        Page<TodoSummaryProjection> result = searchingService.searchTodos(command);

        // Then
        assertThat(result).isEqualTo(expectedPage);
        verify(searchingMapper).toSearchTodoQuery(command);
        verify(searchingRepository).searchTodos(query);
    }
}
