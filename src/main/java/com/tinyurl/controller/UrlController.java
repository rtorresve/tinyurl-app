package com.tinyurl.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.domain.usecase.GetLongUrlUseCase;
import com.tinyurl.exceptions.UrlNotFoundException;
import com.tinyurl.utils.UrlRequest;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class UrlController {

    private final CreateShortUrlUseCase createShortUrlUseCase;
    private final GetLongUrlUseCase getLongUrlUseCase;

    public UrlController(CreateShortUrlUseCase createShortUrlUseCase, GetLongUrlUseCase getLongUrlUseCase) {
        this.createShortUrlUseCase = createShortUrlUseCase;
        this.getLongUrlUseCase = getLongUrlUseCase;
    }

    @PostMapping("/create-url")
    public ResponseEntity<Url> createUrl(@RequestBody UrlRequest request) {
        Url shortUrl = createShortUrlUseCase.createShortUrl(request.getUrl());
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public void getLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        try {
            String longUrl = getLongUrlUseCase.getLongUrl(shortUrl);
            response.sendRedirect(longUrl);
        } catch (UrlNotFoundException | IOException e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Short URL not found");
        }
    }

}