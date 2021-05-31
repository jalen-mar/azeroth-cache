package com.jalen.azeroth.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "azeroth.cache")
public class AzerothCacheProperties {
    private CacheManagerFactory cacheManagerFactory;
    private List<Map<String, Object>> item;

    public CacheManagerFactory getCacheManagerFactory() {
        return cacheManagerFactory;
    }

    public void setCacheManagerFactory(String cacheManagerFactory) {
        if (cacheManagerFactory != null && cacheManagerFactory.trim().length() != 0) {
            try {
                Class<CacheManagerFactory> cls = (Class<CacheManagerFactory>) Class.forName(cacheManagerFactory);
                this.cacheManagerFactory = cls.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("The CacheManagerFactory('" + cacheManagerFactory + "') instance can't been initialized!");
            }
        }
    }

    public List<Map<String, Object>> getItem() {
        return item;
    }

    public void setItem(List<Map<String, Object>> item) {
        this.item = item;
    }
}
