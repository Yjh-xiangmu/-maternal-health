package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mall_order")
public class MallOrder {
    @TableId(type = IdType.AUTO)
    private Long orderId;
    private String orderNo;
    private Long userId;
    private String productName;
    private String productImg;
    private BigDecimal price;
    private Integer status; // 0-待支付, 1-已支付
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}