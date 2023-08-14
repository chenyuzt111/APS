package com.benewake.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Lcs
 * @since 2023年08月14 11:43
 * 描 述： TODO
 */
@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = "com.benewake.system.mapper")
public class ApsSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApsSystemApplication.class, args);
    }
}
