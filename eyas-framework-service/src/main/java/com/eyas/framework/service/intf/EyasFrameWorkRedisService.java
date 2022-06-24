package com.eyas.framework.service.intf;

import java.util.concurrent.TimeUnit;

public interface EyasFrameWorkRedisService<Dto, D, Q> {

    Integer create(Dto dto, String key, long time);

    Integer update(Dto dto, String key, long time, Long id);

    Object getRedisElement(String key, long waitTime, String elementKeyId, TimeUnit timeUnit);
}
