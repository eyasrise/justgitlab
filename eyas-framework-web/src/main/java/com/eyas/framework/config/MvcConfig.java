package com.eyas.framework.config;

import com.eyas.framework.interceptor.AuthenticationInterceptor;
import com.eyas.framework.interceptor.UserInfo;
import com.eyas.framework.provider.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;


    @Bean
    UserProvider userProvider(){
        return (userId, tokenInfo) -> {
            return UserInfo.builder().userId("XS1212").userCode("1212").tenantCode(100L).systemUser(null).build();
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor);
    }

}
