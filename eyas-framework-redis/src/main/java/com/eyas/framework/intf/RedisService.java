package com.eyas.framework.intf;

import org.redisson.api.RLock;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by yixuan on 2019/7/23.
 */
public interface RedisService {

    boolean expire(String key, long time);

    Object get(String key);

    boolean set(String key, Object value);

    void del(String key);

    String tryLock(String key, Long tryMillis, Long expireMillis);

    void releaseLock(String key, String value);

    void setElementMap(Map<String, Object> elementMap);

    Map<String, Object> getElementMap();

    boolean redissonTryLock(String key, long waitTime, TimeUnit timeUnit);

    void redissonUnLock(String key);

    /**
     * 获取redis数据(高可用)
     *
     * @param key redisKey
     * @param bloomFilterExist 是否使用布隆过滤器
     * @return Object
     */
    Object getElementFromCache(String key, boolean bloomFilterExist);

    /**
     * 获取redis数据(高可用)
     *
     * @param key redisKey
     * @return Object
     */
    Object getElementFromCache(String key);

    /**
     * 系统默认的缓存失效时间
     *
     * @return Integer
     */
    Integer getElementCacheTimeout();

    /**
     * 系统默认的空值缓存时间
     *
     * @return
     */
    Integer genEmptyCacheTimeout();

    /**
     * 读写锁-写锁
     *
     * @param key
     * @return RLock
     */
    RLock redissonWriteLock(String key);

    /**
     * 读写锁-读锁
     *
     * @param key
     * @return RLock
     */
    RLock redissonReadLock(String key);

    /**
     * 读写锁失效
     *
     * @param rLock 读写锁失效
     * @return
     */
    boolean redissonReadWriteUnLock(RLock rLock);

}
