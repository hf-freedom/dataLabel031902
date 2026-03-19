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
class TodoPluginApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @Test
    void contextLoads() {
    }

    @Test
    void testUserLogin() {
        // 测试管理员登录
        User admin = userService.login("admin", "admin123");
        assertNotNull(admin);
        assertEquals(User.Role.ADMIN, admin.getRole());

        // 测试普通用户登录
        User user = userService.login("zhangsan", "123456");
        assertNotNull(user);
        assertEquals(User.Role.USER, user.getRole());

        // 测试错误密码
        assertThrows(RuntimeException.class, () -> userService.login("admin", "wrong"));
    }

    @Test
    void testCreateTodo() {
        String creatorId = "user001";
        String executorId = "user002";
        String title = "测试代办";
        String content = "测试内容";
        Todo.Priority priority = Todo.Priority.HIGH;
        LocalDateTime dueTime = LocalDateTime.now().plusDays(1);

        Todo todo = todoService.createTodo(creatorId, executorId, title, content, priority, dueTime);

        assertNotNull(todo);
        assertNotNull(todo.getId());
        assertEquals(creatorId, todo.getCreatorId());
        assertEquals(executorId, todo.getExecutorId());
        assertEquals(title, todo.getTitle());
        assertEquals(priority, todo.getPriority());
        assertEquals(Todo.Status.PENDING, todo.getStatus());
    }

    @Test
    void testUpdateTodo() {
        // 先创建
        String creatorId = "user001";
        String executorId = "user002";
        Todo todo = todoService.createTodo(creatorId, executorId, "原标题", "原内容",
                Todo.Priority.LOW, LocalDateTime.now().plusDays(1));

        // 再更新
        Todo updated = todoService.updateTodo(todo.getId(), creatorId, "新标题", "新内容",
                Todo.Priority.HIGH, null, Todo.Status.IN_PROGRESS);

        assertEquals("新标题", updated.getTitle());
        assertEquals("新内容", updated.getContent());
        assertEquals(Todo.Priority.HIGH, updated.getPriority());
        assertEquals(Todo.Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    void testUpdatePriority() {
        String creatorId = "user001";
        Todo todo = todoService.createTodo(creatorId, "user002", "测试", "内容",
                Todo.Priority.LOW, LocalDateTime.now().plusDays(1));

        Todo updated = todoService.updatePriority(todo.getId(), creatorId, Todo.Priority.URGENT);
        assertEquals(Todo.Priority.URGENT, updated.getPriority());
    }

    @Test
    void testDeleteTodo() {
        String creatorId = "user001";
        Todo todo = todoService.createTodo(creatorId, "user002", "待删除", "内容",
                Todo.Priority.LOW, LocalDateTime.now().plusDays(1));

        String todoId = todo.getId();
        todoService.deleteTodo(todoId, creatorId);

        assertNull(todoService.getTodoById(todoId));
    }

    @Test
    void testGetMyTodos() {
        String creatorId = "user001";
        todoService.createTodo(creatorId, "user002", "代办1", "内容1",
                Todo.Priority.LOW, LocalDateTime.now().plusDays(1));
        todoService.createTodo(creatorId, "user003", "代办2", "内容2",
                Todo.Priority.MEDIUM, LocalDateTime.now().plusDays(2));

        List<Todo> todos = todoService.getMyTodos(creatorId);
        assertTrue(todos.size() >= 2);
    }

    @Test
    void testAdminPushTodo() {
        // 验证管理员权限
        assertTrue(userService.isAdmin("admin001"));

        // 管理员推送代办
        Todo todo = todoService.createTodo("admin001", "user001", "管理员推送", "请处理",
                Todo.Priority.HIGH, LocalDateTime.now().plusHours(2));

        assertNotNull(todo);
        assertEquals("admin001", todo.getCreatorId());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertEquals(4, users.size()); // 1个管理员 + 3个普通用户
    }

    @Test
    void testGetNormalUsers() {
        List<User> users = userService.getAllNormalUsers();
        assertEquals(3, users.size());
        for (User user : users) {
            assertEquals(User.Role.USER, user.getRole());
        }
    }

    @Test
    void testPermissionCheck() {
        String creatorId = "user001";
        String otherUserId = "user002";

        Todo todo = todoService.createTodo(creatorId, "user003", "测试权限", "内容",
                Todo.Priority.LOW, LocalDateTime.now().plusDays(1));

        // 其他用户尝试修改应该失败
        assertThrows(RuntimeException.class, () ->
                todoService.updateTodo(todo.getId(), otherUserId, "新标题", null, null, null, null));

        // 其他用户尝试删除应该失败
        assertThrows(RuntimeException.class, () ->
                todoService.deleteTodo(todo.getId(), otherUserId));
    }
}
