package com.example.todo.util;

import org.springframework.stereotype.Component;

@Component
public class SendUtilImpl implements ISendUtil {
    @Override
    public void send(String msg) {
        // 可以为空实现，这里简单打印日志
        System.out.println("发送消息: " + msg);
    }
}
