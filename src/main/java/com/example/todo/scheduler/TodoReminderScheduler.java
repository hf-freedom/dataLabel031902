package com.example.todo.scheduler;

import com.example.todo.entity.Todo;
import com.example.todo.service.TodoService;
import com.example.todo.util.ISendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class TodoReminderScheduler {

    @Autowired
    private TodoService todoService;

    @Autowired
    private ISendUtil sendUtil;

    /**
     * 每分钟检查一次即将到期的代办
     * 检查距离到期时间小于等于1小时且未发送过提醒的代办
     */
    @Scheduled(fixedRate = 60000)
    public void checkDueTodos() {
        List<Todo> pendingTodos = todoService.getPendingTodos();
        LocalDateTime now = LocalDateTime.now();

        for (Todo todo : pendingTodos) {
            if (todo.isNotified()) {
                continue;
            }

            LocalDateTime dueTime = todo.getDueTime();
            if (dueTime == null) {
                continue;
            }

            // 计算距离到期的时间差（分钟）
            long minutesUntilDue = ChronoUnit.MINUTES.between(now, dueTime);

            // 如果距离到期时间在0到60分钟之间，发送提醒
            if (minutesUntilDue >= 0 && minutesUntilDue <= 60) {
                sendUtil.sendTodoReminder(todo);
                todoService.markAsNotified(todo.getId());
            }
        }
    }
}
