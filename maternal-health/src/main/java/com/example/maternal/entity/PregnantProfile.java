package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("pregnant_profile")
public class PregnantProfile {
    @TableId(type = IdType.AUTO)
    private Long profileId;

    private Long userId;

    private LocalDate lmpDate;   // 末次月经 (Last Menstrual Period)
    private LocalDate eddDate;   // 预产期 (Estimated Due Date)
    private String riskLevel;    // 风险等级 (低风险/高风险)
    private Double preWeight;    // 孕前体重
}