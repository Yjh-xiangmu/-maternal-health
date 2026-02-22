package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.KnowledgeBase;
import com.example.maternal.mapper.KnowledgeBaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = "*")
public class KnowledgeController {

    @Autowired
    private KnowledgeBaseMapper knowledgeMapper;

    // 1. 获取列表 (支持搜索 + 分类)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category) {
        QueryWrapper<KnowledgeBase> query = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            query.like("title", keyword);
        }
        if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            query.eq("category", category);
        }
        query.orderByDesc("create_time");

        List<KnowledgeBase> list = knowledgeMapper.selectList(query);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 0); // LayUI 格式
        result.put("msg", "");
        result.put("count", list.size());
        result.put("data", list);
        return result;
    }

    // 2. 获取详情 (阅读量+1)
    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long id) {
        KnowledgeBase kb = knowledgeMapper.selectById(id);
        if (kb != null) {
            // 增加阅读量
            kb.setViewCount(kb.getViewCount() == null ? 1 : kb.getViewCount() + 1);
            knowledgeMapper.updateById(kb);
        }
        return Map.of("code", 200, "data", kb);
    }

    // 3. 管理员发布文章
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody KnowledgeBase kb) {
        if (kb.getKbId() == null) {
            kb.setCreateTime(LocalDateTime.now());
            kb.setViewCount(0);
            knowledgeMapper.insert(kb);
        } else {
            knowledgeMapper.updateById(kb);
        }
        return Map.of("code", 200, "msg", "发布成功");
    }

    // 4. 管理员删除
    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Long> params) {
        knowledgeMapper.deleteById(params.get("id"));
        return Map.of("code", 200, "msg", "删除成功");
    }
}