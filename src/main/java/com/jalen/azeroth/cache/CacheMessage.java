package com.jalen.azeroth.cache;

public class CacheMessage {
    private Object key;
    private Object value;
    private String topic;

    public CacheMessage() {}

    public CacheMessage(Object key, Object value, String topic) {
        this.key = key;
        this.value = value;
        this.topic = topic;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
