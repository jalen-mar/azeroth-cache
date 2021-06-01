package com.jalen.azeroth.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.jalen.azeroth.cache.bean.ChainCache;
import com.jalen.azeroth.cache.bean.ObserverCaffeineCache;
import com.jalen.azeroth.cache.bean.RedisCache;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;

public class AzerothCacheManagerFactory implements CacheManagerFactory {
    private ApplicationContext applicationContext;

    @Override
    public Cache createNewCache(String name, String topic, Map<String, Object> params, AzerothCacheManager manager) {
        Cache cache;
        switch ((String) params.get("type")) {
            case "caffeine": {
                Caffeine<Object, Object> builder = Caffeine.newBuilder();
                if (params.get("initialCapacity") != null) {
                    builder.initialCapacity((int) params.get("initialCapacity"));
                }
                if (params.get("maximumSize") != null) {
                    builder.maximumSize((int) params.get("maximumSize"));
                }
                if (params.get("maximumWeight") != null) {
                    builder.maximumWeight((int) params.get("maximumWeight"));
                }
                if (params.get("expireAfterAccess") != null) {
                    builder.expireAfterAccess(Duration.ofSeconds((int) params.get("expireAfterAccess")));
                }
                if (params.get("expireAfterWrite") != null) {
                    builder.expireAfterWrite(Duration.ofSeconds((int) params.get("expireAfterWrite")));
                }
                if (params.get("weakKeys") != null && (boolean) params.get("weakKeys")) {
                    builder.weakKeys();
                }
                if (params.get("weakValues") != null && (boolean) params.get("weakValues")) {
                    builder.weakValues();
                }
                if (params.get("softValues") != null && (boolean) params.get("softValues")) {
                    builder.softValues();
                }
                if (params.get("recordStats") != null && (boolean) params.get("recordStats")) {
                    builder.recordStats();
                }
                if (params.get("topic") != null) {
                    cache = new ObserverCaffeineCache(name, params.get("topic").toString(), builder.build(), true);
                } else {
                    cache = new CaffeineCache(name, builder.build(), true);
                }
                if (params.get("chain") != null) {
                    cache = new ChainCache(cache, manager.getCache((String) params.get("chain"), (String) params.get("topic")));
                }
            }
            break;
            case "redis": {
                RedisTemplate redisTemplate = applicationContext.getBean(name, RedisTemplate.class);
                cache = new RedisCache(name, topic, redisTemplate);
                if (params.get("chain") != null) {
                    cache = new ChainCache(cache, manager.getCache((String) params.get("chain"), (String) params.get("topic")));
                }
            }
            break;
            default: {
                throw new IllegalArgumentException("The current cache instance has not been added, The cache name is '" + name + "'!");
            }
        }
        return cache;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
