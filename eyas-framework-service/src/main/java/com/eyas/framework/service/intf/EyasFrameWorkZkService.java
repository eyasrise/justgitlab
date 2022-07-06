package com.eyas.framework.service.intf;

import java.util.concurrent.TimeUnit;

public interface EyasFrameWorkZkService {

    boolean zkLock(String key, long waitTime, TimeUnit timeUnit);
}
