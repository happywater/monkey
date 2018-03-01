package com.google.style.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Method;

/**
 * @author liangz
 * @date 2018/2/28 16:02
 * 这里实现CachingConfigurerSupport主要是方便使用自定义keyGenerator
 *
 **/
@Slf4j
@Configuration
@EnableCaching // 启用缓存
public class RedisCacheCfg extends CachingConfigurerSupport {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 配置redis缓存管理对象
     * @return
     */
    @Bean(name = "cacheManager")
    public CacheManager cacheManager() {
        log.info("------------初始化CacheManager--------------");
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        Map<String, Long> expires = new HashMap<>();
//        expires.put("cache:user", 36000L);
//        cacheManager.setExpires(expires);
        //设置缓存过期时间
        //cacheManager.setDefaultExpiration(10000);
        return cacheManager;
    }

    /**
     * 生成key的策略
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

}
