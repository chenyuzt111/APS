package com.benewake.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * aps
 */
@EnableAsync
@EnableAspectJAutoProxy
@MapperScan(basePackages = "com.benewake.system.mapper")
@SpringBootApplication
public class ApsSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApsSystemApplication.class, args);
    }
}
