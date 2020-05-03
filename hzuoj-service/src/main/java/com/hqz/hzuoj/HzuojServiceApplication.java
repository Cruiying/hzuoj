package com.hqz.hzuoj;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDubbo
@MapperScan(basePackages = "com.hqz.hzuoj")
@EnableJms
@EnableScheduling
@SpringBootApplication
public class HzuojServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HzuojServiceApplication.class, args);
    }

}
