package org.example.expert.domain.searching.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SearchingRepository {
    private final JPAQueryFactory queryFactory;

    public Page<UserSummaryProjection> searchUsers(String nickname, Pageable pageable) {
        QUser user = QUser.user;
        List<UserSummaryProjection> contents = queryFactory
                .select(Projections.constructor(
                        UserSummaryProjection.class,
                        user.id,
                        user.nickname,
                        user.profile))
                .from(user)
                .where(user.nickname.eq(nickname))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(user.nickname.eq(nickname));

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    public Page<TodoSummaryProjection> searchTodos(SearchTodoQuery query) {

        QTodo todo = QTodo.todo;
        QComment comment = QComment.comment;
        QManager manager = QManager.manager;

        BooleanExpression whereClause = Expressions.TRUE.and(titleLike(todo, query.title()))
                .and(dateBetween(todo, query.startDate(), query.endDate()))
                .and(managerNicknameLike(todo, query.managerNickname()));

        List<TodoSummaryProjection> result = queryFactory
                .select(Projections.constructor(TodoSummaryProjection.class,
                        todo.id,
                        todo.title,
                        JPAExpressions.select(manager.id.countDistinct())
                            .from(manager)
                            .where(manager.todo.eq(todo)),
                        JPAExpressions.select(comment.id.countDistinct())
                            .from(comment)
                            .where(comment.todo.eq(todo))))
                .from(todo)
                .where(whereClause)
                .groupBy(todo.id, todo.title)
                .orderBy(todo.createdAt.desc())
                .offset(query.pageable().getOffset())
                .limit(query.pageable().getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .where(whereClause);

        return PageableExecutionUtils.getPage(result, query.pageable(), countQuery::fetchOne);
    }

    private BooleanExpression titleLike(QTodo todo, String title) {
        if (StringUtils.hasText(title) == false) {
            return null;
        }
        return todo.title.like(asLikeString(title));
    }

    private BooleanExpression managerNicknameLike(QTodo todo, String nickname) {
        QManager manager = QManager.manager;

        if (StringUtils.hasText(nickname) == false) {
            return null;
        }

        return queryFactory.selectFrom(manager)
                .where(manager.todo.eq(todo)
                        .and(manager.user.nickname.like(asLikeString(nickname))))
                .exists();
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