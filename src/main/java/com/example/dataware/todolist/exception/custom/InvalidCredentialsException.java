package com.example.dataware.todolist.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {
    private final int statusCode = HttpStatus.BAD_REQUEST.value();
    private final String errorReasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
