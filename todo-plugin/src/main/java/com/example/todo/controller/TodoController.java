package com.example.todo.controller;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping("/executor/{executor}")
    public ResponseEntity<List<Todo>> getTodosByExecutor(@PathVariable String executor) {
        return ResponseEntity.ok(todoService.getTodosByExecutor(executor));
    }

    @GetMapping("/creator/{creator}")
    public ResponseEntity<List<Todo>> getTodosByCreator(@PathVariable String creator) {
        return ResponseEntity.ok(todoService.getTodosByCreator(creator));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<Void> createTodo(@RequestBody Todo todo, @RequestParam String currentUser) {
        boolean success = todoService.createTodo(todo, currentUser);
        return success ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTodo(@PathVariable Long id, @RequestBody Todo todo, @RequestParam String currentUser) {
        todo.setId(id);
        boolean success = todoService.updateTodo(todo, currentUser);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, @RequestParam String currentUser) {
        boolean success = todoService.deleteTodo(id, currentUser);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/push")
    public ResponseEntity<Void> pushTodo(@RequestBody Todo todo, @RequestParam String adminUser) {
        boolean success = todoService.pushTodo(todo, adminUser);
        return success ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<Void> updatePriority(@PathVariable Long id, @RequestParam Integer priority, @RequestParam String currentUser) {
        boolean success = todoService.updatePriority(id, priority, currentUser);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
