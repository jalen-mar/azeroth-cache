package com.jalen.azeroth.cache;

import com.jalen.azeroth.cache.redis.TopicSubscriber;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class AzerothCacheManager implements CacheManager, TopicSubscriber {
    private final ConcurrentMap<String, Cache> cachePool = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> cacheConfigPool = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final CacheManagerFactory factory;
    private final ConcurrentMap<String, List<Observer>> observers = new ConcurrentHashMap<>();
    private RedisTemplate redisTemplate;

    public AzerothCacheManager(AzerothCacheProperties cacheProperties, ApplicationContext context) {
        CacheManagerFactory factory = cacheProperties.getCacheManagerFactory();
        if (factory == null) {
            factory = new AzerothCacheManagerFactory();
        }
        factory.setApplicationContext(context);
        this.factory = factory;
        List<Map<String, Object>> items = cacheProperties.getItem();
        if (items != null) {
            items.forEach( it -> {
                String name = it.remove("name").toString();
                cacheConfigPool.putIfAbsent(name, it);
            });
        }
    }

    @Override
    public Cache getCache(String name) {
        return getCache(name, null);
    }

    public Cache getCache(String name, String topic) {
        Cache cache;
        if (cacheConfigPool.containsKey(name)) {
            cache = getCacheImpl(name, topic);
        } else {
            throw new IllegalArgumentException("The current cache instance has not been added, The cache name is '" + name + "'!");
        }
        return cache;
    }

    private Cache getCacheImpl(String name, String topic) {
        String targetName = topic == null ? name : name + ":" + topic;
        Cache cache = cachePool.get(targetName);
        if (cache == null) {
            try {
                lock.lock();
                cache = cachePool.get(targetName);
                if (cache == null) {
                    cache = createCacheImpl(name, topic);
                    cachePool.putIfAbsent(targetName, cache);
                }
            } finally {
                lock.unlock();
            }
        }
        return cache;
    }

    private Cache createCacheImpl(String name, String topic) {
        Cache cache = factory.createNewCache(name, topic, cacheConfigPool.get(name), this);
        if (cache instanceof Observer) {
            addObserver((Observer) cache);
        } else if (cache.getNativeCache() instanceof Observer) {
            addObserver((Observer) cache.getNativeCache());
        }
        return cache;
    }

    private void addObserver(Observer observer) {
        List<Observer> types = observers.computeIfAbsent(observer.getTopic(), k -> new ArrayList<>());
        types.add(observer);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheConfigPool.keySet();
    }

    @Override
    public String getTopic() {
        return "azeroth-cache-monitor";
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (redisTemplate == null) {
            try {
                lock.lock();
                if (redisTemplate == null) {
                    redisTemplate = factory.getApplicationContext().getBean(RedisTemplate.class);
                }
            } finally {
                lock.unlock();
            }
        }
        CacheMessage cacheMessage = (CacheMessage) redisTemplate.getValueSerializer().deserialize(message.getBody());
        List<Observer> types = observers.get(cacheMessage.getTopic());
        if (types != null) {
            types.forEach(it -> it.onMessage(cacheMessage));
        }
    }
}
