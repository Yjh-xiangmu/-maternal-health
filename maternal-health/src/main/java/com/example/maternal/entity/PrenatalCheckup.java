package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("prenatal_checkup")
public class PrenatalCheckup {
    @TableId(type = IdType.AUTO)
    private Long checkupId;

    private Long userId;

    private LocalDate checkDate;     // 产检日期
    private Integer weekNo;          // 孕周 (第几周)
    private String doctorAdvice;     // 医生医嘱
    private String reportImages;     // 检查报告图片URL (为了简单，先存字符串)
    private LocalDate nextCheckDate; // 下次产检日期
}