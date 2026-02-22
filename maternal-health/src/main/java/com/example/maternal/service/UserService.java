package com.example.maternal.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.User;
import com.example.maternal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 注册 (默认是孕妇 role=0)
    public String registerPregnant(User user) {
        QueryWrapper<User> check = new QueryWrapper<>();
        check.eq("username", user.getUsername());
        if (userMapper.selectCount(check) > 0) {
            return "该手机号已注册";
        }
        user.setRole(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        // 给个默认头像
        user.setAvatar("https://cdn-icons-png.flaticon.com/512/4603/4603681.png");

        userMapper.insert(user);
        return "success";
    }

    /**
     * ★★★ 修改后的登录逻辑：增加了 role 参数
     */
    public User login(String username, String password, Integer role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", password);
        queryWrapper.eq("role", role); // ★ 强制检查角色！

        return userMapper.selectOne(queryWrapper);
    }
}