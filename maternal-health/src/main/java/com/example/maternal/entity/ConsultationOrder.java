package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("consultation_order")
public class ConsultationOrder {
    @TableId(type = IdType.AUTO)
    private Long orderId;

    private Long userId;        // 提问者ID
    private String realName;    // 提问者姓名 (冗余字段方便显示)
    private String phone;       // 联系电话

    private String questionTitle; // 问题标题
    private String questionDesc;  // 问题详细描述

    private Integer status;       // 0-待回复, 1-已回复
    private String replyContent;  // 医生回复内容
    private Long doctorId;        // 回复的医生ID
    private String doctorName;    // 回复的医生姓名

    private LocalDateTime createTime; // 提问时间
    private LocalDateTime replyTime;  // 回复时间
}