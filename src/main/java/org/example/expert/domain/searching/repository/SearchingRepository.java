package org.example.expert.domain.searching.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SearchingRepository {
    private final JPAQueryFactory queryFactory;

    public Page<TodoSummaryProjection> searchTodos(SearchTodoQuery query) {
        QTodo todo = QTodo.todo;
        QManager managers = QManager.manager;
        QComment comments = QComment.comment;
        QUser managerInfo = QUser.user;

        BooleanExpression whereClause = Expressions.TRUE.and(titleLike(todo, query.title()))
                .and(dateBetween(todo, query.startDate(), query.endDate()))
                .and(managerNicknameLike(managerInfo, query.managerNickname()));

        List<TodoSummaryProjection> result = queryFactory
                .select(Projections.constructor(TodoSummaryProjection.class,
                        todo.id,
                        todo.title,
                        managers.countDistinct(),
                        comments.countDistinct()))
                .from(todo).leftJoin(todo.managers, managers).leftJoin(todo.comments, comments)
                .leftJoin(managers.user, managerInfo)
                .where(whereClause)
                .groupBy(todo.id, todo.title)
                .orderBy(todo.createdAt.desc())
                .offset(query.pageable().getOffset())
                .limit(query.pageable().getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(todo.id.countDistinct())
                .from(todo).leftJoin(todo.managers, managers).leftJoin(todo.comments, comments)
                .leftJoin(managers.user, managerInfo)
                .where(whereClause);

        return PageableExecutionUtils.getPage(result, query.pageable(), countQuery::fetchOne);
    }

    private BooleanExpression titleLike(QTodo todo, String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return todo.title.like(asLikeString(title));
    }

    private BooleanExpression managerNicknameLike(QUser managerInfo, String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return null;
        }
        return managerInfo.nickname.like(asLikeString(nickname));
    }

    private BooleanBuilder dateBetween(QTodo todo, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        BooleanBuilder eq = new BooleanBuilder();

        if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            eq.and(todo.createdAt.goe(startDateTime));
        }

        if (endDate != null) {
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
            eq.and(todo.createdAt.lt(endDateTime));
        }

        return eq;
    }

    private String asLikeString(String str) {
        return "%" + str + "%";
    }
}
