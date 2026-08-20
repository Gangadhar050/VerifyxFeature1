package com.verify_x.exception;

public class CandidateNotFoundException
        extends RuntimeException {

    public CandidateNotFoundException(String message) {
        super(message);
    }
}