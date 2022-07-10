package com.eyas.framework.controller;

import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.enums.EyasFrameworkNacosKeyEnum;
import com.eyas.framework.service.intf.EyasFrameworkConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nacosConfig")
@Slf4j
public class NacosConfigController {

    @Autowired
    private EyasFrameworkConfigService eyasFrameworkConfigService;


    @GetMapping("/getNacosKey/{key}")
    @WithOutToken
    public String  getNacosKey(@PathVariable String key){
//        String value = this.eyasFrameworkConfigService.getEnvNacosConfigValue(key);
        String name = this.eyasFrameworkConfigService.getEnvNacosConfigValue(EyasFrameworkNacosKeyEnum.NAME);
        String commonName = this.eyasFrameworkConfigService.getEnvNacosConfigValue(EyasFrameworkNacosKeyEnum.COMMON_NAME);
        String age = this.eyasFrameworkConfigService.getEnvNacosConfigValue(EyasFrameworkNacosKeyEnum.AGE);
        String commonAge = this.eyasFrameworkConfigService.getEnvNacosConfigValue(EyasFrameworkNacosKeyEnum.COMMON_AGE);

        log.info("name:" + name);
        log.info("age:" + age);

        log.info("commonName:" + commonName);
        log.info("commonAge:" + commonAge);

        return name + "--" + age;
    }

}
