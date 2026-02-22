package com.example.maternal.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {

    // 上传接口
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // 1. 确定保存文件夹 (项目根目录/files)
        String fileFolder = System.getProperty("user.dir") + "/files/";
        if (!FileUtil.exist(fileFolder)) {
            FileUtil.mkdir(fileFolder); // 如果没有就创建
        }

        // 2. 生成唯一文件名 (防止重名覆盖)
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename); // 获取后缀 .jpg
        String newFileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 保存文件到本地
        File saveFile = new File(fileFolder + newFileName);
        file.transferTo(saveFile);

        // 4. 返回可访问的 URL
        String url = "http://localhost:8080/files/" + newFileName;

        result.put("code", 200); // LayUI 上传组件默认看 code=0 或 200
        result.put("msg", "上传成功");
        result.put("data", Map.of("src", url)); // 返回图片路径

        return result;
    }
}