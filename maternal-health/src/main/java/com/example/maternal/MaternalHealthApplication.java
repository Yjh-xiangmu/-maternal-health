package com.example.maternal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//访问 http://localhost:8080/login.html
@SpringBootApplication
// 仅保留 basePackages，并确保路径正确指向你的 Mapper 接口包
@MapperScan(basePackages = "com.example.maternal.mapper")
public class MaternalHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaternalHealthApplication.class, args);
    }

}