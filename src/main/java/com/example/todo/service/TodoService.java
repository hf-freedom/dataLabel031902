package com.example.todo.service;

import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    public Todo createTodo(String creatorId, String executorId, String title, String content,
                          Todo.Priority priority, LocalDateTime dueTime) {
        User creator = userRepository.findById(creatorId);
        User executor = userRepository.findById(executorId);

        if (creator == null) {
            throw new RuntimeException("创建人不存在");
        }
        if (executor == null) {
            throw new RuntimeException("执行人不存在");
        }

        Todo todo = new Todo();
        todo.setId(UUID.randomUUID().toString().replace("-", ""));
        todo.setTitle(title);
        todo.setContent(content);
        todo.setCreatorId(creatorId);
        todo.setExecutorId(executorId);
        todo.setPriority(priority);
        todo.setCreateTime(LocalDateTime.now());
        todo.setDueTime(dueTime);
        todo.setStatus(Todo.Status.PENDING);
        todo.setNotified(false);

        return todoRepository.save(todo);
    }

    public Todo updateTodo(String todoId, String userId, String title, String content,
                          Todo.Priority priority, LocalDateTime dueTime, Todo.Status status) {
        Todo todo = todoRepository.findById(todoId);
        if (todo == null) {
            throw new RuntimeException("代办不存在");
        }

        // 只有创建人可以修改
        if (!todo.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有创建人可以修改代办");
        }

        if (title != null) {
            todo.setTitle(title);
        }
        if (content != null) {
            todo.setContent(content);
        }
        if (priority != null) {
            todo.setPriority(priority);
        }
        if (dueTime != null) {
            todo.setDueTime(dueTime);
        }
        if (status != null) {
            todo.setStatus(status);
        }

        return todoRepository.save(todo);
    }

    public void deleteTodo(String todoId, String userId) {
        Todo todo = todoRepository.findById(todoId);
        if (todo == null) {
            throw new RuntimeException("代办不存在");
        }

        // 只有创建人可以删除
        if (!todo.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有创建人可以删除代办");
        }

        todoRepository.deleteById(todoId);
    }

    public Todo getTodoById(String todoId) {
        return todoRepository.findById(todoId);
    }

    public List<Todo> getMyTodos(String userId) {
        return todoRepository.findByCreatorId(userId);
    }

    public List<Todo> getTodosByExecutor(String executorId) {
        return todoRepository.findByExecutorId(executorId);
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo updatePriority(String todoId, String userId, Todo.Priority priority) {
        Todo todo = todoRepository.findById(todoId);
        if (todo == null) {
            throw new RuntimeException("代办不存在");
        }

        // 只有创建人可以修改优先级
        if (!todo.getCreatorId().equals(userId)) {
            throw new RuntimeException("只有创建人可以修改优先级");
        }

        todo.setPriority(priority);
        return todoRepository.save(todo);
    }

    public List<Todo> getPendingTodos() {
        return todoRepository.findPendingTodos();
    }

    public void markAsNotified(String todoId) {
        Todo todo = todoRepository.findById(todoId);
        if (todo != null) {
            todo.setNotified(true);
            todoRepository.save(todo);
        }
    }
}
