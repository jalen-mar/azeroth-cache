package com.jalen.azeroth.cache.bean;

import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractCache extends AbstractValueAdaptingCache {
    private final ReentrantLock lock = new ReentrantLock();

    protected AbstractCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if(value == null) {
            try {
                lock.lock();
                value = lookup(key);
                if (value == null) {
                    value = valueLoader.call();
                    put(key, value);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("CacheValueLoader.call() is Error", e);
            } finally {
                lock.unlock();
            }
        }
        return (T) value;
    }
}
