package com.eyas.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(EyasFrameworkProperties.EYAS_PREFIX)
public class EyasFrameworkProperties {

    public static final String EYAS_PREFIX = "eyas";

    private ServiceConfig service;

    @Data
    public static class ServiceConfig {

        private Boolean enabled;

        private IDType idType = IDType.SNOWFLAKE;

        public enum IDType {
            AUTO,
            SNOWFLAKE,
            ;
        }

    }


}
