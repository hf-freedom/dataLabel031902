package com.example.todo.config;

import com.example.todo.cache.LocalCache;
import com.example.todo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private LocalCache localCache;

    @Override
    public void run(String... args) {
        // 初始化管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole("ADMIN");
        localCache.saveUser(admin);

        // 初始化3个普通用户
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setRole("USER");
            localCache.saveUser(user);
        }

        System.out.println("数据初始化完成：1个管理员，3个普通用户");
    }
}
