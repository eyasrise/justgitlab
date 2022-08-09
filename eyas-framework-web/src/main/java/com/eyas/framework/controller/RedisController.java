package com.eyas.framework.controller;

import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.intf.EyasFrameworkRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
@Slf4j
public class RedisController {


    @Autowired
    private EyasFrameworkRedisService eyasFrameWorkRedisService;


    @GetMapping("/bulong")
    @WithOutToken
    public void bulong() {
//        boolean lock = redisService.set("eyas-test9", "1212121");
//        log.info("" + lock);
//        Object o = redisService.getElementFromCache("eyas-test9", true);
//        log.info(GsonUtil.objectToJson(o));
        for (int i=0; i<100 ; i++) {
            new Thread(() -> {
                Object o1 = eyasFrameWorkRedisService.getRedisElement("eyas-ceShi-0809-3", 1000, "12", TimeUnit.MILLISECONDS);
//                log.info(Thread.currentThread().getName() + "-->" + GsonUtil.objectToJson(o1));
            }, "thread" + i).start();

        }
    }

}
