package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_base")
public class KnowledgeBase {
    @TableId(type = IdType.AUTO)
    private Long kbId;

    private String title;       // 标题
    private String category;    // 分类 (如：孕期营养、产后护理)
    private String content;     // 内容 (支持长文本)
    private Integer viewCount;  // 阅读量
    private LocalDateTime createTime;
}