package com.eyas.framework.controller;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.GsonUtil;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.intf.RedisService;
import com.eyas.framework.service.intf.EyasFrameWorkRedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
@Slf4j
public class RedisController {

//    @Autowired
//    private Redisson redisson;
//
//    @Autowired
//    private RedisService redisService;

    @Autowired
    private EyasFrameWorkRedisService eyasFrameWorkRedisService;

//    public void aa(){
//        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("nameList");
//        if (0 == bloomFilter.getSize()) {
//            //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
//            bloomFilter.tryInit(100000L, 0.03);
//           //将zhuge插入到布隆过滤器中
//            bloomFilter.add("zhuge");
//            bloomFilter.add("tuling");
//            System.out.println("只能进来一次1");
//            //判断下面号码是否在布隆过滤器中
//            System.out.println(bloomFilter.contains("guojia"));//false
//            System.out.println(bloomFilter.contains("baiqi"));//false
//            System.out.println(bloomFilter.contains("zhuge"));//true
//            System.out.println("只能进来一次1");
//        }
//        System.out.println("2");
////        //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
////        bloomFilter.tryInit(100000L,0.03);
//        //判断下面号码是否在布隆过滤器中
//        System.out.println(bloomFilter.contains("guojia"));//false
//        System.out.println(bloomFilter.contains("baiqi"));//false
//        System.out.println(bloomFilter.contains("zhuge"));//true
//    }

    @GetMapping("/bulong")
    @WithOutToken
    public void bulong() {
//        boolean lock = redisService.set("eyas-test9", "1212121");
//        log.info("" + lock);
//        Object o = redisService.getElementFromCache("eyas-test9", true);
//        log.info(GsonUtil.objectToJson(o));
        for (int i=0; i<100 ; i++) {
            new Thread(() -> {
                Object o1 = eyasFrameWorkRedisService.getRedisElement("eyas-ceShi-0706-6", 1000, "12", TimeUnit.MILLISECONDS);
//                log.info(Thread.currentThread().getName() + "-->" + GsonUtil.objectToJson(o1));
            }, "thread" + i).start();

        }
    }
}
