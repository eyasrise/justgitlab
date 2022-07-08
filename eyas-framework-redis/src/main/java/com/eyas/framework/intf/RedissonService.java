package com.eyas.framework.intf;

import com.eyas.framework.enumeration.RedisKeyEnumInterface;

import java.util.concurrent.TimeUnit;

public interface RedissonService {
    /**
     * 获取key数据
     *
     * @param redisKeyEnumInterface key枚举
     * @return key的值
     */
    String getStr(RedisKeyEnumInterface redisKeyEnumInterface);

    /**
     * 设置key的值
     *
     * @param redisKeyEnumInterface key枚举
     * @param value key的值
     */
    void setStr(RedisKeyEnumInterface redisKeyEnumInterface, String value);

    /**
     * 设置带失效时间的key
     *
     * @param redisKeyEnumInterface key枚举
     * @param value key的值
     * @param timeToLive 失效时间
     * @param timeUnit 时间单位
     */
    void setStrTime(RedisKeyEnumInterface redisKeyEnumInterface, String value, Long timeToLive, TimeUnit timeUnit);
}
