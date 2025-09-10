package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    @EntityGraph(attributePaths = { "user" })
    @Query("SELECT t FROM Todo t " +
            "WHERE t.modifiedAt >= :startDate AND t.modifiedAt < :endDate " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> searchBetweenDates(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @EntityGraph(attributePaths = { "user" })
    @Query("SELECT t FROM Todo t " +
            "WHERE t.modifiedAt >= :startDate AND t.modifiedAt < :endDate " +
            "AND t.weather LIKE :weather " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> searchWithWeatherBetweenDates(@Param("weather") String weather,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
