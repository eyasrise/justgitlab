package com.eyas.framework.controller;


import java.util.Arrays;
import java.util.Map;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.config.CommonBlockHandler;
import com.eyas.framework.config.CommonFallback;
import com.eyas.framework.config.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 *
 * @author fox
 * @email 2763800211@qq.com
 * @date 2021-01-28 15:53:24
 */
@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController {




    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @SentinelResource(value = "userinfo",
            blockHandlerClass = CommonBlockHandler.class,
            blockHandler = "handleException2",
            fallbackClass = CommonFallback.class,
            fallback = "fallback"
    )
    @WithOutToken
    public R info(@PathVariable("id") Integer id){
        log.info("id:{}", id);
		if(id==4){
		    throw new IllegalArgumentException("异常参数");
        }

        return R.ok().put("user", id);
    }

    public R handleException2(@PathVariable("id") Integer id, BlockException exception){
        return R.error(-1,"===被限流降级啦===");
    }
    public R fallback(@PathVariable("id") Integer id,Throwable e){
        return R.error(-1,"===被熔断降级啦==="+e.getMessage());
    }

//getMessage    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    public R save(@RequestBody UserEntity user){
//		userService.save(user);
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    public R update(@RequestBody UserEntity user){
//		userService.updateById(user);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Integer[] ids){
//		userService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
