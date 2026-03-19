package com.example.todo.service;

import com.example.todo.cache.LocalCache;
import com.example.todo.entity.Todo;
import com.example.todo.util.ISendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TodoService {
    @Autowired
    private LocalCache localCache;

    @Autowired
    private UserService userService;

    @Autowired
    private ISendUtil sendUtil;

    public List<Todo> getTodosByExecutor(String executor) {
        return localCache.getTodosByExecutor(executor);
    }

    public List<Todo> getTodosByCreator(String creator) {
        return localCache.getTodosByCreator(creator);
    }

    public Todo getTodoById(Long id) {
        return localCache.getTodoById(id);
    }

    public boolean createTodo(Todo todo, String currentUser) {
        todo.setCreator(currentUser);
        todo.setCreateTime(LocalDateTime.now());
        todo.setStatus(0);
        todo.setNotified(false);
        localCache.saveTodo(todo);
        return true;
    }

    public boolean updateTodo(Todo todo, String currentUser) {
        Todo existingTodo = localCache.getTodoById(todo.getId());
        if (existingTodo == null) {
            return false;
        }
        if (!existingTodo.getCreator().equals(currentUser) && !existingTodo.getExecutor().equals(currentUser)) {
            return false;
        }
        // 保留不可修改的字段
        todo.setCreator(existingTodo.getCreator());
        todo.setCreateTime(existingTodo.getCreateTime());
        todo.setNotified(existingTodo.getNotified());
        localCache.saveTodo(todo);
        return true;
    }

    public boolean deleteTodo(Long id, String currentUser) {
        Todo existingTodo = localCache.getTodoById(id);
        if (existingTodo == null) {
            return false;
        }
        if (!existingTodo.getCreator().equals(currentUser)) {
            return false;
        }
        return localCache.deleteTodo(id);
    }

    public boolean pushTodo(Todo todo, String adminUser) {
        if (!userService.isAdmin(adminUser)) {
            return false;
        }
        todo.setCreator(adminUser);
        todo.setCreateTime(LocalDateTime.now());
        todo.setStatus(0);
        todo.setNotified(false);
        localCache.saveTodo(todo);
        return true;
    }

    public boolean updatePriority(Long id, Integer priority, String currentUser) {
        Todo existingTodo = localCache.getTodoById(id);
        if (existingTodo == null) {
            return false;
        }
        if (!existingTodo.getCreator().equals(currentUser) && !existingTodo.getExecutor().equals(currentUser)) {
            return false;
        }
        existingTodo.setPriority(priority);
        localCache.saveTodo(existingTodo);
        return true;
    }

    public List<Todo> getTodosNeedNotify() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        List<Todo> result = new ArrayList<>();
        for (Todo todo : localCache.getAllTodos()) {
            if (todo.getStatus() == 0 && !todo.getNotified() &&
                    todo.getExpireTime() != null &&
                    todo.getExpireTime().isAfter(now) &&
                    todo.getExpireTime().isBefore(oneHourLater)) {
                result.add(todo);
            }
        }
        return result;
    }

    public void markAsNotified(Long id) {
        Todo todo = localCache.getTodoById(id);
        if (todo != null) {
            todo.setNotified(true);
            localCache.saveTodo(todo);
        }
    }
}
