package com.example.todo.service;

import com.example.todo.cache.LocalCache;
import com.example.todo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private LocalCache localCache;

    public List<User> getAllUsers() {
        return localCache.getAllUsers();
    }

    public User getUserByUsername(String username) {
        return localCache.getUserByUsername(username);
    }

    public List<User> getNormalUsers() {
        return localCache.getUsersByRole("USER");
    }

    public boolean isAdmin(String username) {
        User user = getUserByUsername(username);
        return user != null && "ADMIN".equals(user.getRole());
    }
}
