package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.maternal.entity.User;
import com.example.maternal.entity.ConsultationOrder; // 新增
import com.example.maternal.entity.DailyRecord;       // 新增
import com.example.maternal.entity.ProductInfo;       // 新增
import com.example.maternal.mapper.UserMapper;
import com.example.maternal.mapper.ConsultationOrderMapper; // 新增
import com.example.maternal.mapper.DailyRecordMapper;       // 新增
import com.example.maternal.mapper.ProductInfoMapper;       // 新增
import com.example.maternal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductInfoMapper productMapper; // 注入商品Mapper

    @Autowired
    private ConsultationOrderMapper consultMapper; // 注入咨询Mapper

    @Autowired
    private DailyRecordMapper dailyRecordMapper; // 注入打卡记录Mapper

    /**
     * ★★★ 新增接口：获取后台首页真实统计数据 ★★★
     * 对应前端 admin_index.html 的图表和数字卡片
     */
    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> data = new HashMap<>();

        // 1. 获取顶部四个卡片的真实数字
        // 孕妇人数 (role=0)
        data.put("userCount", userMapper.selectCount(new QueryWrapper<User>().eq("role", 0)));
        // 医生人数 (role=1)
        data.put("doctorCount", userMapper.selectCount(new QueryWrapper<User>().eq("role", 1)));
        // 商品总数 (查询所有商品)
        data.put("productCount", productMapper.selectCount(null));
        // 待处理咨询 (status=0)
        data.put("consultCount", consultMapper.selectCount(new QueryWrapper<ConsultationOrder>().eq("status", 0)));

        // 2. 获取近 7 天的活跃趋势 (查数据库 daily_record 表)
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        LocalDate today = LocalDate.now();
        // 循环过去7天 (从6天前到今天)
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString(); // 例如 2023-11-25

            // 查询这一天有多少条打卡记录
            Long count = dailyRecordMapper.selectCount(new QueryWrapper<DailyRecord>().eq("record_date", date));

            dates.add(dateStr.substring(5)); // 只存 MM-dd (例如 11-25) 用于图表X轴
            counts.add(count);
        }

        data.put("chartDates", dates);
        data.put("chartCounts", counts);

        // 返回标准格式
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", data);
        return result;
    }

    /**
     * 获取孕妇用户列表 (支持按姓名/手机号搜索)
     */
    @GetMapping("/user/list")
    public Map<String, Object> getUserList(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer limit,
                                           @RequestParam(required = false) String keyword) {

        // 1. 构造查询条件
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("role", 0); // 只查孕妇 (role=0)

        if (keyword != null && !keyword.isEmpty()) {
            // 模糊查询：手机号 OR 真实姓名
            query.and(wrapper -> wrapper.like("username", keyword).or().like("real_name", keyword));
        }

        query.orderByDesc("create_time"); // 新注册的在前面

        // 2. 执行分页查询 (MyBatis Plus自带分页)
        Page<User> pageParam = new Page<>(page, limit);
        Page<User> resultPage = userMapper.selectPage(pageParam, query);

        // 3. 构造 LayUI 表格需要的返回格式
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0); // LayUI 规定 0 代表成功
        map.put("msg", "");
        map.put("count", resultPage.getTotal()); // 总条数
        map.put("data", resultPage.getRecords()); // 当前页数据

        return map;
    }

    /**
     * 封禁/解封用户
     */
    @PostMapping("/user/status")
    public Map<String, Object> updateUserStatus(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        // 防止空指针，增加简单判断
        if(params.get("userId") == null || params.get("status") == null){
            result.put("code", 500);
            result.put("msg", "参数错误");
            return result;
        }

        Long userId = Long.valueOf(params.get("userId").toString());
        Integer status = Integer.valueOf(params.get("status").toString()); // 0禁用 1正常

        User user = new User();
        user.setUserId(userId);
        user.setStatus(status);

        userMapper.updateById(user); // 更新状态

        result.put("code", 200);
        result.put("msg", "操作成功");
        return result;
    }
}