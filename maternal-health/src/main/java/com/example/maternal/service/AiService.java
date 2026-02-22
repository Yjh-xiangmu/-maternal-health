package com.example.maternal.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.maternal.entity.AiChatRecord;
import com.example.maternal.entity.DailyRecord;
import com.example.maternal.entity.PregnantProfile;
import com.example.maternal.entity.User;
import com.example.maternal.mapper.AiChatMapper;
import com.example.maternal.mapper.DailyRecordMapper;
import com.example.maternal.mapper.PregnantProfileMapper;
import com.example.maternal.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Autowired private AiChatMapper aiChatMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private PregnantProfileMapper profileMapper;
    @Autowired private DailyRecordMapper dailyRecordMapper;

    // ★★★ 请确认这里填的是你的真实 Key ★★★
    private static final String API_KEY = "sk-9ca6c1e26ce740dcbd3e01b3c39109b4";
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    public String chat(Long userId, String question) {
        saveRecord(userId, "user", question);
        String promptWithContext = buildContextPrompt(userId, question);
        String aiAnswer = callAiApi(promptWithContext);
        saveRecord(userId, "ai", aiAnswer);
        return aiAnswer;
    }

    /**
     * 核心升级：构建包含【近7天趋势】的提示词
     */
    private String buildContextPrompt(Long userId, String question) {
        StringBuilder sb = new StringBuilder();

        // 1. 人设设定
        sb.append("你是一位专业的产科医生助手。请结合孕妇的个人档案和【最近7天的健康数据趋势】回答她的问题。\n");
        sb.append("回答要求：语气温柔、专业。如果发现体重激增、血压持续升高或血糖异常，请务必给出风险警示。\n");
        sb.append("-------------------\n");

        // 2. 个人档案 (不变)
        User user = userMapper.selectById(userId);
        QueryWrapper<PregnantProfile> profileQuery = new QueryWrapper<>();
        profileQuery.eq("user_id", userId);
        PregnantProfile profile = profileMapper.selectOne(profileQuery);

        if (user != null) sb.append("【用户姓名】：").append(user.getRealName()).append("\n");

        if (profile != null && profile.getLmpDate() != null) {
            Date lmp = Date.from(profile.getLmpDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            long days = DateUtil.between(lmp, new Date(), DateUnit.DAY);
            sb.append("【当前孕周】：孕 ").append(days / 7).append(" 周 + ").append(days % 7).append(" 天\n");
            sb.append("【风险评估】：").append(profile.getRiskLevel()).append("\n");
        }

        // 3. ★★★ 升级点：查询最近 7 条记录 ★★★
        QueryWrapper<DailyRecord> recordQuery = new QueryWrapper<>();
        recordQuery.eq("user_id", userId)
                .orderByDesc("record_date") // 最新的在前面
                .last("LIMIT 7"); // 只取7条

        List<DailyRecord> recordList = dailyRecordMapper.selectList(recordQuery);

        if (recordList != null && !recordList.isEmpty()) {
            sb.append("【最近7天健康数据监测】：\n");
            for (DailyRecord r : recordList) {
                sb.append("- 日期 ").append(r.getRecordDate()).append("：");

                if (r.getWeight() != null) sb.append("体重").append(r.getWeight()).append("kg ");
                if (r.getBloodSugar() != null) sb.append("| 血糖").append(r.getBloodSugar()).append(" ");
                if (r.getBloodPressureHigh() != null) sb.append("| 血压").append(r.getBloodPressureHigh()).append("/").append(r.getBloodPressureLow()).append(" ");
                if (r.getFeeling() != null) sb.append("| 感觉").append(r.getFeeling());

                sb.append("\n");
            }
        } else {
            sb.append("【健康数据】：暂无近期打卡记录。\n");
        }

        sb.append("-------------------\n");
        sb.append("【用户的问题】：").append(question);

        // 调试打印 (正式上线可注释掉)
        System.out.println(">>> 包含7天数据的 Prompt:\n" + sb.toString());

        return sb.toString();
    }

    private String callAiApi(String finalPrompt) {
        try {
            if (API_KEY == null || API_KEY.contains("你的Key")) {
                return "【系统提示】API Key 未配置。";
            }

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", finalPrompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("messages", new Object[]{message});
            body.put("stream", false);
            body.put("temperature", 1.0);

            String result = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(body))
                    .timeout(60000)
                    .execute()
                    .body();

            JSONObject json = JSONUtil.parseObj(result);
            if (json.containsKey("choices")) {
                JSONArray choices = json.getJSONArray("choices");
                return choices.getJSONObject(0).getJSONObject("message").getStr("content");
            } else if (json.containsKey("error")) {
                return "AI 报错: " + json.getJSONObject("error").getStr("message");
            }
            return "AI 响应异常";
        } catch (Exception e) {
            e.printStackTrace();
            return "网络请求失败，请检查网络。";
        }
    }

    private void saveRecord(Long userId, String role, String content) {
        AiChatRecord record = new AiChatRecord();
        record.setUserId(userId);
        record.setSenderRole(role);
        record.setContent(content);
        record.setCreateTime(LocalDateTime.now());
        aiChatMapper.insert(record);
    }
}