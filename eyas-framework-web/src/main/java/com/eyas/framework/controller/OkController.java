package com.eyas.framework.controller;

import com.eyas.framework.JsonUtil;
import com.eyas.framework.config.UseTask;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.intf.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * @author Created by yixuan on 2019/7/8.
 */
@RestController
@RequestMapping(value = "/hello")
public class OkController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UseTask useTask;


    @GetMapping("/ok")
    public String ok(){
        return "ok!";
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
        userEntityQuery.setPageSize(10);
        return EyasFrameworkResult.ok(userEntityQuery, userEntityQuery);
    }



    @GetMapping("/redisLockTest")
    public void redisLockTest(){
        try {

            thread();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void thread() throws ExecutionException, InterruptedException {
        int j = 0;
        for (int i = 0; i < 10; i++) {
            useTask.aa(i);
        }
    }


}
