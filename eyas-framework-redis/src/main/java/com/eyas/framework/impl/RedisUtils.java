package com.eyas.framework.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    public static final Integer PRODUCT_CACHE_TIMEOUT = 24;

    public static Map<String, Object> elementMap = new ConcurrentHashMap<>();

    public static final String EMPTY_CACHE = "{}";


    /**
     * 锁失效时间
     */
    public static final Integer PRODUCT_CACHE_LEASE_TIME = 60000;

    public static final Integer PRODUCT_LOCK_DEFAULT_WAIT_TIME = 1000;

    /**
     * 默认缓存时间
     */
    private static final Long DEFAULT_EXPIRED = 32000L;


    /**
     * 自动装配redisson client对象
     */
    @Resource
    private RedissonClient redissonClient;


    /**
     * 用于操作key
     * @return RKeys 对象
     */
    public RKeys getKeys() {
        return redissonClient.getKeys();
    }


    /**
     * 移除缓存
     *
     * @param key
     */
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }


    /**
     * 获取getBuckets 对象
     *
     * @return RBuckets 对象
     */
    public RBuckets getBuckets() {
        return redissonClient.getBuckets();
    }


    /**
     * 读取缓存中的字符串，永久有效
     *
     * @param key 缓存key
     * @return 字符串
     */
    public String getStr(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }


    /**
     * 缓存字符串
     *
     * @param key
     * @param value
     */
    public void setStr(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }


    /**
     * 缓存带过期时间的字符串
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param expired 缓存过期时间，long类型，必须传值
     */
    public void setStr(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value, expired <= 0L ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }


    /**
     * string 操作，如果不存在则写入缓存（string方式，不带有redisson的格式信息）
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param expired 缓存过期时间
     */
    public Boolean setIfAbsent(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        return bucket.trySet(value, expired <= 0L ? DEFAULT_EXPIRED : expired, TimeUnit.SECONDS);
    }


    /**
     * 如果不存在则写入缓存（string方式，不带有redisson的格式信息），永久保存
     *
     * @param key   缓存key
     * @param value 缓存值
     */
    public Boolean setIfAbsent(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key, StringCodec.INSTANCE);
        return bucket.trySet(value);
    }


    /**
     * 判断缓存是否存在
     *
     * @param key
     * @return true 存在
     */
    public Boolean isExists(String key) {
        return redissonClient.getBucket(key).isExists();
    }


    /**
     * 获取RList对象
     *
     * @param key RList的key
     * @return RList对象
     */
    public <T> RList<T> getList(String key) {
        return redissonClient.getList(key);
    }


    /**
     * 获取RMapCache对象
     *
     * @param key
     * @return RMapCache对象
     */
    public <K, V> RMapCache<K, V> getMap(String key) {
        return redissonClient.getMapCache(key);
    }


    /**
     * 获取RSET对象
     *
     * @param key
     * @return RSET对象
     */
    public <T> RSet<T> getSet(String key) {
        return redissonClient.getSet(key);
    }


    /**
     * 获取RScoredSortedSet对象
     *
     * @param key
     * @param <T>
     * @return RScoredSortedSet对象
     */
    public <T> RScoredSortedSet<T> getScoredSortedSet(String key) {
        return redissonClient.getScoredSortedSet(key);
    }

    //===============================redisson=================================


    public Map<String, Object> getElementMap() {
        return elementMap;
    }

    public void setElementMap(Map<String, Object> elementMap) {
        RedisServiceImpl.elementMap = elementMap;
    }

    /**
     * 锁等待时间如果为空，默认设置1s一次;
     * 锁等待时间不宜大于看门狗的触发时间(续期时间/3),如果锁等待时间大于看门狗的触发时间，默认修改成看门狗的触发时间/2
     * 比如:redisson默认的续期时间是30s,看门狗的触发时间是30s/3=10s
     * 那么如果锁等待时间设置超过了10s，其实不合理，这种场景项目默认设置成10s/2=5s
     * leaseTime:锁失效时间，默认为设置为60s，看门狗运行6次，锁30s默认续期一次
     * @see PRODUCT_CACHE_LEASE_TIME 60s
     * @param key 锁key
     * @param waitTime 线程等待拿锁时间
     * @return 拿锁结果
     */
    public boolean redissonTryLock(String key, long waitTime, TimeUnit timeUnit){
        RLock hotCacheLock = this.redissonClient.getLock(key);
        try {
            if (EmptyUtil.isEmpty(waitTime)){
                waitTime = PRODUCT_LOCK_DEFAULT_WAIT_TIME;
            }
            // 获取看门狗时间
            long lockWatchdogTimeout = redissonClient.getConfig().getLockWatchdogTimeout();
            if (EmptyUtil.isEmpty(lockWatchdogTimeout)){
                redissonClient.getConfig().setLockWatchdogTimeout(30000L);
                lockWatchdogTimeout = 30000L;
            }
            // 判断时间：等待时间理论上不应该超过看门狗的触发时间也就是续期时间/3
            // 默认给续期时间/3的基础上再折一半
            // 获取一下等待时间
            long waitTimeMs = timeUnit.toMillis(waitTime);
            if (waitTimeMs > lockWatchdogTimeout/3){
                waitTimeMs = lockWatchdogTimeout/6;
            }
            return hotCacheLock.tryLock(waitTimeMs, PRODUCT_CACHE_LEASE_TIME, timeUnit);
        } catch (InterruptedException e) {
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR, "redisson tryLock fail");
        }
    }

    public void redissonUnLock(String key){
        RLock hotCacheLock = redissonClient.getLock(key);
        hotCacheLock.unlock();
    }

    public Object getElementFromCache(String key){
        return getElementFromCache(key, false);
    }

    public Object getElementFromCache(String key, boolean bloomFilterExist){
        // 本地获取--这个map需要热数据维护
        Object element = elementMap.get(key);
        if (EmptyUtil.isNotEmpty(element)){
            return element;
        }
        // 布隆过滤器获取--按需设置--针对缓存穿透
        if (bloomFilterExist){
            String bloomFilterKey = "bloomFilterKey";
            // 判断布隆过滤器是否存在
            Object bloomFilterValue = elementMap.get(bloomFilterKey);
            // map会重启消失，所以map应该由热数据去维护
            if (EmptyUtil.isEmpty(bloomFilterValue)){
                // 如果是空初始化
                RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(bloomFilterKey);
                //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
                bloomFilter.tryInit(100000L,0.03);
                // 把数据添加到bloomFilter
                bloomFilter.add(key);
                // 把bloomFilter塞入map
                elementMap.put(bloomFilterKey, "bloomFilterKey");
            }
            // 如果不是空--开始布隆过滤器逻辑
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(bloomFilterKey);
            boolean elementExist = bloomFilter.contains(key);
            if (!elementExist){
                // 如果元素不存在-直接返回
                return null;
            }
            // 如果元素存在布隆过滤器-继续查看redis
        }
        // redis获取
        Object object = this.getStr(key);
        if (EmptyUtil.isNotEmpty(object)){
            // 避免缓存穿透
            // 第一步如果数据是空，返回空对象并且续期
            if (EMPTY_CACHE.equals(object)){
                // 空数据续期
                // 获取key
                String str = this.getStr(key);
                this.setStr(key, this.getStr(key), getElementCacheTimeout());
                return new Object();
            }
            // 如果不为空
            // 相对热数据续期--增加随机数--避免缓存失效
            this.setStr(key, this.getStr(key), getElementCacheTimeout());
            return object;
        }
        return null;
    }

    /**
     * 续期时间
     * @return
     */
    public Integer genEmptyCacheTimeout() {
        return 60 + new Random().nextInt(30);
    }

    public Integer getElementCacheTimeout() {
        return PRODUCT_CACHE_TIMEOUT + new Random().nextInt(5) * 60 * 60;
    }

    public RLock redissonWriteLock(String key){
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(key);
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        return writeLock;
    }

    public RLock redissonReadLock(String key){
        RReadWriteLock readWriteLock = this.redissonClient.getReadWriteLock(key);
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        return readLock;
    }

    public boolean redissonReadWriteUnLock(RLock rLock){
        return rLock.unlink();
    }
}
