package com.eyas.framework.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Created by yixuan on 2019/7/3.
 */
@Configuration
@Slf4j
public class NacosRegisterConfiguration {

    @Value("${server.port}")
    private int serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    @NacosInjected
    private NamingService namingService;

    @PostConstruct
    public void registerInstance() throws NacosException {
        log.info("applicationName:" + applicationName + "--serverPort:" + serverPort);
        namingService.registerInstance(applicationName, "127.0.0.1", serverPort);
    }
}
