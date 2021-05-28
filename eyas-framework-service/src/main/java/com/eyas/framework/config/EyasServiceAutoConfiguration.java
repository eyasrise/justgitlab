package com.eyas.framework.config;

import com.eyas.framework.provide.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(EyasFrameworkProperties.class)
public class EyasServiceAutoConfiguration {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Configuration
    @ConditionalOnProperty(prefix = EyasFrameworkProperties.EYAS_PREFIX + "dao", value = "enabled", havingValue = "true", matchIfMissing = true)
    public static class FillHandlerAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public IdFillHandler idFillHandler() {
            return new IdFillHandler();
        }

        @Bean
        @ConditionalOnMissingBean
        public CodeFillHandler codeFillHandler() {
            return new CodeFillHandler();
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(TenantCodeProvider.class)
        public TenantCodeFillHandler tenantCodeFillHandler(TenantCodeProvider tenantCodeProvider) {
            return new TenantCodeFillHandler(tenantCodeProvider);
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(AuditProvider.class)
        public AuditFillHandler auditFillHandler(AuditProvider auditProvider) {
            return new AuditFillHandler(auditProvider);
        }

    }


}



