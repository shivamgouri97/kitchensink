package org.spring.boot.quickstart.kitchensink.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String message;
    private HttpStatus status;

}