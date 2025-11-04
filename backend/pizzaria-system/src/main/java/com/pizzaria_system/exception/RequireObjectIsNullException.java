package com.pizzaria_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequireObjectIsNullException extends RuntimeException {
    public RequireObjectIsNullException(String categoryNotFound) {
        super("It is not allowed to persist a null object");
    }
}
