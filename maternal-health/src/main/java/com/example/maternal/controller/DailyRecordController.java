package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.DailyRecord;
import com.example.maternal.mapper.DailyRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
@CrossOrigin(origins = "*")
public class DailyRecordController {

    @Autowired
    private DailyRecordMapper dailyRecordMapper;

    /**
     * 智能打卡接口 (新增或更新)
     * 逻辑：每天只能有一条记录，重复提交会覆盖旧的
     */
    @PostMapping("/add")
    public Map<String, Object> addRecord(@RequestBody DailyRecord record) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = record.getUserId();
            LocalDate today = LocalDate.now();

            // 1. 先查询该用户今天是否已经打过卡
            QueryWrapper<DailyRecord> query = new QueryWrapper<>();
            query.eq("user_id", userId);
            query.eq("record_date", today);
            DailyRecord existingRecord = dailyRecordMapper.selectOne(query);

            if (existingRecord != null) {
                // --- 情况A: 今天已打卡 -> 执行更新 ---
                record.setRecordId(existingRecord.getRecordId()); // 使用旧的ID，保证是同一条数据
                record.setRecordDate(today); // 确保日期正确
                dailyRecordMapper.updateById(record);

                result.put("code", 200);
                result.put("msg", "今日数据已更新！"); // 提示语变化
            } else {
                // --- 情况B: 今天未打卡 -> 执行新增 ---
                record.setRecordDate(today);
                dailyRecordMapper.insert(record);

                result.put("code", 200);
                result.put("msg", "打卡成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户今日的打卡详情 (用于回显)
     */
    @GetMapping("/today")
    public Map<String, Object> getTodayRecord(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<DailyRecord> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.eq("record_date", LocalDate.now());
        DailyRecord record = dailyRecordMapper.selectOne(query);

        if (record != null) {
            result.put("code", 200);
            result.put("data", record); // 返回刚才填过的数据
            result.put("hasRecord", true);
        } else {
            result.put("code", 200);
            result.put("hasRecord", false);
        }
        return result;
    }

    /**
     * 获取历史列表 (用于画图)
     */
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<DailyRecord> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.orderByAsc("record_date");
        // 限制只取最近 30 天的数据，避免图表太挤
        query.last("LIMIT 30");

        List<DailyRecord> list = dailyRecordMapper.selectList(query);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
}