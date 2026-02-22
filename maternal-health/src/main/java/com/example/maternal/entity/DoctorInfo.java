package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("doctor_info")
public class DoctorInfo {
    // 这里不是自增，而是手动关联 user_id
    @TableId
    private Long doctorId;

    private String jobNumber;   // 工号
    private String title;       // 职称 (主任医师/主治医师等)
    private String department;  // 科室 (产科/营养科等)
    private String specialty;   // 擅长领域
    private String introduction;// 个人简介
    private Integer consultCount; // 服务次数
}