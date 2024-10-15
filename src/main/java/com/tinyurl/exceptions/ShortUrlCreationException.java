package com.tinyurl.exceptions;

public class ShortUrlCreationException extends Exception {
    public ShortUrlCreationException(String message) {
        super(message);
    }

    public ShortUrlCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}