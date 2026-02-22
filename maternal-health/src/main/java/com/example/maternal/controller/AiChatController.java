package com.example.maternal.controller;

import com.example.maternal.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 允许跨域，方便前端调用
public class AiChatController {

    @Autowired
    private AiService aiService;

    // 发送对话请求接口
    // POST http://localhost:8080/api/ai/chat
    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String message = params.get("message").toString();

        String reply = aiService.chat(userId, message);

        return Map.of("reply", reply); // 返回 JSON: { "reply": "AI的回答..." }
    }
}