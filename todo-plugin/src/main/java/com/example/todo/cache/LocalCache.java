package com.example.todo.cache;

import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class LocalCache {
    // 用户缓存: username -> User
    private final Map<String, User> userCache = new ConcurrentHashMap<>();
    private final AtomicLong userIdGenerator = new AtomicLong(1);

    // 待办缓存: id -> Todo
    private final Map<Long, Todo> todoCache = new ConcurrentHashMap<>();
    private final AtomicLong todoIdGenerator = new AtomicLong(1);

    // ========== 用户缓存操作 ==========

    public void saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdGenerator.getAndIncrement());
        }
        userCache.put(user.getUsername(), user);
    }

    public User getUserByUsername(String username) {
        return userCache.get(username);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userCache.values());
    }

    public List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : userCache.values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    // ========== 待办缓存操作 ==========

    public void saveTodo(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(todoIdGenerator.getAndIncrement());
        }
        todoCache.put(todo.getId(), todo);
    }

    public Todo getTodoById(Long id) {
        return todoCache.get(id);
    }

    public List<Todo> getTodosByExecutor(String executor) {
        List<Todo> result = new ArrayList<>();
        for (Todo todo : todoCache.values()) {
            if (executor.equals(todo.getExecutor())) {
                result.add(todo);
            }
        }
        return result;
    }

    public List<Todo> getTodosByCreator(String creator) {
        List<Todo> result = new ArrayList<>();
        for (Todo todo : todoCache.values()) {
            if (creator.equals(todo.getCreator())) {
                result.add(todo);
            }
        }
        return result;
    }

    public boolean deleteTodo(Long id) {
        return todoCache.remove(id) != null;
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todoCache.values());
    }

    // ========== 清空缓存(测试用) ==========
    public void clearAll() {
        userCache.clear();
        todoCache.clear();
        userIdGenerator.set(1);
        todoIdGenerator.set(1);
    }
}
