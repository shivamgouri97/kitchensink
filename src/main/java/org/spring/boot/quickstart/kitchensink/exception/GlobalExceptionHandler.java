package org.spring.boot.quickstart.kitchensink.exception;

import lombok.extern.slf4j.Slf4j;
import org.spring.boot.quickstart.kitchensink.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseBody
    public Message handleMemberNotFoundException(Exception ex) {
        log.error("Handling MemberNotFoundException: ", ex);
        return new Message(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomValidationException.class)
    @ResponseBody
    public Message handleConstraintViolationException(Exception ex) {
        log.error("Handling CustomValidationException: ", ex);
        return new Message(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Message handleCommonException(Exception ex) {
        log.error("Handling Common Exception: ", ex);
        return new Message(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}