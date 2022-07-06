package com.eyas.framework.service.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.config.ZkLockService;
import com.eyas.framework.constant.SystemConstant;
import com.eyas.framework.service.intf.EyasFrameWorkZkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EyasFrameWorkZkServiceImpl implements EyasFrameWorkZkService {


    @Autowired
    private ZkLockService zkLockService;

    /**
     * zk锁
     * 强CP，不适合并发特别大的场景
     *
     * @param key zk锁key
     * @param waitTime zk锁等待时间(默认1000ms)
     * @param timeUnit 等待时间单位(默认是ms)
     * @return boolean
     */
    @Override
    public boolean zkLock(String key, long waitTime, TimeUnit timeUnit){
        if (EmptyUtil.isEmpty(waitTime)){
            waitTime = 1000L;
            timeUnit = TimeUnit.MILLISECONDS;
        }
        if (EmptyUtil.isEmpty(timeUnit)){
            timeUnit = TimeUnit.MILLISECONDS;
        }
        String elementKey = "/" + key + ":zb:key";
        boolean lockFlag = false;
        int count = 0;
        while (!lockFlag) {
            count ++;
            lockFlag = this.zkLockService.tryLock(elementKey, waitTime, timeUnit);
            // 自旋
            if (!lockFlag) {
                log.info(Thread.currentThread().getName() + "线程--->没有获取到锁了！");
                if (count >= SystemConstant.PROCESSORS){
                    log.info(Thread.currentThread().getName() + "线程--->超过了核心数，拒绝！");
                    break;
                }
            }
        }
        if (!lockFlag){
            // 自旋一定的次数如果还未获取到锁，那么我就释放锁，返回空对象
            // 防止自旋异常，增加一次获取锁尝试
            lockFlag = this.zkLockService.tryLock(elementKey, waitTime, timeUnit);
            // 依然失败就返回结束
            if (!lockFlag) {
                log.info(Thread.currentThread().getName() + "线程--->获取锁失败！");
                return false;
            }
        }
        log.info(Thread.currentThread().getName() + "线程--->加锁成功！");
        // 成功
        return true;
    }
}
