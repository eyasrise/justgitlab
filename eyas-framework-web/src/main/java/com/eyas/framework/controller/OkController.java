package com.eyas.framework.controller;

import com.eyas.framework.GsonUtil;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.config.UseTask;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntity;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.impl.RedisUtils;
import com.eyas.framework.intf.RedisService;
import com.eyas.framework.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Created by yixuan on 2019/7/8.
 */
@RestController
@RequestMapping(value = "/hello")
@Slf4j
public class OkController {

    @Autowired
    private RedisUtils redisService;

    @Autowired
    private UseTask useTask;

    @Autowired
    private UserServiceImpl userService;



    @GetMapping("/ok")
    @WithOutToken
    public String ok(){
        UserEntityQuery userEntityQuery = new UserEntityQuery();
        List<UserEntity > userEntityList = new ArrayList<>();
        UserEntity userEntity = new UserEntity();
        userEntityQuery.setUserName("王瑞");
        userEntity.setUserName("鼠忆轩");
        userEntityList.add(userEntity);
        this.userService.ceShi(userEntityQuery, userEntityList);
        return "ok111111!";
    }

//    @GetMapping("config/{key}")
//    public String config(@PathVariable String key){
//        return this.eyasFrameworkConfigService.getAreaConfigValue(key);
//    }


    @GetMapping("/testPage")
    @ResponseBody
    public EyasFrameworkResult testPage(){
        UserEntityQuery userEntityQuery = new UserEntityQuery();
        userEntityQuery.setUserName("121221");
        userEntityQuery.setTotalRecord(50);

        //备注222
        // 看一下提交记录
        userEntityQuery.setPageSize(10);
        return EyasFrameworkResult.ok(userEntityQuery, userEntityQuery);
    }



    @GetMapping("/redisLockTest/{delay}")
    public void redisLockTest(@PathVariable String delay){
        for (int i = 0; i < 10; i++) {
            this.redisService.redissonTryLock(i+"", 2* 1000L, TimeUnit.MILLISECONDS);
            useTask.aa(i, Long.valueOf(delay));
            try {
                Thread.currentThread().sleep(Long.valueOf(delay));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/testRedis")
    @WithOutToken
    public void testRedis(){
        this.redisService.setStr("eyas", "eyas-framework");
        Object object = this.redisService.getStr("eyas");
        log.info(GsonUtil.objectToJson(object));
    }

}
