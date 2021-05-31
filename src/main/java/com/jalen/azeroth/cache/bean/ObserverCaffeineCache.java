package com.jalen.azeroth.cache.bean;

import com.github.benmanes.caffeine.cache.Cache;
import com.jalen.azeroth.cache.CacheMessage;
import com.jalen.azeroth.cache.Observer;
import org.springframework.cache.caffeine.CaffeineCache;

public class ObserverCaffeineCache extends CaffeineCache implements Observer {
    private final String topic;

    public ObserverCaffeineCache(String name, String topic, Cache<Object, Object> cache) {
        this(name, topic, cache, true);
    }

    public ObserverCaffeineCache(String name, String topic, Cache<Object, Object> cache, boolean allowNullValues) {
        super(name, cache, allowNullValues);
        this.topic = topic;
    }

    @Override
    public void onMessage(CacheMessage message) {
        Object value = message.getValue();
        if (value == null) {
            evict(message.getKey());
        } else {
            put(message.getKey(), message.getValue());
        }
    }

    @Override
    public String getTopic() {
        return topic;
    }
}
