package com.eyas.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ZkLockService {

    @Autowired
    private CuratorFramework curatorFramework;

    public boolean tryLock(String lockKey, Long timeout, TimeUnit timeUnit) {
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, lockKey);
        try {
            if (lock.acquire(timeout, timeUnit)) {
                log.info(Thread.currentThread().getName() + " get lock");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                log.info(Thread.currentThread().getName() + " release lock");
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


}
