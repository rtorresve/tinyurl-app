package com.tinyurl.utils;

import java.net.MalformedURLException;
import java.net.URL;

import com.tinyurl.exceptions.ShortUrlCreationException;

public class UrlRequest {
    private String url;

    // Getters y Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void validateUrl() throws ShortUrlCreationException {
        try {
            new URL(this.url);
        } catch (MalformedURLException e) {
            throw new ShortUrlCreationException("Invalid URL: " + url, e);
        }
    }
}
