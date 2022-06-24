package com.eyas.framework.service.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.GsonUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import com.eyas.framework.intf.RedisService;
import com.eyas.framework.middle.EyasFrameworkMiddle;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EyasFrameWorkRedisServiceImpl<Dto,D,Q> extends EyasFrameworkServiceImpl<Dto,D,Q> {

    private final RedisService redisService;

    public EyasFrameWorkRedisServiceImpl(EyasFrameworkMiddle<D, Q> eyasFrameworkMiddle,
                                         RedisService redisService) {
        super(eyasFrameworkMiddle);
        this.redisService = redisService;
    }

    // 新增数据
    public Integer create(Dto dto, String key, long time) {
        Integer insert = super.insert(dto);
        if (1 == insert) {
            // 缓存
            if (EmptyUtil.isEmpty(time)) {
                time = redisService.getElementCacheTimeout();
            }
            this.redisService.set(key, time);
        }
        return null;
    }

    public Integer update(Dto dto, String key, long time, Long id) {
        RLock rLock = redisService.redissonWriteLock(key);
        try {
            // 新增数据--防止双写不一致
            Integer update = super.updateByLock(dto, id);
            // 更新缓存
            this.redisService.set(key, time);
            this.redisService.getElementMap().put(key, dto);
        } catch (Exception e) {
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.UPDATE_DATA_FAIL, "热点数据更新有误!");
        } finally {
            this.redisService.redissonReadWriteUnLock(rLock);
        }
        return null;
    }

    public Object getRedisElement(String key, long time, String elementKeyId){
        Object object = this.redisService.getElementFromCache(key);
        if (null != object){
            // 如果不是空返回
            return object;
        }
        // DCL-双重检查锁--防止缓存失效
        // 这个时间需要根据情况设置-可以为空，默认30s
        boolean lockFlag = this.redisService.redissonTryLock(key, time);
        if (lockFlag){
            // 继续查询一下缓存
            object = this.redisService.getElementFromCache(key);
            if (null != object){
                // 如果不是空返回
                return object;
            }
            // 加读锁--为了提高性能
            RLock rLock = this.redisService.redissonReadLock(key);
            try {
                // 查询数据库--调用父类方法
                object = super.getInfoById(Long.valueOf(elementKeyId));
                if (EmptyUtil.isEmpty(object)) {
                    // 缓存redis
                    this.redisService.set(key, object);
                    // 缓存本地map
                    this.redisService.getElementMap().put(key, object);
                }else{
                    // 防止缓存穿透--缓存空数据
                    this.redisService.expire(key, this.redisService.genEmptyCacheTimeout());
                }
            }catch (Exception e){
                throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR, "获取商品失败!");
            }finally {
                this.redisService.redissonReadWriteUnLock(rLock);
            }
        }
        return object;
    }
}
