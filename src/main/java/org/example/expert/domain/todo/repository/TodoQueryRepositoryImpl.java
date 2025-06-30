package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchCondition;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoQueryRepositoryImpl implements TodoQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(TodoSearchCondition condition, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;

        List<TodoSearchResponse> contents = queryFactory
            .select(new QTodoSearchResponse(
                todo.title,
                manager.id.countDistinct(),
                comment.id.countDistinct()
            ))
            .from(todo)
            .leftJoin(todo.user, user)
            .leftJoin(manager).on(manager.todo.eq(todo))
            .leftJoin(comment).on(comment.todo.eq(todo))
            .where(
                condition.getTitleKeyword() != null && !condition.getTitleKeyword().isBlank()
                    ? todo.title.containsIgnoreCase(condition.getTitleKeyword()) : null,

                condition.getNickname() != null && !condition.getNickname().isBlank()
                    ? user.nickname.containsIgnoreCase(condition.getNickname()) : null,

                condition.getStartDate() != null && condition.getEndDate() != null
                    ? todo.createdAt.between(condition.getStartDate(), condition.getEndDate())
                    : condition.getStartDate() != null
                        ? todo.createdAt.goe(condition.getStartDate())
                        : condition.getEndDate() != null
                            ? todo.createdAt.loe(condition.getEndDate())
                            : null
            )
            .groupBy(todo.id)
            .orderBy(todo.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = queryFactory
            .select(todo.countDistinct())
            .from(todo)
            .leftJoin(todo.user, user)
            .where(
                condition.getTitleKeyword() != null && !condition.getTitleKeyword().isBlank()
                    ? todo.title.containsIgnoreCase(condition.getTitleKeyword()) : null,

                condition.getNickname() != null && !condition.getNickname().isBlank()
                    ? user.nickname.containsIgnoreCase(condition.getNickname()) : null,

                condition.getStartDate() != null && condition.getEndDate() != null
                    ? todo.createdAt.between(condition.getStartDate(), condition.getEndDate())
                    : condition.getStartDate() != null
                        ? todo.createdAt.goe(condition.getStartDate())
                        : condition.getEndDate() != null
                            ? todo.createdAt.loe(condition.getEndDate())
                            : null
            )
            .fetchOne();

        return new PageImpl<>(contents, pageable, total != null ? total : 0L);
    }
}
