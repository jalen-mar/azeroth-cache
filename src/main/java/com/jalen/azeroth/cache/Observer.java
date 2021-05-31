package com.jalen.azeroth.cache;

public interface Observer {
    void onMessage(CacheMessage message);

    String getTopic();
}
