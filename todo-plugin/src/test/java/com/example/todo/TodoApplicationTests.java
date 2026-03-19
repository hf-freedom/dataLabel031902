package com.example.todo;

import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @Test
    void testUserInitialization() {
        List<User> users = userService.getAllUsers();
        assertEquals(4, users.size()); // 1 admin + 3 users

        User admin = userService.getUserByUsername("admin");
        assertNotNull(admin);
        assertEquals("ADMIN", admin.getRole());

        User user1 = userService.getUserByUsername("user1");
        assertNotNull(user1);
        assertEquals("USER", user1.getRole());
    }

    @Test
    void testCreateTodo() {
        Todo todo = new Todo();
        todo.setTitle("测试代办");
        todo.setContent("测试内容");
        todo.setExecutor("user1");
        todo.setPriority(1);
        todo.setExpireTime(LocalDateTime.now().plusHours(2));

        boolean success = todoService.createTodo(todo, "user1");
        assertTrue(success);

        List<Todo> todos = todoService.getTodosByCreator("user1");
        assertEquals(1, todos.size());
    }

    @Test
    void testAdminPushTodo() {
        Todo todo = new Todo();
        todo.setTitle("管理员推送的代办");
        todo.setContent("管理员内容");
        todo.setExecutor("user2");
        todo.setPriority(2);
        todo.setExpireTime(LocalDateTime.now().plusHours(3));

        boolean success = todoService.pushTodo(todo, "admin");
        assertTrue(success);

        List<Todo> todos = todoService.getTodosByExecutor("user2");
        assertEquals(1, todos.size());
    }

    @Test
    void testUpdatePriority() {
        Todo todo = new Todo();
        todo.setTitle("优先级测试");
        todo.setContent("测试内容");
        todo.setExecutor("user3");
        todo.setPriority(3);
        todo.setExpireTime(LocalDateTime.now().plusHours(4));

        todoService.createTodo(todo, "user3");
        List<Todo> todos = todoService.getTodosByCreator("user3");
        assertFalse(todos.isEmpty());

        Long todoId = todos.get(0).getId();
        boolean success = todoService.updatePriority(todoId, 1, "user3");
        assertTrue(success);

        Todo updatedTodo = todoService.getTodoById(todoId);
        assertEquals(1, updatedTodo.getPriority());
    }
}
