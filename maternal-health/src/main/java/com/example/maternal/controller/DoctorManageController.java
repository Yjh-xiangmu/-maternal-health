package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.DoctorInfo;
import com.example.maternal.entity.User;
import com.example.maternal.mapper.DoctorInfoMapper;
import com.example.maternal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/doctor")
@CrossOrigin(origins = "*")
public class DoctorManageController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DoctorInfoMapper doctorInfoMapper;

    // 1. 列表
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.eq("role", 1);
        List<User> userList = userMapper.selectList(userQuery);

        List<Map<String, Object>> list = new ArrayList<>();
        for (User u : userList) {
            DoctorInfo info = doctorInfoMapper.selectById(u.getUserId());
            Map<String, Object> item = new HashMap<>();
            item.put("userId", u.getUserId());
            item.put("realName", u.getRealName());
            item.put("username", u.getUsername());
            item.put("status", u.getStatus());

            if (info != null) {
                item.put("title", info.getTitle());
                item.put("department", info.getDepartment());
                item.put("consultCount", info.getConsultCount());
                item.put("introduction", info.getIntroduction()); // 详情需要
            } else {
                item.put("title", "暂无");
                item.put("department", "暂无");
            }
            list.add(item);
        }
        result.put("code", 0);
        result.put("count", list.size());
        result.put("data", list);
        return result;
    }

    // 2. 新增
    @PostMapping("/add")
    @Transactional
    public Map<String, Object> addDoctor(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String phone = (String) params.get("username");
            String realName = (String) params.get("realName");

            QueryWrapper<User> check = new QueryWrapper<>();
            check.eq("username", phone);
            if (userMapper.selectCount(check) > 0) {
                result.put("code", 400);
                result.put("msg", "该手机号已存在");
                return result;
            }

            User user = new User();
            user.setUsername(phone);
            user.setRealName(realName);
            user.setPassword("123456");
            user.setRole(1);
            user.setStatus(1);
            user.setCreateTime(LocalDateTime.now());
            user.setAvatar("https://cdn-icons-png.flaticon.com/512/3304/3304567.png");
            userMapper.insert(user);

            DoctorInfo info = new DoctorInfo();
            info.setDoctorId(user.getUserId());
            info.setTitle((String) params.get("title"));
            info.setDepartment((String) params.get("department"));
            info.setJobNumber("DOC" + System.currentTimeMillis());
            info.setIntroduction((String) params.get("introduction"));
            info.setConsultCount(0);
            doctorInfoMapper.insert(info);

            result.put("code", 200);
            result.put("msg", "添加成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "系统错误：" + e.getMessage());
        }
        return result;
    }

    // 3. [新增] 编辑
    @PostMapping("/update")
    @Transactional
    public Map<String, Object> updateDoctor(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());

        User user = new User();
        user.setUserId(userId);
        if(params.get("realName") != null) user.setRealName(params.get("realName").toString());
        userMapper.updateById(user);

        DoctorInfo info = new DoctorInfo();
        info.setDoctorId(userId);
        if(params.get("title") != null) info.setTitle(params.get("title").toString());
        if(params.get("department") != null) info.setDepartment(params.get("department").toString());
        if(params.get("introduction") != null) info.setIntroduction(params.get("introduction").toString());

        doctorInfoMapper.updateById(info);
        return Map.of("code", 200, "msg", "更新成功");
    }

    // 4. [新增] 删除
    @PostMapping("/delete")
    @Transactional
    public Map<String, Object> deleteDoctor(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        userMapper.deleteById(userId);
        doctorInfoMapper.deleteById(userId);
        return Map.of("code", 200, "msg", "删除成功");
    }

    // 5. [新增] 状态切换
    @PostMapping("/status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());

        User user = new User();
        user.setUserId(userId);
        user.setStatus(status);
        userMapper.updateById(user);

        return Map.of("code", 200, "msg", "状态已更新");
    }

    // 6. 简单列表
    @GetMapping("/simple-list")
    public Map<String, Object> getSimpleList() {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("role", 1);
        query.eq("status", 1);
        query.select("user_id", "real_name", "username");
        List<User> list = userMapper.selectList(query);
        return Map.of("code", 200, "data", list);
    }
}