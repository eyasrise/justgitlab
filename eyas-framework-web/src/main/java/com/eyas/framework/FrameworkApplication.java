package com.eyas.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = {
        "com.eyas.framework.dao",
        "com.eyas.framework.annotation",
        "com.eyas.framework.config",
        "com.eyas.framework.mapper"
})
@EnableSwagger2
public class FrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkApplication.class, args);
    }

}
