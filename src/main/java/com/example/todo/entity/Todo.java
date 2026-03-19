package com.example.todo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private String id;
    private String title;
    private String content;
    private String creatorId;
    private String executorId;
    private Priority priority;
    private LocalDateTime createTime;
    private LocalDateTime dueTime;
    private Status status;
    private boolean notified;

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
