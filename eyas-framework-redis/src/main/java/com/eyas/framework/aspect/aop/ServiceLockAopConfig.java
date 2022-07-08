package com.eyas.framework.aspect.aop;

import com.eyas.framework.data.EyasFrameworkResult;
import com.eyas.framework.intf.RedisService;
import com.eyas.framework.intf.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class ServiceLockAopConfig {

    @Autowired
    private RedissonService redisService;


    @Pointcut("execution(public * *(..)) && @annotation(com.eyas.framework.aspect.config.ServiceLockService)" )
    public void addAdvice(){}


    @Around("addAdvice()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String logStr = "snt-eyas-framework-ServiceLockAopConfig-";
        // 设置锁
        String className = this.getClass().getSimpleName();
        String key = "EYAS-FRAMEWORK-SERVICE:SYNC:" + className;
        boolean lockKey = false;
        Object result;
        try {
            // 加锁开始
            // 加锁的实效是时间是一分钟——强制服务必须在一分钟执行完，其他的10s旋转等待
            log.info(logStr + key);
            lockKey = this.redisService.redissonTryLock(key, 10* 1000L, TimeUnit.MILLISECONDS);
            log.info(logStr + lockKey);
            long start = System.currentTimeMillis();
            result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info(logStr + "runTime--" + joinPoint + "\tUse time : " + (end - start) + " ms!");
            return result;
        } catch (Exception e) {
            log.error("加锁失败!", e.getMessage());
            e.printStackTrace();
            return EyasFrameworkResult.ok();
        }finally {
            log.info(lockKey + "锁已被释放");
            this.redisService.redissonUnLock(key);
        }
    }
}
