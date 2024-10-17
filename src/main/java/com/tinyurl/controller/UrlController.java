package com.tinyurl.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.usecase.CreateShortUrlUseCase;
import com.tinyurl.domain.usecase.GetLongUrlUseCase;
import com.tinyurl.exceptions.ErrorDetails;
import com.tinyurl.exceptions.ShortUrlCreationException;
import com.tinyurl.exceptions.UrlNotFoundException;
import com.tinyurl.utils.UrlRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/tiny")
public class UrlController {

    private final CreateShortUrlUseCase createShortUrlUseCase;
    private final GetLongUrlUseCase getLongUrlUseCase;

    public UrlController(CreateShortUrlUseCase createShortUrlUseCase, GetLongUrlUseCase getLongUrlUseCase) {
        this.createShortUrlUseCase = createShortUrlUseCase;
        this.getLongUrlUseCase = getLongUrlUseCase;
    }

    @Operation(summary = "Create a tiny url", description = "Return a Url object eith the long and shor url.")
    @ApiResponse(responseCode = "200", description = "Successfully created tiny url")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/create-url")
    @CircuitBreaker(name = "createShortUrl", fallbackMethod = "fallbackCreateShortUrl")
    public ResponseEntity<?> createUrl(@RequestBody UrlRequest request) {
        Url shortUrl;
        try {
            request.validateUrl();
            shortUrl = createShortUrlUseCase.createShortUrl(request.getUrl());
        } catch (ShortUrlCreationException e) {
            ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                e.getMessage(),
                null
            );
            return ResponseEntity.internalServerError().body(errorDetails);
        }
        return ResponseEntity.ok(shortUrl);
    }

    @Operation(summary = "Receive a tiny url", description = "Redirect to real url.")
    @ApiResponse(responseCode = "302", description = "Redirecto to original url.")
    @GetMapping("/{shortUrl}")
    public RedirectView getLongUrl(@PathVariable String shortUrl) {
        RedirectView redirectView = new RedirectView();
        try {
            String longUrl = getLongUrlUseCase.getLongUrl(shortUrl);
            redirectView.setStatusCode(HttpStatus.FOUND);
            redirectView.setUrl(longUrl);
        } catch (UrlNotFoundException e) {
            redirectView.setUrl("/error/404");
        }
        return redirectView;
    }

    public ResponseEntity<?> fallbackCreateShortUrl(UrlRequest request, Throwable t) {
        ErrorDetails errorDetails = new ErrorDetails(
            LocalDateTime.now(),
            "Service is currently unavailable. Please try again later.",
            t.getMessage()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.SERVICE_UNAVAILABLE);
    }

}