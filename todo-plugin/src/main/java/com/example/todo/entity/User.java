package com.example.todo.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String role; // ADMIN, USER
}
