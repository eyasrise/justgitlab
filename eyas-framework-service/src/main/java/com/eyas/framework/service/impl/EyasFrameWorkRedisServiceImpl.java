package com.eyas.framework.service.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import com.eyas.framework.intf.RedisService;
import com.eyas.framework.middle.EyasFrameworkMiddle;
import com.eyas.framework.service.intf.EyasFrameWorkRedisService;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

@Service
public class EyasFrameWorkRedisServiceImpl<Dto,D,Q> extends EyasFrameworkServiceImpl<Dto,D,Q> implements EyasFrameWorkRedisService<Dto,D,Q> {

    private final RedisService redisService;

    public EyasFrameWorkRedisServiceImpl(EyasFrameworkMiddle<D, Q> eyasFrameworkMiddle,
                                         RedisService redisService) {
        super(eyasFrameworkMiddle);
        this.redisService = redisService;
    }

    @Override
    public Integer create(Dto dto, String key, long time) {
        Integer insert = super.insert(dto);
        if (1 == insert) {
            // 缓存
            if (EmptyUtil.isEmpty(time)) {
                time = redisService.getElementCacheTimeout();
            }
            this.redisService.set(key, time);
        }
        return insert;
    }

    @Override
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

    /**
     * 高可用热点数据双写一致性
     * 逻辑
     * 1、解决缓存穿透问题
     * 2、解决缓存失效问题
     * 3、解决读写不一致的问题
     * 4、增加读写锁提高获取数据的性能
     * 实现逻辑
     * 1、获取缓存数据:
     * 从一层redis增加三层并且可配置
     * ①jvm层使用ConcurrentHashMap(注意大小分配)
     * ②布隆过滤器
     * ③redis--如果查询的数据为空。缓存短暂的空数据
     * 2、DCL双重检查锁机制-防止缓存穿透
     * 分布式锁，在缓存没有数据的时候，只允许一个线程进来读数据库，其他的等待(这边需要注意一下失效时间问题)
     * 第二个线程进来就不会继续落到数据库了，失效时间控制的合理，会统一释放用户。
     * 不然会等待(总有一个线程持有一把锁，有性能消耗)
     * 3、增加读写锁
     * ①更新数据的时候使用写锁
     * ②查询数据的时候使用读锁
     * 当两个线程的mode都是read read的时候，支持并发访问，提高性能
     *
     * @param key 缓存key
     * @param time 缓存key失效时间，可以为空
     * @param elementKeyId 缓存key对应的数据id-用来获取数据库数据
     * @return Object
     */
    @Override
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
                if (EmptyUtil.isNotEmpty(object)) {
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
