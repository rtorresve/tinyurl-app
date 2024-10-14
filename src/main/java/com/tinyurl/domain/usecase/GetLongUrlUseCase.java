package com.tinyurl.domain.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.UrlNotFoundException;

@Service
public class GetLongUrlUseCase {

    private final UrlRepository urlRepository;

    private final RedisUrlRepository redisUrlRepository;

    @Value("${redis.ttl}")
    private long redisTtl;

    public GetLongUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository) {
        this.urlRepository = urlRepository;
        this.redisUrlRepository = redisUrlRepository;
    }

    public String getLongUrl(String shortUrl) {
        String longUrl = redisUrlRepository.getUrl(shortUrl);
        if (longUrl != null) {
            redisUrlRepository.updateTtl(shortUrl, redisTtl);
            return longUrl;
        }
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url != null) {
            redisUrlRepository.saveUrl(url);
            return url.getLongUrl();
        }
        throw new UrlNotFoundException("Short URL not found:" + shortUrl);
    }
}