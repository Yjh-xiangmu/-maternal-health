package com.example.maternal.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.MallOrder;
import com.example.maternal.mapper.MallOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private MallOrderMapper orderMapper;

    /**
     * 1. [用户] 创建订单
     */
    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody MallOrder order) {
        // 生成唯一订单号 (时间戳+随机数，使用Hutool工具)
        order.setOrderNo("ORD" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus(0); // 初始状态：0-待支付
        order.setCreateTime(LocalDateTime.now());

        orderMapper.insert(order);

        return Map.of("code", 200, "msg", "下单成功", "data", order.getOrderNo());
    }

    /**
     * 2. [用户] 模拟支付成功
     * 前端收银台点击"我已付款"后调用
     */
    @PostMapping("/pay")
    public Map<String, Object> pay(@RequestBody Map<String, String> params) {
        String orderNo = params.get("orderNo");

        QueryWrapper<MallOrder> query = new QueryWrapper<>();
        query.eq("order_no", orderNo);
        MallOrder order = orderMapper.selectOne(query);

        if(order != null) {
            order.setStatus(1); // 修改状态为：1-已支付
            order.setPayTime(LocalDateTime.now());
            orderMapper.updateById(order);
            return Map.of("code", 200, "msg", "支付成功");
        }
        return Map.of("code", 500, "msg", "订单不存在");
    }

    /**
     * 3. [用户] 查看我的订单列表
     */
    @GetMapping("/my")
    public Map<String, Object> myOrders(@RequestParam Long userId) {
        QueryWrapper<MallOrder> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        query.orderByDesc("create_time"); // 最新订单排最前

        List<MallOrder> list = orderMapper.selectList(query);
        return Map.of("code", 200, "data", list);
    }

    /**
     * 4. [管理员] 获取所有订单 (支持按订单号、状态搜索)
     */
    @GetMapping("/admin/list")
    public Map<String, Object> getAllOrders(@RequestParam(required = false) String orderNo,
                                            @RequestParam(required = false) Integer status) {
        QueryWrapper<MallOrder> query = new QueryWrapper<>();

        // 动态拼接查询条件
        if (orderNo != null && !orderNo.isEmpty()) {
            query.like("order_no", orderNo);
        }
        if (status != null) {
            query.eq("status", status);
        }
        query.orderByDesc("create_time");

        List<MallOrder> list = orderMapper.selectList(query);

        // 构造 LayUI 表格需要的格式
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", list.size());
        map.put("data", list);
        return map;
    }

    /**
     * 5. [管理员] 订单发货
     */
    @PostMapping("/delivery")
    public Map<String, Object> delivery(@RequestBody Map<String, Long> params) {
        Long orderId = params.get("orderId");

        MallOrder order = new MallOrder();
        order.setOrderId(orderId);
        order.setStatus(2); // 修改状态为：2-已发货

        orderMapper.updateById(order);

        return Map.of("code", 200, "msg", "发货成功");
    }
    // ... 原有代码 ...

    // [新增] 6. 用户确认收货
    @PostMapping("/receive")
    public Map<String, Object> confirmReceipt(@RequestBody Map<String, Long> params) {
        Long orderId = params.get("orderId");

        MallOrder order = new MallOrder();
        order.setOrderId(orderId);
        order.setStatus(3); // 3 代表已完成/已收货

        orderMapper.updateById(order);

        return Map.of("code", 200, "msg", "交易完成");
    }
}