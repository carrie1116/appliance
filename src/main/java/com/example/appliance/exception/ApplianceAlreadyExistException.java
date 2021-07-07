package com.example.appliance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class ApplianceAlreadyExistException extends RuntimeException {

    public ApplianceAlreadyExistException(String message) {
        super(message);
    }

}
