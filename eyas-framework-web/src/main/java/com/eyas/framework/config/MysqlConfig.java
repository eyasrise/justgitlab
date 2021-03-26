package com.eyas.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MysqlConfig {

    @Bean
    public MySqlInterceptor mySqlInterceptor(){
        return new MySqlInterceptor();
    }
}
