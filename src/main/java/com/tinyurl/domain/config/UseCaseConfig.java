package com.tinyurl.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.domain.usecase.GetLongUrlUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateShortUrlUseCase createShortUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository) {
        return new CreateShortUrlUseCase(urlRepository, redisUrlRepository);
    }

    @Bean
    public GetLongUrlUseCase getLongUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository) {
        return new GetLongUrlUseCase(urlRepository, redisUrlRepository);
    }
}