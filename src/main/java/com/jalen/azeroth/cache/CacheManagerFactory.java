package com.jalen.azeroth.cache;

import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public interface CacheManagerFactory {
    Cache createNewCache(String topic, String name, Map<String, Object> params, AzerothCacheManager manager);

    ApplicationContext getApplicationContext();

    void setApplicationContext(ApplicationContext applicationContext);
}
