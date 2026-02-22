package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.PrenatalCheckup;
import com.example.maternal.mapper.PrenatalCheckupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkup")
@CrossOrigin(origins = "*")
public class PrenatalCheckupController {

    @Autowired
    private PrenatalCheckupMapper checkupMapper;

    // 获取列表 (按孕周从小到大排序，做时间轴用)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<PrenatalCheckup> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.orderByDesc("week_no"); // 最近的产检在最上面

        List<PrenatalCheckup> list = checkupMapper.selectList(query);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 新增产检记录
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody PrenatalCheckup checkup) {
        Map<String, Object> result = new HashMap<>();
        try {
            checkupMapper.insert(checkup);
            result.put("code", 200);
            result.put("msg", "记录成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "失败：" + e.getMessage());
        }
        return result;
    }
}