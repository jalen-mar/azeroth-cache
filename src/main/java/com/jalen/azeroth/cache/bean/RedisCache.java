package com.jalen.azeroth.cache.bean;

import com.jalen.azeroth.cache.CacheMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.Set;

public class RedisCache extends AbstractCache {
    private final String name;
    private final RedisTemplate redisTemplate;
    private final String topic;

    public RedisCache(String name, String topic, RedisTemplate redisTemplate) {
        super(true);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(redisTemplate, "redisTemplate must not be null");
        this.name = name;
        this.topic = topic;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected Object lookup(Object key) {
        System.out.println("redis获取key:" + key);
        return redisTemplate.opsForValue().get(getKey(key));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public void put(Object key, Object value) {
        System.out.println("redis修改key:" + key);
        redisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
        notify(new CacheMessage(key, value, topic));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object cacheValue = lookup(key);
        ValueWrapper result;
        if (cacheValue != null) {
            result = toValueWrapper(cacheValue);
        } else {
            result = toValueWrapper(value);
            put(key, value);
            notify(new CacheMessage(key, value, topic));
        }
        return result;
    }

    @Override
    public void evict(Object key) {
        redisTemplate.delete(getKey(key));
        notify(new CacheMessage(key, null, topic));
    }

    @Override
    public boolean evictIfPresent(Object key) {
        boolean result = redisTemplate.delete(getKey(key));
        if (result) {
            notify(new CacheMessage(key, null, topic));
        }
        return result;
    }

    @Override
    public void clear() {
        invalidate();
    }

    @Override
    public boolean invalidate() {
        Set<Object> keys = redisTemplate.keys(name.concat(":"));
        for(Object key : keys) {
            redisTemplate.delete(key);
            notify(new CacheMessage(key, null, topic));
        }
        return !keys.isEmpty();
    }

    private String getKey(Object key) {
        if (topic != null) {
            key = topic + ":" + key;
        }
        return name + ":" + key;
    }

    public void notify(CacheMessage message) {
        redisTemplate.convertAndSend("azeroth-cache-monitor", message);
    }
}
