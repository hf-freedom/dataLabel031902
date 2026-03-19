package com.example.todo.controller;

import com.example.todo.dto.Result;
import com.example.todo.entity.User;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.login(username, password);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/get/{userId}")
    public Result<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/all")
    public Result<List<User>> getAllUsers(@RequestParam String adminId) {
        // 验证是否为管理员
        if (!userService.isAdmin(adminId)) {
            return Result.error("只有管理员可以查看所有用户");
        }
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/list")
    public Result<List<User>> getAllNormalUsers(@RequestParam String userId) {
        // 验证用户是否存在
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        List<User> users = userService.getAllNormalUsers();
        return Result.success(users);
    }
}
