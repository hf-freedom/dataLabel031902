package com.example.todo.repository;

import com.example.todo.entity.User;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UserRepository {

    private final Map<String, User> userCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 初始化管理员
        User admin = new User("admin001", "admin", "admin123", User.Role.ADMIN);
        userCache.put(admin.getId(), admin);

        // 初始化3个普通用户
        User user1 = new User("user001", "zhangsan", "123456", User.Role.USER);
        User user2 = new User("user002", "lisi", "123456", User.Role.USER);
        User user3 = new User("user003", "wangwu", "123456", User.Role.USER);

        userCache.put(user1.getId(), user1);
        userCache.put(user2.getId(), user2);
        userCache.put(user3.getId(), user3);
    }

    public User findById(String id) {
        return userCache.get(id);
    }

    public User findByUsername(String username) {
        return userCache.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(userCache.values());
    }

    public List<User> findAllUsers() {
        return userCache.values().stream()
                .filter(user -> user.getRole() == User.Role.USER)
                .collect(Collectors.toList());
    }

    public User save(User user) {
        userCache.put(user.getId(), user);
        return user;
    }

    public void deleteById(String id) {
        userCache.remove(id);
    }

    public boolean existsById(String id) {
        return userCache.containsKey(id);
    }
}
