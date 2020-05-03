package com.hqz.hzuoj;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.hqz.hzuoj.judge.mapper")
@EnableDubbo
@SpringBootApplication
@EnableJms
public class HzuojJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HzuojJudgeServiceApplication.class, args);
    }

}
