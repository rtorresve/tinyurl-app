package com.tinyurl.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyurl.domain.repository.RedisUrlRepository;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.domain.usecase.GetLongUrlUseCase;
import com.tinyurl.infraestructure.config.ZooKeeperConfig;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateShortUrlUseCase createShortUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository, ZooKeeperConfig zooKeeperConfig) {
        return new CreateShortUrlUseCase(urlRepository, redisUrlRepository, zooKeeperConfig);
    }

    @Bean
    public GetLongUrlUseCase getLongUrlUseCase(UrlRepository urlRepository, RedisUrlRepository redisUrlRepository) {
        return new GetLongUrlUseCase(urlRepository, redisUrlRepository);
    }
}