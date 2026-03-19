package com.example.todo.service;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllNormalUsers() {
        return userRepository.findAllUsers();
    }

    public boolean isAdmin(String userId) {
        User user = userRepository.findById(userId);
        return user != null && user.getRole() == User.Role.ADMIN;
    }
}
