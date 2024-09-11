package org.spring.boot.quickstart.kitchensink.exception;


public class CustomValidationException extends RuntimeException {
    public CustomValidationException() {
        super();
    }

    public CustomValidationException(String message) {
        super(message);
    }
}
