package com.tinyurl.domain.usecase;

import java.util.Random;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;

public class CreateShortUrlUseCase {

    private final UrlRepository urlRepository;
    private final RedisUrlRepository redisUrlRepository;

    public CreateShortUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository) {
        this.urlRepository = urlRepository;
        this.redisUrlRepository = redisUrlRepository;
    }

    public Url createShortUrl(String longUrl) {
        // Generate a 7-character alphanumeric hash
        String shortUrl = generateShortUrl();

        // Create Url object with the long and short URLs
        Url url = new Url();
        url.setLongUrl(longUrl);
        url.setShortUrl(shortUrl);

        // Save the Url in the repository
        urlRepository.save(url);
        redisUrlRepository.saveUrl(shortUrl, longUrl);

        return url;
    }

    private String generateShortUrl() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortUrl = new StringBuilder();
        Random rnd = new Random();
        while (shortUrl.length() < 7) {
            shortUrl.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return shortUrl.toString();
    }
}