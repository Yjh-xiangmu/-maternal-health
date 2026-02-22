package com.example.maternal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String path = System.getProperty("user.dir") + "/files/";

        // 关键配置：把网址 /files/** 映射到 本地文件夹 files/
        // 例如：http://localhost:8080/files/abc.jpg -> 读取本地 files/abc.jpg
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + path);
    }
}