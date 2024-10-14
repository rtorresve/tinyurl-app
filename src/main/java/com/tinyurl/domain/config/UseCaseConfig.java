package com.tinyurl.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateShortUrlUseCase createShortUrlUseCase(UrlRepository messageRepository) {
        return new CreateShortUrlUseCase(messageRepository);
    }
}