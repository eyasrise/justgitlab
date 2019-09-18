package com.eyas.framework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = {
        "com.eyas.framework.dao"
})
public class FrameworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkApplication.class, args);
    }

    @Bean
    public void setProductName() {
        System.setProperty("project.name", "eyas-framework");
    }

}
