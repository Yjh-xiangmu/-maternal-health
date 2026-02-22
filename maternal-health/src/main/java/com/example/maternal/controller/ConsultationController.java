package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.ConsultationOrder;
import com.example.maternal.mapper.ConsultationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consult")
@CrossOrigin(origins = "*")
public class ConsultationController {

    @Autowired
    private ConsultationMapper consultationMapper;

    // 1. 孕妇发起提问 (反馈)
    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody ConsultationOrder order) {
        Map<String, Object> result = new HashMap<>();
        try {
            order.setStatus(0); // 默认待回复
            order.setCreateTime(LocalDateTime.now());

            // 如果前端传了 doctorId="" (空字符串)，手动设为 null
            if (order.getDoctorId() != null && order.getDoctorId() == 0) {
                order.setDoctorId(null);
            }

            consultationMapper.insert(order);

            result.put("code", 200);
            result.put("msg", "提交成功，请等待医生回复");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "提交失败：" + e.getMessage());
        }
        return result;
    }

    // 2. 医生获取工单列表 (升级版：看公共池 + 指定给我的)
    @GetMapping("/doctor/list")
    public Map<String, Object> getListForDoctor(@RequestParam(required = false) Integer status,
                                                @RequestParam Long doctorId) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<ConsultationOrder> query = new QueryWrapper<>();

        if (status != null) {
            query.eq("status", status);
        }

        // 核心逻辑：显示 (指定给我的) OR (没指定人的) OR (我自己回复过的)
        // 加上括号很重要，否则 OR 会破坏前面的 status 条件
        query.and(wrapper ->
                wrapper.eq("doctor_id", doctorId)
                        .or()
                        .isNull("doctor_id")
                        // 如果是已回复状态，还得看是不是我回复的
                        .or(w -> w.eq("status", 1).eq("doctor_id", doctorId))
        );

        query.orderByDesc("create_time");

        List<ConsultationOrder> list = consultationMapper.selectList(query);

        result.put("code", 0);
        result.put("msg", "");
        result.put("count", list.size());
        result.put("data", list);
        return result;
    }

    // 3. 医生回复工单
    @PostMapping("/reply")
    public Map<String, Object> reply(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long orderId = Long.valueOf(params.get("orderId").toString());
            String content = params.get("replyContent").toString();
            Long doctorId = Long.valueOf(params.get("doctorId").toString());
            String doctorName = params.get("doctorName").toString();

            ConsultationOrder order = new ConsultationOrder();
            order.setOrderId(orderId);
            order.setReplyContent(content);
            order.setDoctorId(doctorId); // 锁定回复人
            order.setDoctorName(doctorName);
            order.setStatus(1); // 标记为已回复
            order.setReplyTime(LocalDateTime.now());

            consultationMapper.updateById(order);

            result.put("code", 200);
            result.put("msg", "回复成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "回复失败");
        }
        return result;
    }

    // 4. 孕妇查看自己的咨询记录
    @GetMapping("/my")
    public Map<String, Object> getMyConsult(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<ConsultationOrder> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.orderByDesc("create_time");

        List<ConsultationOrder> list = consultationMapper.selectList(query);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }

    // 5. 医生工作台统计数据
    @GetMapping("/doctor/stats")
    public Map<String, Object> getDoctorStats(@RequestParam Long doctorId) {
        Map<String, Object> result = new HashMap<>();

        // 待处理：状态=0 (且 指派给自己 或 未指派)
        QueryWrapper<ConsultationOrder> todoQuery = new QueryWrapper<>();
        todoQuery.eq("status", 0)
                .and(w -> w.eq("doctor_id", doctorId).or().isNull("doctor_id"));
        Long todoCount = consultationMapper.selectCount(todoQuery);

        // 已处理：状态=1 且 处理人是自己
        QueryWrapper<ConsultationOrder> doneQuery = new QueryWrapper<>();
        doneQuery.eq("status", 1).eq("doctor_id", doctorId);
        Long doneCount = consultationMapper.selectCount(doneQuery);

        // 累计服务人数
        Long totalCount = doneCount;

        // 模拟高危预警数 (比如描述里包含"疼"或"血")
        Long riskCount = consultationMapper.selectCount(
                new QueryWrapper<ConsultationOrder>()
                        .eq("status", 0) // 只统计未处理的高危
                        .and(w -> w.like("question_desc", "疼").or().like("question_desc", "血"))
        );

        Map<String, Object> data = new HashMap<>();
        data.put("todo", todoCount);
        data.put("done", doneCount);
        data.put("total", totalCount);
        data.put("risk", riskCount);

        result.put("code", 200);
        result.put("data", data);
        return result;
    }
}