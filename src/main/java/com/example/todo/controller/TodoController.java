package com.example.todo.controller;

import com.example.todo.dto.Result;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<Todo> createTodo(
            @RequestParam String creatorId,
            @RequestParam String executorId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Todo.Priority priority,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dueTime) {
        try {
            Todo todo = todoService.createTodo(creatorId, executorId, title, content, priority, dueTime);
            return Result.success(todo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<Todo> updateTodo(
            @RequestParam String todoId,
            @RequestParam String userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Todo.Priority priority,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dueTime,
            @RequestParam(required = false) Todo.Status status) {
        try {
            Todo todo = todoService.updateTodo(todoId, userId, title, content, priority, dueTime, status);
            return Result.success(todo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result<Void> deleteTodo(@RequestParam String todoId, @RequestParam String userId) {
        try {
            todoService.deleteTodo(todoId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/get/{todoId}")
    public Result<Todo> getTodoById(@PathVariable String todoId) {
        Todo todo = todoService.getTodoById(todoId);
        if (todo == null) {
            return Result.error("代办不存在");
        }
        return Result.success(todo);
    }

    @GetMapping("/my/{userId}")
    public Result<List<Todo>> getMyTodos(@PathVariable String userId) {
        List<Todo> todos = todoService.getMyTodos(userId);
        return Result.success(todos);
    }

    @GetMapping("/executor/{executorId}")
    public Result<List<Todo>> getTodosByExecutor(@PathVariable String executorId) {
        List<Todo> todos = todoService.getTodosByExecutor(executorId);
        return Result.success(todos);
    }

    @GetMapping("/all")
    public Result<List<Todo>> getAllTodos(@RequestParam String userId) {
        // 验证是否为管理员
        if (!userService.isAdmin(userId)) {
            return Result.error("只有管理员可以查看所有代办");
        }
        List<Todo> todos = todoService.getAllTodos();
        return Result.success(todos);
    }

    @PostMapping("/updatePriority")
    public Result<Todo> updatePriority(
            @RequestParam String todoId,
            @RequestParam String userId,
            @RequestParam Todo.Priority priority) {
        try {
            Todo todo = todoService.updatePriority(todoId, userId, priority);
            return Result.success(todo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/push")
    public Result<Todo> pushTodoToUser(
            @RequestParam String adminId,
            @RequestParam String executorId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Todo.Priority priority,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dueTime) {
        // 验证是否为管理员
        if (!userService.isAdmin(adminId)) {
            return Result.error("只有管理员可以推送代办消息");
        }

        try {
            Todo todo = todoService.createTodo(adminId, executorId, title, content, priority, dueTime);
            return Result.success(todo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
