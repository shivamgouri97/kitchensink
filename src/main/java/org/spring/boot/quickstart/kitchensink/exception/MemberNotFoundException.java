package org.spring.boot.quickstart.kitchensink.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super();
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

}
