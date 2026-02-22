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
@RequestMapping("/api/admin/product")
@CrossOrigin(origins = "*")
public class AdminProductController {

    @Autowired
    private ProductInfoMapper productMapper;

    // 1. 获取商品列表
    @GetMapping("/list")
    public Map<String, Object> list() {
        List<ProductInfo> list = productMapper.selectList(new QueryWrapper<ProductInfo>().orderByDesc("product_id"));
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", list.size());
        map.put("data", list);
        return map;
    }

    // 2. 新增/编辑商品
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody ProductInfo product) {
        if (product.getProductId() == null) {
            productMapper.insert(product); // 新增
        } else {
            productMapper.updateById(product); // 编辑
        }
        return Map.of("code", 200, "msg", "保存成功");
    }

    // 3. 删除商品
    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody Map<String, Integer> params) {
        productMapper.deleteById(params.get("productId"));
        return Map.of("code", 200, "msg", "删除成功");
    }

    // 4. 快速上下架
    @PostMapping("/status")
    public Map<String, Object> updateStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("productId").toString());
        Integer status = Integer.valueOf(params.get("status").toString());

        ProductInfo p = new ProductInfo();
        p.setProductId(id);
        p.setStatus(status);
        productMapper.updateById(p);

        return Map.of("code", 200, "msg", "操作成功");
    }
}