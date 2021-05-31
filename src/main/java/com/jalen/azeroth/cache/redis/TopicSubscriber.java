package com.jalen.azeroth.cache.redis;

import org.springframework.data.redis.connection.MessageListener;

public interface TopicSubscriber extends MessageListener {
    String getTopic();
}
