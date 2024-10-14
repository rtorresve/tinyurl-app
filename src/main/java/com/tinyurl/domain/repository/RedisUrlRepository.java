package com.tinyurl.domain.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import com.tinyurl.domain.model.Url;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisUrlRepository {

    private final StringRedisTemplate redisTemplate;
    private final long ttl;

    public RedisUrlRepository(StringRedisTemplate redisTemplate, @Value("${redis.ttl}") long ttl) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
    }

    public void saveUrl(Url url) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        this.saveUrl(url.getShortUrl(), url.getLongUrl());
    }

    public void saveUrl(String shortUrl, String longUrl) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(shortUrl, longUrl, ttl, TimeUnit.SECONDS);
    }

    public String getUrl(String shortUrl) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(shortUrl);
    }

    public void updateTtl(String shortUrl, long newTtlValue) {
        redisTemplate.expire(shortUrl, newTtlValue, TimeUnit.SECONDS);
    }
}