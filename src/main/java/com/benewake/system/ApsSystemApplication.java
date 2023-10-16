package com.benewake.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Lcs
 * @since 2023年08月14 11:43
 * 描 述： TODO
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
