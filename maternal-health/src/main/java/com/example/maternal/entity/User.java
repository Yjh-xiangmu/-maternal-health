package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username; // 账号/手机号
    private String password; // 密码
    private String realName; // 真实姓名
    private Integer role;    // 角色: 0-孕妇, 1-医生, 2-管理员
    private String avatar;   // ★★★ 之前漏掉的头像字段
    private Integer status;  // 1-正常, 0-禁用
    private LocalDateTime createTime;
}