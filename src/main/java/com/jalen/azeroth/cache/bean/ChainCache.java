package com.jalen.azeroth.cache.bean;

import org.springframework.cache.Cache;

public class ChainCache extends AbstractCache {
    private final Cache currentCache;
    private final Cache nextCache;

    public ChainCache(Cache currentCache, Cache nextCache) {
        super(true);
        this.currentCache = currentCache;
        this.nextCache = nextCache;
    }

    @Override
    public String getName() {
        return currentCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return currentCache;
    }

    @Override
    public void put(Object key, Object value) {
        if (nextCache != null) {
            nextCache.put(key, value);
        }
        currentCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (nextCache != null) {
            nextCache.putIfAbsent(key, value);
        }
        return currentCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        if (nextCache != null) {
            nextCache.evict(key);
        }
        currentCache.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        if (nextCache != null) {
            nextCache.evictIfPresent(key);
        }
        return currentCache.evictIfPresent(key);
    }

    @Override
    public void clear() {
        if (nextCache != null) {
            nextCache.clear();
        }
        currentCache.clear();
    }

    @Override
    public boolean invalidate() {
        if (nextCache != null) {
            nextCache.invalidate();
        }
        return currentCache.invalidate();
    }

    protected Object lookup(Object key) {
        ValueWrapper value = currentCache.get(key);
        if(value == null && nextCache != null) {
            value = nextCache.get(key);
            if (value != null) {
                currentCache.put(key, value.get());
            }
        }
        return value != null ? value.get() : null;
    }
}
