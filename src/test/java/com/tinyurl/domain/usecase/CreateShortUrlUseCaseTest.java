package com.tinyurl.domain.usecase;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CreateShortUrlUseCaseTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private CreateShortUrlUseCase createShortUrlUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateShortUrlSuccess() {
        String longUrl = "http://example.com/long-url";
        Url result = createShortUrlUseCase.createShortUrl(longUrl);

        assertEquals(7, result.getShortUrl().length());
        verify(urlRepository, times(1)).save(any(Url.class));
    }
}