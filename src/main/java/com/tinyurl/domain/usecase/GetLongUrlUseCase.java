package com.tinyurl.domain.usecase;

import org.springframework.stereotype.Service;

import com.tinyurl.domain.model.Url;
import com.tinyurl.domain.repository.UrlRepository;
import com.tinyurl.exceptions.UrlNotFoundException;

@Service
public class GetLongUrlUseCase {

    private final UrlRepository urlRepository;

    public GetLongUrlUseCase(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String getLongUrl(String shortUrl) {
        Url url = urlRepository.findByShortUrl(shortUrl);
        if (url != null) {
            return url.getLongUrl();
        }
        throw new UrlNotFoundException("Short URL not found:" + shortUrl);
    }
}