package com.eyas.framework.impl;

import com.eyas.framework.EmptyUtil;
import com.eyas.framework.config.RedissonConfig;
import com.eyas.framework.enumeration.ErrorFrameworkCodeEnum;
import com.eyas.framework.exception.EyasFrameworkRuntimeException;
import com.eyas.framework.intf.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Created by yixuan on 2019/6/20.
 */
@Service
public class RedisServiceImpl implements RedisService {

    public static final Integer PRODUCT_CACHE_TIMEOUT = 24;

    public static Map<String, Object> elementMap = new ConcurrentHashMap<>();

    public static final String EMPTY_CACHE = "{}";

    private final RedisTemplate redisTemplate;

    private final Redisson redisson;

    /**
     * 锁失效时间
     */
    public static final Integer PRODUCT_CACHE_LEASE_TIME = 60;

    public static final Integer PRODUCT_LOCK_DEFAULT_WAIT_TIME = 5;


    @Autowired
    public RedisServiceImpl(RedisTemplate redisTemplate, Redisson redisson) {
        this.redisTemplate = redisTemplate;
        this.redisson = redisson;
    }

//=============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    public void del(String key){
        redisTemplate.delete(key);
    }

//============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    @Override
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 天数
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

//================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return double
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

//============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
//===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public String tryLock(String key, Long tryMillis, Long expireMillis) {
        RedisSerializer<String> keySerializer = this.redisTemplate.getKeySerializer();
        RedisSerializer<String> valueSerializer = this.redisTemplate.getValueSerializer();
        byte[] lockKey = keySerializer.serialize("RDS_LOCK_".concat(key));
        byte[] lockValue = valueSerializer.serialize(UUID.randomUUID().toString());
        Expiration expiration = Expiration.from(expireMillis, TimeUnit.MILLISECONDS);
        RedisStringCommands.SetOption option = RedisStringCommands.SetOption.ifAbsent();
        Long startMillis = System.currentTimeMillis();
        int var12 = 0;

        boolean isLock;
        do {
            if (var12++ > 0) {
                try {
                    Thread.sleep(2L);
                } catch (InterruptedException var14) {
                }
            }

            isLock = (Boolean)this.redisTemplate.execute((RedisCallback) (conn) -> {
                try {
                    return conn.set(lockKey, lockValue, expiration, option);
                } catch (Exception var6) {
                    throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR, var6);
                }
            });
        } while(!isLock && startMillis + tryMillis > System.currentTimeMillis());

        if (isLock) {
            return (String)valueSerializer.deserialize(lockValue);
        } else {
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public void releaseLock(String key, String value) {
        key = "RDS_LOCK_".concat(key);
        RedisScript<Long> script = RedisScript.of("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end", Long.class);
        Long result = (Long)this.redisTemplate.execute(script, Collections.singletonList(key), new Object[]{value});
        if (1L != result) {
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR, "release lock fail");
        }
    }

    //===============================redisson=================================


    @Override
    public Map<String, Object> getElementMap() {
        return elementMap;
    }

    @Override
    public void setElementMap(Map<String, Object> elementMap) {
        RedisServiceImpl.elementMap = elementMap;
    }

    /**
     *
     * 锁等待时间如果为空，默认设置1s一次;
     * 锁等待时间不宜大于10s,如果锁等待时间大于10s，默认修改成5s
     * @see PRODUCT_LOCK_DEFAULT_WAIT_TIME
     * leaseTime:锁失效时间，默认为设置为60s，看门狗运行6次，锁30s默认续期一次
     * @see PRODUCT_CACHE_LEASE_TIME
     * @param key 锁key
     * @param waitTime 线程等待拿锁时间
     * @return 拿锁结果
     */
    @Override
    public boolean redissonTryLock(String key, long waitTime){
        RLock hotCacheLock = redisson.getLock(key);
        try {
            if (EmptyUtil.isEmpty(waitTime)){
                return hotCacheLock.tryLock(PRODUCT_LOCK_DEFAULT_WAIT_TIME, PRODUCT_CACHE_LEASE_TIME, TimeUnit.SECONDS);
            }
            return hotCacheLock.tryLock(waitTime, PRODUCT_CACHE_LEASE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new EyasFrameworkRuntimeException(ErrorFrameworkCodeEnum.SYSTEM_ERROR, "redisson tryLock fail");
        }
    }

    @Override
    public void redissonUnLock(String key){
        RLock hotCacheLock = redisson.getLock(key);
        hotCacheLock.unlock();
    }

    @Override
    public Object getElementFromCache(String key){
        return getElementFromCache(key, false);
    }

    @Override
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
                RBloomFilter<String> bloomFilter = redisson.getBloomFilter(bloomFilterKey);
                //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
                bloomFilter.tryInit(100000L,0.03);
                // 把数据添加到bloomFilter
                bloomFilter.add(key);
                // 把bloomFilter塞入map
                elementMap.put(bloomFilterKey, "bloomFilterKey");
            }
            // 如果不是空--开始布隆过滤器逻辑
            RBloomFilter<String> bloomFilter = redisson.getBloomFilter(bloomFilterKey);
            boolean elementExist = bloomFilter.contains(key);
            if (!elementExist){
                // 如果元素不存在-直接返回
                return null;
            }
            // 如果元素存在布隆过滤器-继续查看redis
        }
        // redis获取
        Object object = this.get(key);
        if (EmptyUtil.isNotEmpty(object)){
            // 避免缓存穿透
            // 第一步如果数据是空，返回空对象并且续期
            if (EMPTY_CACHE.equals(object)){
                // 空数据续期
                this.expire(key, genEmptyCacheTimeout());
                return new Object();
            }
            // 如果不为空
            // 相对热数据续期--增加随机数--避免缓存失效
            this.expire(key, getElementCacheTimeout());
            return object;
        }
        return null;
    }

    /**
     * 续期时间
     * @return
     */
    @Override
    public Integer genEmptyCacheTimeout() {
        return 60 + new Random().nextInt(30);
    }

    @Override
    public Integer getElementCacheTimeout() {
        return PRODUCT_CACHE_TIMEOUT + new Random().nextInt(5) * 60 * 60;
    }

    @Override
    public RLock redissonWriteLock(String key){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
        RLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        return writeLock;
    }

    @Override
    public RLock redissonReadLock(String key){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(key);
        RLock readLock = readWriteLock.readLock();
        readLock.lock();
        return readLock;
    }

    @Override
    public boolean redissonReadWriteUnLock(RLock rLock){
        return rLock.unlink();
    }
}