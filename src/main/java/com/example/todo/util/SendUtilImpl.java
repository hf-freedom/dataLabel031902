package com.example.todo.util;

import com.example.todo.entity.Todo;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class SendUtilImpl implements ISendUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void send(String msg) {
        // 消息发送实现，目前为空
        System.out.println("[消息发送] " + msg);
    }

    @Override
    public void sendTodoReminder(Todo todo) {
        String msg = String.format("【代办提醒】代办任务即将到期！\n" +
                "标题: %s\n" +
                "内容: %s\n" +
                "执行人: %s\n" +
                "优先级: %s\n" +
                "到期时间: %s",
                todo.getTitle(),
                todo.getContent(),
                todo.getExecutorId(),
                todo.getPriority(),
                todo.getDueTime().format(FORMATTER));
        send(msg);
    }
}
