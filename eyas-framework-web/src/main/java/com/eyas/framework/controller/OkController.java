package com.eyas.framework.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.config.CommonBlockHandler;
import com.eyas.framework.config.CommonFallback;
import com.eyas.framework.config.R;
import com.eyas.framework.config.UseTask;
import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.entity.UserEntityQuery;
import com.eyas.framework.intf.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    @WithOutToken
    public String ok(){
        return "ok111111!";
    }


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
            this.redisService.tryLock(i+"", 2* 1000L, 3* 1000L);
            useTask.aa(i, Long.valueOf(delay));
            try {
                Thread.currentThread().sleep(Long.valueOf(delay));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public R handleException2(@PathVariable("id") Integer id, BlockException exception){
        return R.error(-1,"===被限流降级啦===");
    }
    public R fallback(@PathVariable("id") Integer id,Throwable e){
        return R.error(-1,"===被熔断降级啦==="+e.getMessage());
    }

    public R handleException1(BlockException exception){
        return R.error(-1,"===被限流降级啦===");
    }
    public R fallback1(Throwable e){
        return R.error(-1,"===被熔断降级啦==="+e.getMessage());
    }

}
