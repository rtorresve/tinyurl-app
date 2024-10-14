package com.tinyurl.domain.usecase;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetLongUrlUseCaseTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private GetLongUrlUseCase getLongUrlUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLongUrlSuccess() {
        String shortUrl = "shortUrlExample";
        String longUrl = "http://example.com/long-url";
        Url url = new Url(shortUrl, longUrl);

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(url);

        String result = getLongUrlUseCase.getLongUrl(shortUrl);

        assertEquals(longUrl, result);
        verify(urlRepository, times(1)).findByShortUrl(shortUrl);
    }

    @Test
    void testGetLongUrlNotFound() {
        String shortUrl = "shortUrlExample";

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(null);

        assertThrows(UrlNotFoundException.class, () -> {
            getLongUrlUseCase.getLongUrl(shortUrl);
        });

        verify(urlRepository, times(1)).findByShortUrl(shortUrl);
    }
}