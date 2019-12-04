package com.eyas.framework.intf;

/**
 * @author Created by yixuan on 2019/7/23.
 */
public interface RedisService {

    boolean expire(String key, long time);

    Object get(String key);

    boolean set(String key, Object value);

    /**
     * 删除key
     *
     * @param key
     */
    void del(String... key);

    void del(String key);

}
