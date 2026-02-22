package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.PregnantProfile;
import com.example.maternal.entity.User;
import com.example.maternal.mapper.PregnantProfileMapper;
import com.example.maternal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PregnantProfileMapper profileMapper;

    // 1. 获取个人资料
    @GetMapping("/info")
    public Map<String, Object> getInfo(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.selectById(userId);
        user.setPassword(null);

        QueryWrapper<PregnantProfile> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        PregnantProfile profile = profileMapper.selectOne(query);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("profile", profile);

        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    // 2. 更新资料
    @PostMapping("/update")
    public Map<String, Object> updateInfo(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());

        User user = new User();
        user.setUserId(userId);
        if(params.get("realName") != null) user.setRealName(params.get("realName").toString());
        userMapper.updateById(user);

        QueryWrapper<PregnantProfile> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        PregnantProfile profile = profileMapper.selectOne(query);

        if(profile == null) {
            profile = new PregnantProfile();
            profile.setUserId(userId);
        }

        if(params.get("lmpDate") != null) profile.setLmpDate(java.time.LocalDate.parse(params.get("lmpDate").toString()));
        if(params.get("eddDate") != null) profile.setEddDate(java.time.LocalDate.parse(params.get("eddDate").toString()));
        if(params.get("preWeight") != null) profile.setPreWeight(Double.valueOf(params.get("preWeight").toString()));

        if(profile.getProfileId() == null) {
            profileMapper.insert(profile);
        } else {
            profileMapper.updateById(profile);
        }

        return Map.of("code", 200, "msg", "资料已更新");
    }

    // 3. 修改密码
    @PostMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        Long userId = Long.valueOf(params.get("userId").toString());
        String oldPass = params.get("oldPass").toString();
        String newPass = params.get("newPass").toString();

        User user = userMapper.selectById(userId);
        if (!user.getPassword().equals(oldPass)) {
            result.put("code", 400);
            result.put("msg", "原密码错误");
            return result;
        }

        user.setPassword(newPass);
        userMapper.updateById(user);

        result.put("code", 200);
        result.put("msg", "密码修改成功");
        return result;
    }

    // 4. [新增] 注销账号
    @PostMapping("/delete")
    public Map<String, Object> deleteAccount(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        userMapper.deleteById(userId); // 删用户

        QueryWrapper<PregnantProfile> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        profileMapper.delete(query); // 删档案

        return Map.of("code", 200, "msg", "账号已注销");
    }
}