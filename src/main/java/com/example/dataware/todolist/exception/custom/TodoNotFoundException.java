package com.example.dataware.todolist.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class TodoNotFoundException extends RuntimeException implements BaseCustomException {
    private final int statusCode = HttpStatus.NOT_FOUND.value();
    private final String errorReasonPhrase = HttpStatus.NOT_FOUND.getReasonPhrase();

    public TodoNotFoundException(String message) {
        super(message);
    }

}
