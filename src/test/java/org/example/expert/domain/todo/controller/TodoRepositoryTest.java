package org.example.expert.domain.todo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.expert.config.QueryDslConfig;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@DisplayName("Repository:Todo")
@Import(QueryDslConfig.class)
class TodoRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    private User user;
    private Todo todo;

    @BeforeEach
    void setUp() {
        user = new User("hong-gd@gmail.com", "password", UserRole.USER);
        userRepository.save(user);

        todo = new Todo("todo title", "todo contents", "sunny", user);
        todoRepository.save(todo);
    }

    @Test
    @DisplayName("QueryDsl로 전환한 findByIdWithUser의 동작 검증")
    void findByIdWithUser() {
        // Given
        Long todoId = todo.getId();

        // When
        Todo todo = todoRepository.findByIdWithUser(todoId).orElseThrow();

        // Then
        assertThat(todo).isNotNull();
    }
}
