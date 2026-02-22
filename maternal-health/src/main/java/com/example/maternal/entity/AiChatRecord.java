package com.example.maternal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat_record")
public class AiChatRecord {
    @TableId(type = IdType.AUTO)
    private Long chatId;

    private Long userId;

    // "user" 代表用户提问, "ai" 代表AI回答
    private String senderRole;

    private String content;

    private LocalDateTime createTime;
}