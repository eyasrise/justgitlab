package com.eyas.framework.intf;

import java.util.concurrent.TimeUnit;

public interface EyasFrameworkRedisService<Dto, D, Q> {

    Integer createRedisElement(Dto dto, String key, long time);

    Integer updateRedisElement(Dto dto, String key, long time, Long id);

    Object getRedisElement(String key, long waitTime, String elementKeyId, TimeUnit timeUnit);

    Object getRedisElementLogs(String element, long waitTime, String elementKeyId, TimeUnit timeUnit);
}
