package com.eyas.framework.intf;

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

    boolean redissonTryLock(String key, long time);

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

}
