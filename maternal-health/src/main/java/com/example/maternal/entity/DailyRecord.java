package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("daily_record")
public class DailyRecord {
    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long userId;

    private LocalDate recordDate; // 记录日期

    private Double weight;        // 体重 (kg)
    private Integer bloodPressureHigh; // 收缩压
    private Integer bloodPressureLow;  // 舒张压
    private Double bloodSugar;    // 血糖
    private Integer fetalMovement; // 胎动次数

    private String feeling;       // 身体感受 (开心/难受等)
    private String remark;        // 备注
}