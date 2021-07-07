package com.example.appliance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ApplianceNotFoundException extends RuntimeException {

    public ApplianceNotFoundException(String message) {
        super(message);
    }

}
