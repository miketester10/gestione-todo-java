package com.example.dataware.todolist.exception.custom;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class S3UploadException extends RuntimeException implements BaseCustomException {
    private final int statusCode = HttpStatus.BAD_GATEWAY.value();
    private final String errorReasonPhrase = HttpStatus.BAD_GATEWAY.getReasonPhrase();

    public S3UploadException(String message) {
        super(message);
    }

}