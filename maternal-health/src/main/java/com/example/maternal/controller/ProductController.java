package com.example.maternal.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.ProductInfo;
import com.example.maternal.mapper.ProductInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductInfoMapper productMapper;

    // 获取商品列表 (支持搜索和分类筛选)
    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) String category,
                                    @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<ProductInfo> query = new QueryWrapper<>();

        // 如果传了分类，就筛选分类
        if (category != null && !category.isEmpty() && !"全部".equals(category)) {
            query.eq("category", category);
        }
        // 如果传了关键词，就模糊查询
        if (keyword != null && !keyword.isEmpty()) {
            query.like("product_name", keyword);
        }

        query.eq("status", 1); // 只查上架的

        List<ProductInfo> list = productMapper.selectList(query);
        result.put("code", 200);
        result.put("data", list);
        return result;
    }
}