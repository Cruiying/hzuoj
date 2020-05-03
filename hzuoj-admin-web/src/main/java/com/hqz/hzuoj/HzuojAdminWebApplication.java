package com.hqz.hzuoj;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class HzuojAdminWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HzuojAdminWebApplication.class, args);
    }

}
