package com.verify_x.exception;

public class OcrProcessingException extends RuntimeException {
    public OcrProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    public OcrProcessingException(String message) {
        super(message);
    }
}