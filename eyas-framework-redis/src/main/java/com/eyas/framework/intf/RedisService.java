package com.eyas.framework.intf;

/**
 * @author Created by yixuan on 2019/7/23.
 */
public interface RedisService {

    boolean expire(String key, long time);

    Object get(String key);

    boolean set(String key, Object value);

    void del(String key);

}
