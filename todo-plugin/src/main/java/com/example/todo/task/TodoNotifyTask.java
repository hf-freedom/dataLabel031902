package com.example.todo.task;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.util.ISendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TodoNotifyTask {
    @Autowired
    private TodoService todoService;

    @Autowired
    private ISendUtil sendUtil;

    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void notifyExpiringTodos() {
        List<Todo> todos = todoService.getTodosNeedNotify();
        for (Todo todo : todos) {
            String msg = String.format("代办提醒：%s 将在1小时内到期，请及时处理！", todo.getTitle());
            sendUtil.send(msg);
            todoService.markAsNotified(todo.getId());
        }
    }
}
