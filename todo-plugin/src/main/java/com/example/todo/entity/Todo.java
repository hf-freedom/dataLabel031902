package com.example.todo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Todo {
    private Long id;
    private String title;
    private String content;
    private String creator;
    private String executor;
    private Integer priority; // 1:高, 2:中, 3:低
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    private Integer status; // 0:待处理, 1:进行中, 2:已完成, 3:已过期
    private Boolean notified; // 是否已发送提醒
}
