package com.eyas.framework.config;

import com.eyas.framework.data.UserInfo;
import com.eyas.framework.interceptor.AuthenticationInterceptor;
import com.eyas.framework.intf.DatabaseService;
import com.eyas.framework.provider.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;


    @Bean
    public UserProvider userProvider(){
        return (userId, tokenInfo) -> UserInfo.builder().userId("XS1212").userCode("1212").tenantCode(100L).systemUser(null).build();
    }

    @Bean
    @ConditionalOnMissingBean(UserProvider.class)
    public UserProvider userProvider(DatabaseService databaseService) {
        return new DemoProvicer(databaseService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor);
    }

}
