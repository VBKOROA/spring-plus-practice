package org.example.expert.domain.todo.repository;

import java.util.Optional;

import org.example.expert.domain.todo.dto.projection.GetTodoProjection;
import org.example.expert.domain.todo.dto.projection.SimpleUserProjection;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Optional<GetTodoProjection> findByIdWithUser(long todoId) {
        QTodo todo = QTodo.todo;
        QUser author = new QUser("author");

        GetTodoProjection result = jpaQueryFactory.select(Projections.constructor(GetTodoProjection.class,
                todo.id,
                todo.title,
                todo.contents,
                todo.weather,
                Projections.constructor(SimpleUserProjection.class, author.id, author.email, author.profile),
                todo.createdAt,
                todo.modifiedAt))
                .from(todo)
                .join(todo.user, author)
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
