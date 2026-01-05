package com.example.dataware.todolist.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class EmptyFileException extends RuntimeException implements BaseCustomException {
    private final int statusCode = HttpStatus.BAD_REQUEST.value();
    private final String errorReasonPhrase = HttpStatus.BAD_REQUEST.getReasonPhrase();

    public EmptyFileException(String message) {
        super(message);
    }

}
