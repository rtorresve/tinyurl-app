package com.tinyurl.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.utils.UrlRequest;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final CreateShortUrlUseCase createShortUrlUseCase;

    public UrlController(CreateShortUrlUseCase createShortUrlUseCase) {
        this.createShortUrlUseCase = createShortUrlUseCase;
    }

    @PostMapping("/create-url")
    public ResponseEntity<Url> createUrl(@RequestBody UrlRequest request) {
        Url shortUrl = createShortUrlUseCase.createShortUrl(request.getUrl());
        return ResponseEntity.ok(shortUrl);
    }

}