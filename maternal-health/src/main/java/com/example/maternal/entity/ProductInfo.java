package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("product_info")
public class ProductInfo {
    @TableId(type = IdType.AUTO)
    private Long productId;

    private String productName; // 商品名称
    private String category;    // 分类
    private BigDecimal price;   // 价格
    private String coverImg;    // 图片链接
    private String detailContent; // 详情
    private Integer status;     // 1-上架
}