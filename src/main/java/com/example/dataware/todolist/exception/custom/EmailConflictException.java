package com.example.dataware.todolist.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EmailConflictException extends RuntimeException {
    private final int statusCode = HttpStatus.CONFLICT.value();
    private final String errorReasonPhrase = HttpStatus.CONFLICT.getReasonPhrase();

    public EmailConflictException(String message) {
        super(message);
    }

}
