package com.example.todo.util;

import com.example.todo.entity.Todo;

public interface ISendUtil {

    void send(String msg);

    void sendTodoReminder(Todo todo);
}
