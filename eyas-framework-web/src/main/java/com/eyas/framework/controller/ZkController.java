package com.eyas.framework.controller;

import com.eyas.framework.GsonUtil;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.config.ZkLockService;
import com.eyas.framework.service.intf.EyasFrameWorkZkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/zk")
@Slf4j
public class ZkController {

    @Autowired
    private EyasFrameWorkZkService eyasFrameWorkZkService;


    @GetMapping("/bulong")
    @WithOutToken
    public void bulong() {
        for (int i=0; i<10 ; i++) {
            new Thread(() -> {
                Object o1 = eyasFrameWorkZkService.zkLock("eyas-zk-test", 5000L, TimeUnit.MILLISECONDS);
                log.info(Thread.currentThread().getName() + "-->" + GsonUtil.objectToJson(o1));
            }, "thread" + i).start();

        }
    }


}
