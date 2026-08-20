package com.verify_x.exception;


public class RelievingLetterProcessingException
        extends RuntimeException {


    public RelievingLetterProcessingException() {
        super();
    }


    public RelievingLetterProcessingException(String message) {
        super(message);
    }


    public RelievingLetterProcessingException(Throwable cause) {
        super(cause);
    }


    public RelievingLetterProcessingException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
