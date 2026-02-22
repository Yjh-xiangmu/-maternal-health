package com.example.maternal.controller;

import com.example.maternal.entity.User;
import com.example.maternal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    // 注册接口
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = userService.registerPregnant(user);
            if ("success".equals(msg)) {
                result.put("code", 200);
                result.put("msg", "注册成功");
            } else {
                result.put("code", 400);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统错误：" + e.getMessage());
        }
        return result;
    }

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        // ★ 这里把 user.getRole() 传进去
        User loginUser = userService.login(user.getUsername(), user.getPassword(), user.getRole());

        if (loginUser != null) {
            if (loginUser.getStatus() == 0) {
                result.put("code", 403);
                result.put("msg", "账号已被禁用，请联系管理员");
            } else {
                result.put("code", 200);
                result.put("msg", "登录成功");
                result.put("data", loginUser);
            }
        } else {
            // 如果查不到，可能是账号密码错，也可能是角色选错了
            result.put("code", 401);
            result.put("msg", "登录失败：账号密码错误，或您的角色选择不正确");
        }
        return result;
    }
}