package com.jalen.azeroth.cache.annotation;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@Resource(name = "defaultRedisTemplate", type = RedisTemplate.class)
public @interface DefaultRedisTemplate {
}
