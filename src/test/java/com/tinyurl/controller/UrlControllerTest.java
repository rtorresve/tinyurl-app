package com.tinyurl.controller;

import com.tinyurl.domain.usecase.GetLongUrlUseCase;
import com.tinyurl.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(getLongUrlUseCase.getLongUrl(shortUrl)).thenReturn(longUrl);

        urlController.getLongUrl(shortUrl, response);

        verify(response, times(1)).sendRedirect(longUrl);
        verify(getLongUrlUseCase, times(1)).getLongUrl(shortUrl);
    }

    @Test
    void testGetLongUrlNotFound() throws IOException {
        String shortUrl = "shortUrlExample";
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(getLongUrlUseCase.getLongUrl(shortUrl)).thenThrow(new UrlNotFoundException("Short URL not found"));

        urlController.getLongUrl(shortUrl, response);

        verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found");
        verify(getLongUrlUseCase, times(1)).getLongUrl(shortUrl);
    }
}