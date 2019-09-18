package com.eyas.framework.service.impl;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.eyas.framework.EmptyUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import com.eyas.framework.service.intf.EyasFrameworkConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author Created by yixuan on 2019/7/3.
 */
@Service
@Slf4j
public class EyasFrameworkConfigServiceImpl implements EyasFrameworkConfigService {

    @Value("${username2:2}")
    private String useLocalCache;

    @Override
    public String index(String key){
        return useLocalCache;
    }

    @NacosInjected
    private NamingService namingService;


    @Override
    public String getAreaConfigValue(String key){
        try {
            if (namingService != null) {
                Instance instance = this.namingService.selectOneHealthyInstance(System.getProperty("project.name"));
                log.info("instance:" + instance);
                // 获取元数据
                Object object = instance.getMetadata().get(key);
                if (EmptyUtil.isNotEmpty(object)) {
                    return String.valueOf(object);
                }
            }
        }catch (NacosException e) {
            log.error(ErrorFrameworkCodeEnum.NACOS_CONFIG_ERROR.getErrMsg(), e);
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.NACOS_CONFIG_ERROR, "配置中心服务异常");
        }
        return null;
    }
}
