package com.tinyurl.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.domain.usecase.GetLongUrlUseCase;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateShortUrlUseCase createShortUrlUseCase(UrlRepository urlRepository) {
        return new CreateShortUrlUseCase(urlRepository);
    }

    @Bean
    public GetLongUrlUseCase getLongUrlUseCase(UrlRepository urlRepository) {
        return new GetLongUrlUseCase(urlRepository);
    }
}