package com.example.todo.repository;

import com.example.todo.entity.Todo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TodoRepository {

    private final Map<String, Todo> todoCache = new ConcurrentHashMap<>();

    public Todo findById(String id) {
        return todoCache.get(id);
    }

    public List<Todo> findAll() {
        return new ArrayList<>(todoCache.values());
    }

    public List<Todo> findByCreatorId(String creatorId) {
        return todoCache.values().stream()
                .filter(todo -> todo.getCreatorId().equals(creatorId))
                .collect(Collectors.toList());
    }

    public List<Todo> findByExecutorId(String executorId) {
        return todoCache.values().stream()
                .filter(todo -> todo.getExecutorId().equals(executorId))
                .collect(Collectors.toList());
    }

    public List<Todo> findByStatus(Todo.Status status) {
        return todoCache.values().stream()
                .filter(todo -> todo.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Todo> findPendingTodos() {
        return todoCache.values().stream()
                .filter(todo -> todo.getStatus() == Todo.Status.PENDING || todo.getStatus() == Todo.Status.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    public Todo save(Todo todo) {
        todoCache.put(todo.getId(), todo);
        return todo;
    }

    public void deleteById(String id) {
        todoCache.remove(id);
    }

    public boolean existsById(String id) {
        return todoCache.containsKey(id);
    }
}
