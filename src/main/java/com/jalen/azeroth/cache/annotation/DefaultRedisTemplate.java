package com.jalen.azeroth.cache.annotation;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Resource(name = "defaultRedisTemplate", type = RedisTemplate.class)
public @interface DefaultRedisTemplate {
}
