package org.spring.boot.quickstart.kitchensink.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.spring.boot.quickstart.kitchensink.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;


import java.util.HashMap;
import java.util.Map;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.debug("Handling ConstraintViolationException. Violations found: {}", ex.getConstraintViolations().size());

        Map<String, String> responseObj = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        log.info("Handling ValidationException: {}", ex.getMessage());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex, WebRequest request) {
        log.error("Handling Exception: ", ex);

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseBody
    public Message handleMemberNotFoundException(Exception ex) {
        log.error("Handling MemberNotFoundException: ", ex);
        Message message = new Message(ex.getMessage(), HttpStatus.NOT_FOUND);
        return message;
    }


//    @ExceptionHandler(EmployeeNotFoundException.class)
//    @ResponseBody
//    //@ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorMessage employeeNotFoundHandler(EmployeeNotFoundException exception) {
//        ErrorMessage message
//                = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
//        return message;
//    }
}