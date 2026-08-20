package com.verify_x.exception;

public class DuplicateCandidateException
        extends RuntimeException {

    public DuplicateCandidateException(String message) {
        super(message);
    }
}