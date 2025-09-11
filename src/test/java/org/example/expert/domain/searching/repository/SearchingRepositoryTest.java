package org.example.expert.domain.searching.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import org.example.expert.config.PersistenceConfig;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PersistenceConfig.class)
class SearchingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private SearchingRepository searchingRepository;

    private User user1;
    private User user2;
    private Todo todo1;
    private Todo todo2;
    private Todo todo3;

    @BeforeEach
    void setUp() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager.getEntityManager());
        searchingRepository = new SearchingRepository(queryFactory);

        user1 = new User("user1@example.com", "john", "password1", UserRole.USER);
        entityManager.persist(user1);

        user2 = new User("user2@example.com", "mike", "password2", UserRole.USER);
        entityManager.persist(user2);

        todo1 = new Todo("Spring Boot Project", "Spring Boot project description", "Sunny", user1);
        entityManager.persist(todo1);

        todo2 = new Todo("React Application", "React app development", "Cloudy", user2);
        entityManager.persist(todo2);

        todo3 = new Todo("Database Design", "Database schema design", "Rainy", user1);
        entityManager.persist(todo3);

        Comment comment1 = new Comment("Great project!", user1, todo1);
        entityManager.persist(comment1);

        Comment comment2 = new Comment("Nice work!", user2, todo1);
        entityManager.persist(comment2);

        Comment comment3 = new Comment("Good job!", user1, todo2);
        entityManager.persist(comment3);

        entityManager.flush();
    }

    @Test
    void searchTodos_withEmptyCriteria_returnsAllTodos() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void searchTodos_withTitleFilter_returnsMatchingTodos() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .title("Spring")
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).todoTitle()).contains("Spring");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void searchTodos_withDateRangeFilter_returnsTodosInDateRange() {
        // given
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now().plusYears(1);
        
        SearchTodoQuery query = SearchTodoQuery.builder()
                .startDate(startDate)
                .endDate(endDate)
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void searchTodos_withManagerNicknameFilter_returnsTodosWithMatchingManager() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .managerNickname("john")
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(2); 
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void searchTodos_withCombinedFilters_returnsMatchingTodos() {
        // given
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now().plusYears(1);
        
        SearchTodoQuery query = SearchTodoQuery.builder()
                .title("Project")
                .startDate(startDate)
                .endDate(endDate)
                .managerNickname("john")
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).todoTitle()).contains("Project");
    }

    @Test
    void searchTodos_withPagination_returnsCorrectPage() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .pageable(PageRequest.of(0, 2))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void searchTodos_withNullFilters_returnsAllTodos() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .title(null)
                .startDate(null)
                .endDate(null)
                .managerNickname(null)
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void searchTodos_withEmptyStringFilters_returnsAllTodos() {
        // given
        SearchTodoQuery query = SearchTodoQuery.builder()
                .title("")
                .managerNickname("")
                .pageable(PageRequest.of(0, 10))
                .build();

        // when
        Page<TodoSummaryProjection> result = searchingRepository.searchTodos(query);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}