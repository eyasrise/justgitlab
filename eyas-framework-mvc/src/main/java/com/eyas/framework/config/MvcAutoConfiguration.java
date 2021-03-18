package com.eyas.framework.config;

import com.eyas.framework.intf.RedisService;
import com.eyas.framework.provider.RedisUserProvider;
import com.eyas.framework.provider.UserProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class MvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisService.class)
    public UserProvider userProvider(RedisService redisService) {
        return new RedisUserProvider(redisService);
    }

}
