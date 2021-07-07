package com.example.appliance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidApplianceSecretException extends RuntimeException {

    public InvalidApplianceSecretException(String message) {
        super(message);
    }

}
