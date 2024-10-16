package com.tinyurl.controller;

import com.tinyurl.domain.usecase.GetLongUrlUseCase;
import com.tinyurl.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UrlControllerTest {

    @Mock
    private GetLongUrlUseCase getLongUrlUseCase;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLongUrlSuccess() throws IOException {
        String shortUrl = "shortUrlExample";
        String longUrl = "http://example.com/long-url";
        RedirectView response;

        when(getLongUrlUseCase.getLongUrl(shortUrl)).thenReturn(longUrl);

        response = urlController.getLongUrl(shortUrl);

        assertEquals(longUrl, response.getUrl());
    }

    @Test
    void testGetLongUrlNotFound() throws IOException {
        String shortUrl = "shortUrlExample";
        RedirectView response;

        when(getLongUrlUseCase.getLongUrl(shortUrl)).thenThrow(new UrlNotFoundException("Short URL not found"));

        response = urlController.getLongUrl(shortUrl);

        assertEquals("/error/404", response.getUrl());
    }
}